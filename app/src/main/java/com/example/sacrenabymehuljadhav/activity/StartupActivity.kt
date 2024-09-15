package com.example.sacrenabymehuljadhav.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.core.app.TaskStackBuilder
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.sacrenabymehuljadhav.BaseClass
import com.example.sacrenabymehuljadhav.ChatHelper
import com.example.sacrenabymehuljadhav.activity.login.LoginActivity
import com.example.sacrenabymehuljadhav.viewmodel.StartupViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class StartupActivity : ComponentActivity() {

    private lateinit var viewModel: StartupViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(this)[StartupViewModel::class.java]

        // Observe the navigation events
        viewModel.navigationEvent.observe(this) { event ->
            handleNavigationEvent(event)
        }

        viewModel.checkUserCredentials(intent)

    }

    private fun handleNavigationEvent(event: StartupViewModel.NavigationEvent) {
        when (event) {
            is StartupViewModel.NavigationEvent.ToChannelsScreen -> {
                startActivity(ChannelsActivity.createIntent(this))
            }

            is StartupViewModel.NavigationEvent.ToLoginScreen -> {
                startActivity(LoginActivity.createIntent(this))
            }

            is StartupViewModel.NavigationEvent.ToMessagesScreen -> {
                TaskStackBuilder.create(this)
                    .addNextIntent(ChannelsActivity.createIntent(this))
                    .addNextIntent(
                        MessagesActivity.createIntent(
                            context = this,
                            channelId = event.channelId,
                            messageId = event.messageId,
                            parentMessageId = event.parentMessageId
                        )
                    )
                    .startActivities()
            }
        }
        finish()
    }

}
