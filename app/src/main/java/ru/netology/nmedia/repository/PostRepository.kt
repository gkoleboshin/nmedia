package ru.netology.nmedia.repository

import androidx.lifecycle.LiveData
import ru.netology.nmedia.dto.Post
import javax.security.auth.callback.Callback

interface PostRepository {
    val data: LiveData<List<Post>>
    suspend fun getAll()
    suspend fun save(post: Post, saveDAO: Boolean)
    suspend fun removeById(id: Long, saveDAO: Boolean)
    suspend fun likeByIdById(post: Post, saveDAO: Boolean)
}
