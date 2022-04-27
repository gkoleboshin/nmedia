package ru.netology.nmedia.repository

import android.app.Application
import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import ru.netology.nmedia.dto.Post

class PostRepositoryFileImpl(private  val application: Application) : PostRepository {

    // region Gson config

    private val gson = Gson()
    private val type = TypeToken.getParameterized(
        List::class.java, Post::class.java
    ).type

    // endregion


    private var nextId = 1L


    private var posts
        get() = checkNotNull(data.value) {
            "Posts should be always not null"
        }
        set(value) {
            data.value = value
            application.openFileOutput(
                FILE_NAME,Context.MODE_PRIVATE
            ).bufferedWriter().use{bufferedWriter->
                bufferedWriter.write(gson.toJson(value))
            }
        }


    override val data by lazy {
        val file = application.filesDir.resolve(FILE_NAME)
        val posts: List<Post> = if (file.exists()) {
            application.openFileInput(FILE_NAME)
                .bufferedReader()
                .use { bufferedReader ->
                    gson.fromJson(bufferedReader, type)
                }
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
        const val FILE_NAME = "posts.json"
    }
}
