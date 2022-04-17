package ru.netology.nmedia.adapter

import ru.netology.nmedia.dto.Post

interface PostInteractionListiner {
    fun onLike(post: Post)
    fun onShare(post: Post)
    fun onRemove(post:Post)
    fun onEdit(post: Post)
    fun onCancel()
}