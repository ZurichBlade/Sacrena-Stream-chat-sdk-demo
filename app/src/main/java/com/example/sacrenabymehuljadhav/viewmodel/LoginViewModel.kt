package com.example.sacrenabymehuljadhav.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sacrenabymehuljadhav.ChatHelper
import com.example.sacrenabymehuljadhav.data.UserCredentials
import dagger.hilt.android.lifecycle.HiltViewModel
import io.getstream.chat.android.client.ChatClient
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class LoginViewModel @Inject constructor(
    private val chatHelper: ChatHelper
) : ViewModel() {

    private val _navigationEvent = MutableLiveData<NavigationEvent>()
    val navigationEvent: LiveData<NavigationEvent> get() = _navigationEvent

    fun onUserCredentialsClick(userCredentials: UserCredentials) {
        viewModelScope.launch {
            if (ChatClient.instance().config.apiKey != userCredentials.apiKey) {
                chatHelper.initializeSdk(userCredentials.apiKey)
            }
            chatHelper.connectUser(userCredentials = userCredentials,
                onSuccess = {
                    _navigationEvent.value = NavigationEvent.OpenChannels
                },
                onError = {
                    Log.e("LoginViewModel", "Error:")
                }
            )
        }
    }

    fun onCustomLoginClick() {
        _navigationEvent.value = NavigationEvent.OpenCustomLogin
    }

    sealed class NavigationEvent {
        object OpenChannels : NavigationEvent()
        object OpenCustomLogin : NavigationEvent()
    }

}