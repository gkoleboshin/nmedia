package ru.netology.nmedia.repository

import android.app.Application
import android.content.Context
import androidx.core.content.edit
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import ru.netology.nmedia.dto.Post
import kotlin.properties.Delegates

class PostRepositorySharedPrefsImpl(application: Application) : PostRepository {

    // region Gson config

    private val gson = Gson()
    private val type = TypeToken.getParameterized(
        List::class.java, Post::class.java
    ).type

    // endregion

    private val prefs = application.getSharedPreferences(
        "repo", Context.MODE_PRIVATE
    )


    private var nextId by Delegates.observable(
        prefs.getLong(SHARED_PREFS_NEXT_ID_KEY, 1L)
    ) { _, _, nextValue ->
        prefs.edit {
            putLong(SHARED_PREFS_NEXT_ID_KEY, nextValue)
        }
    }


    private var posts
        get() = checkNotNull(data.value) {
            "Posts should be always not null"
        }
        set(value) {
            data.value = value
            prefs.edit {
                putString(SHARED_PREFS_POST_KEY, gson.toJson(value))
            }
        }


    override val data by lazy {
        val serializedPosts = prefs.getString(SHARED_PREFS_POST_KEY, null)
        val posts: List<Post> = if (serializedPosts != null) {
            gson.fromJson(serializedPosts, type)
        } else emptyList()

        MutableLiveData(posts)
    }


    override fun likeById(id: Long) {
        posts = posts.map {
            if (it.id != id) it else it.copy(
                likedByMe = !it.likedByMe,
                likes = if (it.likedByMe) it.likes - 1 else it.likes + 1
            )
        }
    }


    override fun shareById(id: Long) {
        posts = posts.map {
            if (it.id != id) it else it.copy(shares = it.shares + 1)
        }
    }

    override fun removeById(id: Long) {
        posts = posts.filterNot { it.id == id }
    }

    override fun save(post: Post) = if (post.id == NEW_POST_ID) insert(post) else update(post)

    override fun findPostById(id: Long): Post {
        return posts.first { it.id == id }
    }

    override fun viewById(id: Long) {
        posts= posts.map{
            if (it.id != id) it else it.copy(views = it.views+1)
        }
    }

    private fun insert(post: Post) {
        val indentifiedPost = post.copy(id = nextId++)
        posts = listOf(indentifiedPost) + posts
    }

    private fun update(post: Post) {
        posts = posts.map {
            if (it.id == post.id) post else it
        }
    }


    private companion object {
        const val NEW_POST_ID = 0L
        const val SHARED_PREFS_POST_KEY = "posts"
        const val SHARED_PREFS_NEXT_ID_KEY = "postsId"
    }
}
