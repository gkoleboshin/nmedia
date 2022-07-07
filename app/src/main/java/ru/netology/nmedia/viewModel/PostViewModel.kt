package ru.netology.nmedia.viewmodel


import android.app.Application
import androidx.lifecycle.*
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.model.FeedModel
import ru.netology.nmedia.repository.*
import ru.netology.nmedia.util.SingleLiveEvent
import java.io.IOException

private val empty = Post(
    id = 0,
    content = "",
    author = "",
    authorAvatar = "netology.jpg",
    likedByMe = false,
    likes = 0,
    published = 0
)

class PostViewModel(application: Application) : AndroidViewModel(application) {
    // упрощённый вариант
    private val repository: PostRepository = PostRepositoryImpl()
    private val _data = MutableLiveData(FeedModel())
    private var lastIdRemove: Long? = null
    private var lastPost: Post? = null
    private var lastAction: ActionType? = null

    val data: LiveData<FeedModel>
        get() = _data
    val edited = MutableLiveData(empty)
    private val _postCreated = SingleLiveEvent<Unit>()
    val postCreated: LiveData<Unit>
        get() = _postCreated

    init {
        loadPosts()
    }

    fun loadPosts() {
        lastAction = ActionType.LOAD
        _data.postValue(FeedModel(loading = true))
        repository.getAllAsync(object : PostRepository.Callback<List<Post>> {

            override fun onSuccess(posts: List<Post>) {
                lastAction = null
                _data.postValue(FeedModel(posts = posts, empty = posts.isEmpty()))
            }

            override fun onError(e: Exception) {
                _data.postValue(FeedModel(error = true))
            }
        })
    }


    fun save() {

        edited.value?.let {
            lastPost = it
            lastAction = ActionType.SAVE
            repository.saveAsync(it, object : PostRepository.Callback<Post> {
                override fun onSuccess(posts: Post) {
                    lastAction = null
                    lastPost = null
                    _postCreated.postValue(Unit)
                }

                override fun onError(e: Exception) {
                    _data.postValue(FeedModel(error = true))
                }
            })
        }
        edited.value = empty
    }

    fun edit(post: Post) {
        edited.value = post
    }

    fun changeContent(content: String) {
        val text = content.trim()
        if (edited.value?.content == text) {
            return
        }
        edited.value = edited.value?.copy(content = text)
    }

    fun likeById(post: Post) {
        lastAction = ActionType.LIKE
        lastPost = post
        repository.likeByIdByIdAsync(post, object : PostRepository.Callback<Post> {
            override fun onSuccess(posts: Post) {
                lastPost = null
                lastAction = null
                val postsUp = _data.value?.posts?.map {
                    if (it.id != post.id) it else posts
                } ?: return
                _data.postValue(FeedModel(posts = postsUp, error = false))
            }

            override fun onError(e: Exception) {
                retry()
            }
        })
    }

    fun removeById(id: Long) {
        // Оптимистичная модель

        lastAction = ActionType.REMOVE
        lastIdRemove = id
        val old = _data.value?.posts.orEmpty()
        _data.postValue(
            _data.value?.copy(posts = _data.value?.posts.orEmpty()
                .filter { it.id != id }
            )
        )
        try {
            repository.removeByIdAsync(id, object : PostRepository.Callback<Unit> {
                override fun onError(e: Exception) {
                    _data.postValue(FeedModel(error = true))
                }
            })
        } catch (e: IOException) {
            _data.postValue(_data.value?.copy(posts = old))
        }

    }

    fun retrySave() {
        lastPost?.let {
            edited.value = it
            save()
        }
    }

    fun retryLike() {
        lastPost?.let {
            likeById(it)
        }
    }

    fun retryRemove() {
        lastIdRemove?.let {
            removeById(it)
        }
    }


    fun retry() {
        when (lastAction) {
            ActionType.LIKE -> retryLike()
            ActionType.REMOVE -> retryRemove()
            ActionType.SAVE -> retrySave()
            ActionType.LOAD -> loadPosts()
        }
    }
}

enum class ActionType {
    REMOVE,
    LIKE,
    SAVE,
    LOAD
}
