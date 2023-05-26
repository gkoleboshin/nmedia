package ru.netology.nmedia.repository


import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import ru.netology.nmedia.entity.toDto
import ru.netology.nmedia.api.PostsApiService
import ru.netology.nmedia.auth.AuthLogin
import ru.netology.nmedia.auth.AuthState
import ru.netology.nmedia.dao.PostDao
import ru.netology.nmedia.dto.*
import ru.netology.nmedia.entity.PostEntity
import ru.netology.nmedia.entity.toEntity
import ru.netology.nmedia.error.ApiError
import ru.netology.nmedia.error.AppError
import ru.netology.nmedia.error.NetworkError
import ru.netology.nmedia.error.UnknownError
import java.io.IOException
import javax.inject.Inject


class PostRepositoryImpl @Inject constructor(
    private val dao: PostDao,
    val service: PostsApiService
) : PostRepository {
    override val data = dao.getAll().map(List<PostEntity>::toDto)
        .flowOn(Dispatchers.Default)

    override fun getNewer(id: Long): Flow<Int> = flow {
        while (true) {
            try {
                delay(10_000)
                val response = service.getNewer(id)
                if (!response.isSuccessful) {
                    throw ApiError(response.code(), response.message())
                }
                val body = response.body() ?: throw ApiError(response.code(), response.message())
                dao.insert(body.map { it.copy(show = false) }.toEntity())
                emit(body.size)
            } catch (e: IOException) {
                throw NetworkError
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                throw UnknownError
            }
        }
    }
        .flowOn((Dispatchers.Default))

    override suspend fun showNewer() {
        val updatePosts = dao.getAll().map { posts ->
            posts.toDto().map { post ->
                post.copy(show = true)
            }.toEntity()
        }
        dao.insert(updatePosts.first())
    }


    override suspend fun getAll() {
        try {
            val response = service.getAll()
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            val body = response.body() ?: throw ApiError(response.code(), response.message())
            dao.insert(body.map { it.copy(show = true) }.toEntity())
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun save(post: Post, saveDAO: Boolean) {
        try {
            val response = service.save(post)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            val body = response.body() ?: throw ApiError(response.code(), response.message())
            if (saveDAO) {
                dao.insert(PostEntity.fromDto(body))
            }
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun saveWithAttachment(post: Post, upload: MediaUpload) {
        try {
            val media = upload(upload)
            val postWithAttachment =
                post.copy(attachment = Attachment(media.id, AttachmentType.IMAGE))
            save(postWithAttachment, true)
        } catch (e: AppError) {
            throw e
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun removeById(id: Long, saveDAO: Boolean) {
        if (saveDAO) {
            dao.removeById(id)
        }
        try {
            val response = service.removeById(id)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun likeByIdById(post: Post, saveDAO: Boolean) {
        if (post.likedByMe) {
            if (saveDAO) {
                val upPost = post.copy(likedByMe = !post.likedByMe, likes = post.likes - 1)
                dao.insert(upPost.toEntity())
            }
            try {
                val response = service.dislikedById(post.id)
                if (!response.isSuccessful) {
                    throw ApiError(response.code(), response.message())
                }
            } catch (e: IOException) {
                throw NetworkError
            } catch (e: Exception) {
                throw UnknownError
            }
        } else {
            val upPost = post.copy(likedByMe = !post.likedByMe, likes = post.likes + 1)
            dao.insert(upPost.toEntity())
            try {
                val response = service.likedById(post.id)
                if (!response.isSuccessful) {
                    throw ApiError(response.code(), response.message())
                }
            } catch (e: IOException) {
                throw NetworkError
            } catch (e: Exception) {
                throw UnknownError
            }
        }
    }

    override suspend fun upload(upload: MediaUpload): Media {
        try {
            val media = MultipartBody.Part.createFormData(
                "file", upload.file.name, upload.file.asRequestBody()
            )
            val response = service.upload(media)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            return response.body() ?: throw ApiError(response.code(), response.message())
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun auth(login: AuthLogin): AuthState {
        try {
            val response = service.updateUser(login.login, login.password)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            return response.body() ?: throw ApiError(response.code(), response.message())
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }
}
