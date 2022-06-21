package ru.netology.nmedia.repository

import androidx.lifecycle.LiveData
import ru.netology.nmedia.dto.Post
import javax.security.auth.callback.Callback

interface PostRepository {

    fun getAllAsync(callback: Callback<List<Post>>)
    fun saveAsync(post: Post,callback: Callback<Post>)
    fun removeByIdAsync(id: Long,callback: Callback<Unit>)
    fun likeByIdByIdAsync(post: Post,callback: Callback<Post>)


    interface Callback<T> {
        fun onSuccess(posts:T) {}
        fun onError(e: Exception) {}
    }

}
