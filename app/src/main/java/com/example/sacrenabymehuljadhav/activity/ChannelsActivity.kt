package com.example.sacrenabymehuljadhav.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.ui.res.stringResource
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewModelScope
import com.example.sacrenabymehuljadhav.BaseClass
import com.example.sacrenabymehuljadhav.ChatHelper
import com.example.sacrenabymehuljadhav.R
import com.example.sacrenabymehuljadhav.activity.login.LoginActivity
import com.example.sacrenabymehuljadhav.streamcustomviews.channel.ChannelsScreen
import dagger.hilt.android.AndroidEntryPoint
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.compose.ui.theme.ChatTheme
import io.getstream.chat.android.compose.viewmodel.channels.ChannelListViewModel
import io.getstream.chat.android.compose.viewmodel.channels.ChannelViewModelFactory
import io.getstream.chat.android.models.Channel
import io.getstream.chat.android.models.Message
import io.getstream.chat.android.models.querysort.QuerySortByField
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ChannelsActivity @Inject constructor(
    private val chatHelper: ChatHelper
) : BaseConnectedActivity() {

    private val factory by lazy {
        ChannelViewModelFactory(
            ChatClient.instance(),
            QuerySortByField.descByName("last_updated"),
            null,
        )
    }

    private val listViewModel: ChannelListViewModel by viewModels { factory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set the status bar color
        window?.let { window ->
            window.statusBarColor = ContextCompat.getColor(this, R.color.background)
        }

        setContent {
            ChatTheme(
                dateFormatter = BaseClass.dateFormatter,
                autoTranslationEnabled = BaseClass.autoTranslationEnabled,
                allowUIAutomationTest = true,
            ) {
                ChannelsScreen(
                    viewModelFactory = factory,
                    title = stringResource(id = R.string.app_name),
                    isShowingHeader = true,
                    onChannelClick = ::openMessages,
                    onSearchMessageItemClick = ::openMessages,
                    onBackPressed = ::finish,
                    onHeaderActionClick = {
                        listViewModel.viewModelScope.launch {
                            chatHelper.disconnectUser()
                            openUserLogin()
                        }
                    },
                )
            }
        }
    }


    private fun openMessages(channel: Channel) {
        startActivity(
            MessagesActivity.createIntent(
                context = this,
                channelId = channel.cid,
                messageId = null,
                parentMessageId = null,
            ),
        )
    }

    private fun openMessages(message: Message) {
        startActivity(
            MessagesActivity.createIntent(
                context = this,
                channelId = message.cid,
                messageId = message.id,
                parentMessageId = message.parentId,
            ),
        )
    }

    private fun openUserLogin() {
        finish()
        startActivity(LoginActivity.createIntent(this))
        overridePendingTransition(0, 0)
    }

    companion object {
        fun createIntent(context: Context): Intent {
            return Intent(context, ChannelsActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
        }
    }
}
