package ru.netology.nmedia.dto

data class Post(
    val id: Long,
    val author: String,
    val content: String,
    val published: String,
    val likedByMe: Boolean,
    var likes: Int = 0,
    var shares: Int = 0,
    var views:Int = 0
)
