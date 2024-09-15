package com.example.sacrenabymehuljadhav.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sacrenabymehuljadhav.ChatHelper
import com.example.sacrenabymehuljadhav.data.UserCredentials
import dagger.hilt.android.lifecycle.HiltViewModel
import io.getstream.chat.android.models.User
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CustomLoginViewModel @Inject constructor(
    private val chatHelper: ChatHelper
) : ViewModel() {

    var apiKey by mutableStateOf("")
    var userId by mutableStateOf("")
    var userToken by mutableStateOf("")
    var userName by mutableStateOf("")

    val isLoginButtonEnabled
        get() = apiKey.isNotEmpty() && userId.isNotEmpty() && userToken.isNotEmpty()

    fun onLoginButtonClick(onSuccess: () -> Unit, onError: (ChatHelper.AppError) -> Unit) {
        val userCredentials = UserCredentials(
            apiKey = apiKey, user = User(id = userId, name = userName), token = userToken
        )

        viewModelScope.launch {
            try {
                chatHelper.initializeSdk(apiKey)
                chatHelper.connectUser(
                    userCredentials = userCredentials, onSuccess = onSuccess, onError = onError
                )
            } catch (e: Exception) {
                onError(ChatHelper.AppErrorImpl(e.message ?: "Unknown error"))
            }
        }
    }
}