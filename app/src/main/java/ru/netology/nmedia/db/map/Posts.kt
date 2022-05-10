package ru.netology.nmedia.db.map

import ru.netology.nmedia.db.entity.PostEntity
import ru.netology.nmedia.dto.Post

internal fun PostEntity.toModel() = Post(
    id = id,
    author = author,
    content = content,
    contentVideo = contentVideo,
    published = published,
    likedByMe = likedByMe,
    likes = likes,
    shares = shares,
    views = views
)

internal fun Post.toEntity()= PostEntity(
    id = id,
    author = author,
    content = content,
    contentVideo = contentVideo,
    published = published,
    likedByMe = likedByMe,
    likes = likes,
    shares = shares,
    views = views
)