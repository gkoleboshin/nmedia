package ru.netology.nmedia.viewmodel

import android.app.Application
import androidx.lifecycle.*
import kotlinx.coroutines.launch
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.auth.AuthLogin
import ru.netology.nmedia.auth.AuthState
import ru.netology.nmedia.db.AppDb
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.repository.PostRepositoryImpl

class SignInViewModel(application: Application) : AndroidViewModel(application) {
    val data: MutableLiveData<AuthLogin> = MutableLiveData<AuthLogin>()
    val _data: MutableLiveData<AuthState> = MutableLiveData<AuthState>()
    val repository: PostRepository =
        PostRepositoryImpl(AppDb.getInstance(context = application).postDao())

    fun Login(login: String, password: String){
        val authState:AuthState? = null
        viewModelScope.launch {
            try {
                val signIn = AuthLogin(login, password)
                data.value = signIn
               _data.value = repository.auth(data.value!!)
            } catch (e: Exception) {
                _data.value = null
            }
        }
    }


}