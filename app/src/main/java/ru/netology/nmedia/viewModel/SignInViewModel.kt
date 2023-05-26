package ru.netology.nmedia.viewmodel

import android.app.Application
import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.auth.AuthLogin
import ru.netology.nmedia.auth.AuthState
import ru.netology.nmedia.db.AppDb
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.repository.PostRepositoryImpl
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(private val repository: PostRepository) : ViewModel() {
    val data: MutableLiveData<AuthLogin> = MutableLiveData<AuthLogin>()
    val _data: MutableLiveData<AuthState> = MutableLiveData<AuthState>()
    fun Login(login: String, password: String){
        val authState:AuthState? = null
        viewModelScope.launch {
            try {
                val signIn = AuthLogin(login, password)
                data.value = signIn
               _data.value = repository.auth(data.value!!)
            } catch (e: Exception){
            }
        }
    }
}
