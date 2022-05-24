package ru.netology.nmedia.repository

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import ru.netology.nmedia.db.dao.PostDao
import ru.netology.nmedia.db.map.toEntity
import ru.netology.nmedia.db.map.toModel
import ru.netology.nmedia.dto.Post

class PostRepositoryImpl(private val postDao: PostDao) : PostRepository {

    private var nextId = 1L

    override val data = postDao.getAll().map { entities ->
        entities.map { it.toModel() }
    }

    override fun save(post: Post) = if (post.id == 0L) insert(post) else update(post)



    override fun likeById(id: Long) {
        postDao.likeById(id)
    }


    override fun shareById(id: Long) {
        postDao.shareById(id)
    }

    override fun removeById(id: Long) {
        postDao.removeById(id)
    }

    override fun viewById(id: Long) {
        postDao.viewById(id)
    }


    private fun insert(post: Post) {
       postDao.insert(post.toEntity())
    }

    private fun update(post: Post) {
       postDao.update(post.toEntity())
    }


}