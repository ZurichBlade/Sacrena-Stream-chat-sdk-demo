package com.example.sacrenabymehuljadhav.viewmodel

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sacrenabymehuljadhav.BaseClass
import com.example.sacrenabymehuljadhav.ChatHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StartupViewModel @Inject constructor(
    private val chatHelper: ChatHelper
) : ViewModel() {

    private val _navigationEvent = MutableLiveData<NavigationEvent>()
    val navigationEvent: LiveData<NavigationEvent> get() = _navigationEvent


    fun checkUserCredentials(intent: Intent) {
        viewModelScope.launch {
            val userCredentials = BaseClass.credentialsRepository.loadUserCredentials()
            if (userCredentials != null) {
                chatHelper.connectUser(userCredentials = userCredentials,
                    onSuccess = {
                        if (intent.hasExtra(KEY_CHANNEL_ID)) {
                            // Navigating from push, route to the messages screen
                            val channelId = requireNotNull(intent.getStringExtra(KEY_CHANNEL_ID))
                            val messageId = intent.getStringExtra(KEY_MESSAGE_ID)
                            val parentMessageId = intent.getStringExtra(KEY_PARENT_MESSAGE_ID)

                            _navigationEvent.value = NavigationEvent.ToMessagesScreen(
                                channelId = channelId,
                                messageId = messageId,
                                parentMessageId = parentMessageId
                            )
                        } else {
                            // Logged in, navigate to the channels screen
                            _navigationEvent.value = NavigationEvent.ToChannelsScreen
                        }
                    },
                    onError = { error ->
                        // Handle the error (show a message, log it, etc.)
                        Log.e("StartupViewModel", "Error: ${error.message}")
                        // Navigate to login screen or handle as appropriate
                        _navigationEvent.value = NavigationEvent.ToLoginScreen
                    })


            } else {
                // Not logged in, start with the login screen
                _navigationEvent.value = NavigationEvent.ToLoginScreen
            }
        }
    }

    sealed class NavigationEvent {
        object ToChannelsScreen : NavigationEvent()
        object ToLoginScreen : NavigationEvent()
        data class ToMessagesScreen(
            val channelId: String, val messageId: String?, val parentMessageId: String?
        ) : NavigationEvent()
    }

    companion object {
        private const val KEY_CHANNEL_ID = "channelId"
        private const val KEY_MESSAGE_ID = "messageId"
        private const val KEY_PARENT_MESSAGE_ID = "parentMessageId"
    }

}