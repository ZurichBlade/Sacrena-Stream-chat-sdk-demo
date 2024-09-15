package com.example.sacrenabymehuljadhav

import android.app.Application
import com.example.sacrenabymehuljadhav.data.PredefinedUserCredentials
import com.example.sacrenabymehuljadhav.data.UserCredentialsRepository
import dagger.hilt.android.HiltAndroidApp
import io.getstream.chat.android.client.utils.internal.toggle.ToggleService
import io.getstream.chat.android.core.internal.InternalStreamChatApi
import io.getstream.chat.android.ui.common.helper.DateFormatter
import javax.inject.Inject

@HiltAndroidApp
class BaseClass : Application() {

    @Inject
    lateinit var chatHelper: ChatHelper

    override fun onCreate() {
        super.onCreate()

        credentialsRepository = UserCredentialsRepository(this)
        dateFormatter = DateFormatter.from(this)

        initializeToggleService()

        // Initialize Stream SDK
        chatHelper.initializeSdk(getApiKey())
    }

    private fun getApiKey(): String {
        return credentialsRepository.loadApiKey() ?: PredefinedUserCredentials.API_KEY
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

        const val autoTranslationEnabled: Boolean = true

        const val isComposerLinkPreviewEnabled: Boolean = true
    }
}
