package com.example.sacrenabymehuljadhav

import android.app.Application
import com.example.sacrenabymehuljadhav.data.PredefinedUserCredentials
import com.example.sacrenabymehuljadhav.data.UserCredentialsRepository
import io.getstream.chat.android.client.utils.internal.toggle.ToggleService
import io.getstream.chat.android.core.internal.InternalStreamChatApi
import io.getstream.chat.android.ui.common.helper.DateFormatter

class ChatApp : Application() {

    override fun onCreate() {
        super.onCreate()
        // Done for simplicity, use a DI framework instead
        credentialsRepository = UserCredentialsRepository(this)
        dateFormatter = DateFormatter.from(this)

        initializeToggleService()

        // Initialize Stream SDK
        ChatHelper.initializeSdk(this, getApiKey())
    }

    private fun getApiKey(): String {
        return credentialsRepository.loadApiKey() ?: PredefinedUserCredentials.API_KEY1
    }

    @OptIn(InternalStreamChatApi::class)
    private fun initializeToggleService() {
        ToggleService.init(applicationContext)
    }

    companion object {
        lateinit var credentialsRepository: UserCredentialsRepository
            private set

        lateinit var dateFormatter: DateFormatter
            private set

        public const val autoTranslationEnabled: Boolean = true

        public const val isComposerLinkPreviewEnabled: Boolean = true
    }
}
