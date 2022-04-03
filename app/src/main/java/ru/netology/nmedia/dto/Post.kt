package ru.netology.nmedia.dto

data class Post(
    val id:Long,
    val author:String,
    val content:String,
    val published:String,
    val views:Int = 0,
    val likedByMe:Boolean = false,
    val likes:Int=0,
    val share:Int=0
)