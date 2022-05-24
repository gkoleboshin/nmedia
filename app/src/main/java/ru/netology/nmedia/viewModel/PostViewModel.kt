package ru.netology.nmedia.viewmodel


import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import ru.netology.nmedia.adapter.PostInteractionListiner
import ru.netology.nmedia.db.AppDb
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.repository.PostRepositoryFileImpl
import ru.netology.nmedia.repository.PostRepositorySQLLiteImpl
import ru.netology.nmedia.util.SingleLiveEvent

class PostViewModel(
    application: Application
) : AndroidViewModel(application), PostInteractionListiner {
    private val repository: PostRepository = PostRepositorySQLLiteImpl(
        AppDb.getInstance(application).postDao
    )
    var curentPost: Post? = null
    val data by repository::data
    val videoUrl = MutableLiveData<String>()
    val shareEvent = SingleLiveEvent<String>()
    val navigeteToNewPostScreen = SingleLiveEvent<String>()
    val navigeteToThisPostScreen = SingleLiveEvent<Long>()

    fun changeContent(postContent: String) {
        val updatedPost = curentPost?.copy(
            content = postContent
        ) ?: Post(
            id = 0L,
            author = "Me",
            content = postContent,
            published = "now",
            likedByMe = false
        )
        repository.save(updatedPost)
        this.curentPost = null

    }

    // region PostInteractionListiner implmentation

    override fun onLike(post: Post) {
        repository.likeById(post.id)
    }

    override fun onShare(post: Post) {
        shareEvent.value = post.content
        repository.shareById(post.id)
    }

    override fun onRemove(post: Post) {
        repository.removeById(post.id)
    }

    override fun onEdit(post: Post) {
        curentPost = post
        navigeteToNewPostScreen.value = post.content
    }

    override fun onCancel() {

    }

    override fun onPlayVideo(post: Post) {
        videoUrl.value = post.contentVideo
    }

    override fun onViewPost(post: Post) {
        repository.viewById(post.id)
        navigeteToThisPostScreen.value = post.id
    }

    // endregion PostInteractionListiner implmentation
}