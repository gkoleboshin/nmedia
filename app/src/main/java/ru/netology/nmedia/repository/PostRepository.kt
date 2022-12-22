package ru.netology.nmedia.repository


import android.net.Uri
import kotlinx.coroutines.flow.Flow
import ru.netology.nmedia.auth.AuthLogin
import ru.netology.nmedia.auth.AuthState
import ru.netology.nmedia.dto.Media
import ru.netology.nmedia.dto.MediaUpload
import ru.netology.nmedia.dto.Post
import java.io.File

interface PostRepository {
    val data: Flow<List<Post>>
    fun getNewer(id: Long): Flow<Int>
    suspend fun showNewer()
    suspend fun getAll()
    suspend fun save(post: Post, saveDAO: Boolean)
    suspend fun saveWithAttachment(post: Post, upload: MediaUpload)
    suspend fun removeById(id: Long, saveDAO: Boolean)
    suspend fun likeByIdById(post: Post, saveDAO: Boolean)
    suspend fun upload(upload:MediaUpload): Media
    suspend fun auth(login: AuthLogin):AuthState
}
