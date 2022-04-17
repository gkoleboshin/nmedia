package ru.netology.nmedia.repository

import android.opengl.Visibility
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.netology.nmedia.dto.Post

interface PostRepository {
    val data:LiveData<List<Post>>
    fun likeById(id: Long)
    fun shareById(id:Long)
    fun removeById(id:Long)
    fun save(post:Post)

}