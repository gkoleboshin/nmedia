package ru.netology.nmedia.repository


import androidx.lifecycle.LiveData
import androidx.lifecycle.map
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
    override val data: LiveData<List<Post>> = dao.getAll().map(List<PostEntity>::toDto)


    override suspend fun getAll() {
        try {
            val response = PostApi.service.getAll()
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            val body = response.body() ?: throw ApiError(response.code(), response.message())
            dao.insert(body.toEntity())
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun save(post: Post) {
        try {
            val response = PostApi.service.save(post)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            val body = response.body() ?: throw ApiError(response.code(), response.message())
            dao.insert(PostEntity.fromDto(body))
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun removeById(id: Long) {
        dao.removeById(id)
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

    override suspend fun likeByIdById(post: Post) {
        if (post.likedByMe) {
            val upPost = post.copy(likedByMe = !post.likedByMe, likes = post.likes - 1)
            dao.insert(upPost.toEntity())
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
