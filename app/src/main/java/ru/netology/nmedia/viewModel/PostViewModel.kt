package ru.netology.nmedia.viewmodel

import androidx.constraintlayout.widget.Group
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import ru.netology.nmedia.adapter.PostInteractionListiner
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.repository.PostRepositoryInMemoryImpl

class PostViewModel : ViewModel(), PostInteractionListiner {
    private val repository: PostRepository = PostRepositoryInMemoryImpl()
    val data by repository::data
    val editPost = MutableLiveData<Post?>()
    fun onSaveButtonClicked(postContent: String) {
        val updatedPost = editPost.value?.copy(
            content = postContent
        ) ?: Post(
            id = 0L,
            author = "",
            content = postContent,
            published = "",
            likedByMe = false
        )
        repository.save(updatedPost)
        this.editPost.value = null
    }

    // region PostInteractionListiner implmentation

    override fun onLike(post: Post) {
        repository.likeById(post.id)
    }

    override fun onShare(post: Post) {
        repository.shareById(post.id)
    }

    override fun onRemove(post: Post) {
        repository.removeById(post.id)
    }

    override fun onEdit(post: Post) {
        editPost.value = post
    }

    // endregion PostInteractionListiner implmentation

}