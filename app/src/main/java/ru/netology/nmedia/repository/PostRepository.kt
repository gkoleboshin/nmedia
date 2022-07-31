package ru.netology.nmedia.repository


import kotlinx.coroutines.flow.Flow
import ru.netology.nmedia.dto.Post

interface PostRepository {
    val data: Flow<List<Post>>
    fun getNewer(id: Long): Flow<Int>
    suspend fun showNewer()
    suspend fun getAll()
    suspend fun save(post: Post, saveDAO: Boolean)
    suspend fun removeById(id: Long, saveDAO: Boolean)
    suspend fun likeByIdById(post: Post, saveDAO: Boolean)
}
