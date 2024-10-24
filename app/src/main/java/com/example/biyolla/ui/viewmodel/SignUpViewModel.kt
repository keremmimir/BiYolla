package com.example.biyolla.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.biyolla.Event
import com.example.biyolla.data.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(private val authRepository: AuthRepository) :
    ViewModel() {

    val authResult = MutableLiveData<Event<Result<String>?>>()
    val error = MutableLiveData<Event<String>>()

    fun signUp(
        name: String,
        surname: String,
        username: String,
        email: String,
        password: String
    ) {
        viewModelScope.launch {
            val result = authRepository.signUp(name, surname, username, email, password)
            if (result.isSuccess) {
                authResult.value = Event(result)
            } else {
                error.value = Event(result.exceptionOrNull()?.message ?: "Error")
            }
        }
    }
}