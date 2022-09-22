package ru.netology.nmedia.viewmodel


import android.app.Application
import android.net.Uri
import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import ru.netology.nmedia.db.AppDb
import ru.netology.nmedia.dto.MediaUpload
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.model.FeedModel
import ru.netology.nmedia.model.FeedModelState
import ru.netology.nmedia.model.PhotoModel
import ru.netology.nmedia.repository.*
import ru.netology.nmedia.util.SingleLiveEvent
import java.io.File
import java.io.IOException

private val empty = Post(
    id = 0,
    content = "",
    author = "",
    authorAvatar = "netology.jpg",
    likedByMe = false,
    likes = 0,
    published = ""
)
private val noPhoto = PhotoModel()

class PostViewModel(application: Application) : AndroidViewModel(application) {


    // упрощённый вариант
    private val repository: PostRepository =
        PostRepositoryImpl(AppDb.getInstance(context = application).postDao())
    val data: LiveData<FeedModel> = repository.data.map(::FeedModel)
        .asLiveData(Dispatchers.Default)
    val newerCount: LiveData<Int> = data.switchMap {
        repository.getNewer(it.posts.firstOrNull()?.id ?: 0L)
            .asLiveData(Dispatchers.Default)
    }
        .distinctUntilChanged()
    private val _dataState = MutableLiveData<FeedModelState>()
    val dataState: LiveData<FeedModelState>
        get() = _dataState

    private val edited = MutableLiveData(empty)
    private val _photo = MutableLiveData<PhotoModel?>(null)
    val photo: LiveData<PhotoModel?>
        get() = _photo

    private val _postCreated = SingleLiveEvent<Unit>()
    val postCreated: LiveData<Unit>
        get() = _postCreated
    private var lastIdRemove: Long? = null
    private var lastPost: Post? = null
    private var lastAction: ActionType? = null

    init {
        loadPosts()
    }

    fun loadPosts() = viewModelScope.launch {
        try {
            _dataState.value = FeedModelState(loading = true)
            repository.getAll()
            _dataState.value = FeedModelState()
        } catch (e: Exception) {
            _dataState.value = FeedModelState(error = true)
        }
    }

    fun refreshPosts() = viewModelScope.launch {
        try {
            _dataState.value = FeedModelState(refreshing = true)
            repository.getAll()
            _dataState.value = FeedModelState()
        } catch (e: Exception) {
            _dataState.value = FeedModelState(error = true)
        }
    }


    fun save(saveDAO: Boolean = true) {
        edited.value?.let {
            _postCreated.value = Unit
            viewModelScope.launch {
                try {
                    when(_photo.value) {
                        noPhoto -> repository.save(it,saveDAO)
                        else -> _photo.value?.file?.let { file ->
                            repository.saveWithAttachment(it, MediaUpload(file))
                        }
                    }
                    _dataState.value = FeedModelState()
                } catch (e: Exception) {
                    _dataState.value = FeedModelState(error = true)
                }
            }
        }
        edited.value = empty
        _photo.value = noPhoto
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

    fun changePhoto(uri: Uri?, file: File?) {
        _photo.value = PhotoModel(uri, file)
    }

    fun likeById(post: Post, saveDAO: Boolean = true) {
        viewModelScope.launch {
            try {
                repository.likeByIdById(post, saveDAO)
                _dataState.value = FeedModelState()
                lastAction = null
                lastPost = null
            } catch (e: Exception) {
                lastAction = ActionType.LIKE
                lastPost = post
                _dataState.value = FeedModelState(error = true)
            }
        }
    }

    fun removeById(id: Long, saveDAO: Boolean = true) {
        viewModelScope.launch {
            try {
                repository.removeById(id, saveDAO)
                _dataState.value = FeedModelState()
                lastAction = null
                lastIdRemove = null
            } catch (e: Exception) {
                lastAction = ActionType.REMOVE
                lastIdRemove = id
                _dataState.value = FeedModelState(error = true)
            }
        }
    }

    fun showNewer() {
        viewModelScope.launch {
            repository.showNewer()
        }

    }

    fun retrySave() {
        lastPost?.let {
            edited.value = it
            save(false)
        }
    }

    fun retryLike() {
        lastPost?.let {
            likeById(it, false)
        }
    }

    fun retryRemove() {
        lastIdRemove?.let {
            removeById(it, false)
        }
    }


    fun retry() {
        when (lastAction) {
            ActionType.LIKE -> retryLike()
            ActionType.REMOVE -> retryRemove()
            ActionType.SAVE -> retrySave()
            ActionType.LOAD -> loadPosts()
            else -> 0
        }
    }

}

enum class ActionType {
    REMOVE,
    LIKE,
    SAVE,
    LOAD
}
