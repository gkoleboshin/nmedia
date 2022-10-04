package ru.netology.nmedia.dto

import ru.netology.nmedia.entity.AttachmentEmbeddable
import ru.netology.nmedia.entity.PostEntity

data class Post(
    val id: Long,
    val author: String,
    val authorAvatar: String = "",
    val content: String,
    val published: String,
    val likedByMe: Boolean,
    val likes: Int = 0,
    val show: Boolean = true,
    val attachment: Attachment?= null

)

fun Post.toEntity(): PostEntity = PostEntity(
    id = id,
    author = author,
    authorAvatar = authorAvatar,
    content = content,
    published = published,
    likedById = likedByMe,
    likes = likes,
    show = show,
    attachment = AttachmentEmbeddable.fromDto(attachment)
)


