package ru.netology.nmedia.repository


import androidx.lifecycle.LiveData
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import okhttp3.Dispatcher
import ru.netology.nmedia.entity.toDto
import ru.netology.nmedia.api.PostApi
import ru.netology.nmedia.dao.PostDao
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.dto.toEntity
import ru.netology.nmedia.entity.PostEntity
import ru.netology.nmedia.entity.toEntity
import ru.netology.nmedia.error.ApiError
import ru.netology.nmedia.error.NetworkError
import ru.netology.nmedia.error.UnknownError
import java.io.IOException


class PostRepositoryImpl(private val dao: PostDao) : PostRepository {
    override val data = dao.getAll().map(List<PostEntity>::toDto)
        .flowOn(Dispatchers.Default)

    override fun getNewer(id: Long): Flow<Int> = flow {
        while (true) {
            try {
                delay(10_000)
                val response = PostApi.service.getNewer(id)
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

    override suspend fun showNewer(){
        val updatePosts = dao.getAll().map{posts->
            posts.toDto().map {post->
               post.copy(show = true)
            }.toEntity()
        }
        dao.insert(updatePosts.first())
    }


    override suspend fun getAll() {
        try {
            val response = PostApi.service.getAll()
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
            val response = PostApi.service.save(post)
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

    override suspend fun removeById(id: Long, saveDAO: Boolean) {
        if (saveDAO) {
            dao.removeById(id)
        }
        try {
            val responce = PostApi.service.removeById(id)
            if (!responce.isSuccessful) {
                throw ApiError(responce.code(), responce.message())
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
                val response = PostApi.service.dislikedById(post.id)
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
                val response = PostApi.service.likedById(post.id)
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
}
