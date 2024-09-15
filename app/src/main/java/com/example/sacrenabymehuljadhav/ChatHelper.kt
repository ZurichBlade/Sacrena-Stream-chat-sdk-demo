package com.example.sacrenabymehuljadhav

import android.content.Context
import android.util.Log
import com.example.sacrenabymehuljadhav.data.UserCredentials
import io.getstream.chat.android.client.BuildConfig
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.client.logger.ChatLogLevel
import io.getstream.chat.android.models.InitializationState
import io.getstream.chat.android.models.UploadAttachmentsNetworkType
import io.getstream.chat.android.offline.plugin.factory.StreamOfflinePluginFactory
import io.getstream.chat.android.state.plugin.config.StatePluginConfig
import io.getstream.chat.android.state.plugin.factory.StreamStatePluginFactory
import kotlinx.coroutines.flow.transformWhile
import javax.inject.Inject


class ChatHelper @Inject constructor(private val context: Context) {

    private val TAG = "ChatHelper"

    /**
     * Initializes the SDK with the given API key.
     */
    fun initializeSdk(apiKey: String) {
        Log.d(TAG, "[init] apiKey: $apiKey")

        val offlinePlugin = StreamOfflinePluginFactory(context)

        val statePluginFactory = StreamStatePluginFactory(
            config = StatePluginConfig(
                backgroundSyncEnabled = true,
                userPresence = true,
            ),
            appContext = context,
        )

        val logLevel = if (BuildConfig.DEBUG) ChatLogLevel.ALL else ChatLogLevel.NOTHING

        ChatClient.Builder(apiKey, context).withPlugins(offlinePlugin, statePluginFactory)
            .logLevel(logLevel)
            .uploadAttachmentsNetworkType(UploadAttachmentsNetworkType.NOT_ROAMING).build()
    }

    /**
     * Initializes [ChatClient] with the given user and saves it to the persistent storage.
     */
    suspend fun connectUser(
        userCredentials: UserCredentials,
        onSuccess: () -> Unit = {},
        onError: (AppError) -> Unit = {},
    ) {
        ChatClient.instance().run {
            clientState.initializationState.transformWhile {
                emit(it)
                it != InitializationState.COMPLETE
            }.collect {
                if (it == InitializationState.NOT_INITIALIZED) {
                    connectUser(userCredentials.user, userCredentials.token)
                        .enqueue { result ->
                            result.onError { error ->
                                onError(AppErrorImpl(error.message ?: "Unknown error"))
                            }.onSuccess {
                                BaseClass.credentialsRepository.saveUserCredentials(userCredentials)
                                onSuccess()
                            }
                        }
                }
            }
        }
    }

    /**
     * Logs out the user and removes their credentials from the persistent storage.
     */
    suspend fun disconnectUser() {
        BaseClass.credentialsRepository.clearCredentials()
        ChatClient.instance().disconnect(false).await()
    }

    interface AppError {
        val message: String
    }

    data class AppErrorImpl(
        override val message: String
    ) : AppError


}
