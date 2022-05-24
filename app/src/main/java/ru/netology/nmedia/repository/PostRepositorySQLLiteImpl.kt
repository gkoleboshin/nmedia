package ru.netology.nmedia.repository

import androidx.lifecycle.MutableLiveData
import ru.netology.nmedia.db.dao.PostsDao
import ru.netology.nmedia.dto.Post

class PostRepositorySQLLiteImpl(
    private val dao: PostsDao
) : PostRepository {
    private var posts = dao.getAll()
    override val data = MutableLiveData(posts)


    override fun likeById(id: Long) {
        dao.likeById(id)
        posts = posts.map {
            if (it.id != id) it else it.copy(
                likedByMe = !it.likedByMe,
                likes = if (it.likedByMe) it.likes - 1 else it.likes + 1
            )
        }
        data.value = posts
    }

    override fun shareById(id: Long) {
        dao.shareById(id)
        posts = posts.map {
            if (it.id != id) it else it.copy(
                shares = it.shares + 1
            )
        }
        data.value = posts
    }

    override fun removeById(id: Long) {
        dao.removeById(id)
        posts = posts.filter { it.id != id }
        data.value = posts
    }

    override fun save(post: Post) {
        val id = post.id
        posts = if (id == 0L) {
            val saved = dao.insert(post)
            listOf(saved) + posts
        } else {
            val update = dao.update(post)
            posts.map {
                if (it.id != id) it else update
            }
        }
        data.value = posts
    }

    override fun viewById(id: Long) {
        dao.viewById(id)
        posts = posts.map {
            if (it.id != id) it else it.copy(
                views = it.views + 1
            )
        }
        data.value = posts
    }


}