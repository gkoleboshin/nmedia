package ru.netology.nmedia.db.dao

import ru.netology.nmedia.dto.Post

interface PostsDao {
    fun getAll():List<Post>
    fun insert(post:Post):Post
    fun update(post:Post):Post
    fun likeById(id:Long)
    fun removeById(id:Long)
    fun shareById(id:Long)
    fun findPostById(id:Long):Post
    fun viewById(id:Long)
}