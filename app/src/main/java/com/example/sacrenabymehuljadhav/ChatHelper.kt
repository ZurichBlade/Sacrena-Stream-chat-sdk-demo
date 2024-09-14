package com.example.sacrenabymehuljadhav

import android.content.Context
import android.util.Log
import com.example.sacrenabymehuljadhav.activity.StartupActivity
import com.example.sacrenabymehuljadhav.data.UserCredentials
import io.getstream.chat.android.client.BuildConfig
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.client.logger.ChatLogLevel
import io.getstream.chat.android.client.notifications.handler.NotificationHandlerFactory
import io.getstream.chat.android.models.Channel
import io.getstream.chat.android.models.InitializationState
import io.getstream.chat.android.models.Message
import io.getstream.chat.android.models.UploadAttachmentsNetworkType
import io.getstream.chat.android.offline.plugin.factory.StreamOfflinePluginFactory
import io.getstream.chat.android.state.plugin.config.StatePluginConfig
import io.getstream.chat.android.state.plugin.factory.StreamStatePluginFactory
import io.getstream.result.Error
import kotlinx.coroutines.flow.transformWhile

/**
 * A helper class that is responsible for initializing the SDK and connecting/disconnecting
 * a user. Under the hood, it persists the user so that we are able to connect automatically
 * next time the app is launched.
 */
object ChatHelper {

    private const val TAG = "ChatHelper"

    /**
     * Initializes the SDK with the given API key.
     */
    fun initializeSdk(context: Context, apiKey: String) {
        Log.d(TAG, "[init] apiKey: $apiKey")
//        val notificationConfig = NotificationConfig(
//            pushDeviceGenerators = listOf(FirebasePushDeviceGenerator(providerName = "Firebase")),
//            autoTranslationEnabled = ChatApp.autoTranslationEnabled,
//        )
        val notificationHandler = NotificationHandlerFactory.createNotificationHandler(
            context = context,
//            notificationConfig = notificationConfig,
            newMessageIntent = { message: Message, channel: Channel ->
                StartupActivity.createIntent(
                    context = context,
                    channelId = "${channel.type}:${channel.id}",
                    messageId = message.id,
                    parentMessageId = message.parentId,
                )
            },
        )

        val offlinePlugin = StreamOfflinePluginFactory(context)

        val statePluginFactory = StreamStatePluginFactory(
            config = StatePluginConfig(
                backgroundSyncEnabled = true,
                userPresence = true,
            ),
            appContext = context,
        )

        val logLevel = if (BuildConfig.DEBUG) ChatLogLevel.ALL else ChatLogLevel.NOTHING

        ChatClient.Builder(apiKey, context)
//            .notifications(notificationConfig, notificationHandler)
            .withPlugins(offlinePlugin, statePluginFactory)
            .logLevel(logLevel)
            .uploadAttachmentsNetworkType(UploadAttachmentsNetworkType.NOT_ROAMING)
            .build()
    }

    /**
     * Initializes [ChatClient] with the given user and saves it to the persistent storage.
     */
    suspend fun connectUser(
        userCredentials: UserCredentials,
        onSuccess: () -> Unit = {},
        onError: (Error) -> Unit = {},
    ) {
        ChatClient.instance().run {
            clientState.initializationState
                .transformWhile {
                    emit(it)
                    it != InitializationState.COMPLETE
                }
                .collect {
                    if (it == InitializationState.NOT_INITIALIZED) {
                        connectUser(userCredentials.user, userCredentials.token)
                            .enqueue { result ->
                                result.onError(onError)
                                    .onSuccess {
                                        ChatApp.credentialsRepository.saveUserCredentials(
                                            userCredentials
                                        )
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
        ChatApp.credentialsRepository.clearCredentials()

        ChatClient.instance().disconnect(false).await()
    }
}
