package com.example.sacrenabymehuljadhav.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.ui.res.painterResource
import androidx.core.content.ContextCompat
import com.example.sacrenabymehuljadhav.BaseClass
import com.example.sacrenabymehuljadhav.R
import com.example.sacrenabymehuljadhav.streamcustomviews.message.MessagesScreen
import dagger.hilt.android.AndroidEntryPoint
import io.getstream.chat.android.compose.state.messages.attachments.StatefulStreamMediaRecorder
import io.getstream.chat.android.compose.ui.components.messageoptions.MessageOptionItemVisibility
import io.getstream.chat.android.compose.ui.theme.AttachmentPickerTheme
import io.getstream.chat.android.compose.ui.theme.ChatTheme
import io.getstream.chat.android.compose.ui.theme.MessageComposerTheme
import io.getstream.chat.android.compose.ui.theme.MessageOptionsTheme
import io.getstream.chat.android.compose.ui.theme.ReactionOptionsTheme
import io.getstream.chat.android.compose.ui.theme.StreamColors
import io.getstream.chat.android.compose.ui.theme.StreamTypography
import io.getstream.chat.android.compose.viewmodel.messages.MessagesViewModelFactory
import io.getstream.chat.android.models.ReactionSortingByLastReactionAt
import io.getstream.chat.android.ui.common.state.messages.list.DeletedMessageVisibility
import io.getstream.sdk.chat.audio.recording.DefaultStreamMediaRecorder
import io.getstream.sdk.chat.audio.recording.MediaRecorderState
import io.getstream.sdk.chat.audio.recording.StreamMediaRecorder

@AndroidEntryPoint
class MessagesActivity : BaseConnectedActivity() {

    private val streamMediaRecorder: StreamMediaRecorder by lazy {
        DefaultStreamMediaRecorder(
            applicationContext
        )
    }
    private val statefulStreamMediaRecorder by lazy {
        StatefulStreamMediaRecorder(
            streamMediaRecorder
        )
    }

    private val factory by lazy {
        MessagesViewModelFactory(
            context = this,
            channelId = requireNotNull(intent.getStringExtra(KEY_CHANNEL_ID)),
            autoTranslationEnabled = BaseClass.autoTranslationEnabled,
            isComposerLinkPreviewEnabled = BaseClass.isComposerLinkPreviewEnabled,
            deletedMessageVisibility = DeletedMessageVisibility.ALWAYS_VISIBLE,
            messageId = intent.getStringExtra(KEY_MESSAGE_ID),
            parentMessageId = intent.getStringExtra(KEY_PARENT_MESSAGE_ID),
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window?.let { window ->
            window.statusBarColor = ContextCompat.getColor(this, R.color.background)
        }

        setContent {
            val colors =
                if (isSystemInDarkTheme()) StreamColors.defaultDarkColors() else StreamColors.defaultColors()
            val typography = StreamTypography.defaultTypography()
            ChatTheme(
                colors = colors,
                typography = typography,
                dateFormatter = BaseClass.dateFormatter,
                autoTranslationEnabled = BaseClass.autoTranslationEnabled,
                isComposerLinkPreviewEnabled = BaseClass.isComposerLinkPreviewEnabled,
                allowUIAutomationTest = true,
                messageComposerTheme = MessageComposerTheme.defaultTheme(typography)
                    .let { messageComposerTheme ->
                        messageComposerTheme.copy(
                            attachmentCancelIcon = messageComposerTheme.attachmentCancelIcon.copy(
                                painter = painterResource(id = io.getstream.chat.android.compose.R.drawable.stream_compose_ic_clear),
                                tint = colors.overlayDark,
                                backgroundColor = colors.appBackground,
                            ),
                        )
                    },
                attachmentPickerTheme = AttachmentPickerTheme.defaultTheme(colors).copy(
                    backgroundOverlay = colors.overlayDark,
                    backgroundSecondary = colors.inputBackground,
                    backgroundPrimary = colors.barsBackground,
                ),
                reactionOptionsTheme = ReactionOptionsTheme.defaultTheme(),
                messageOptionsTheme = MessageOptionsTheme.defaultTheme(
                    optionVisibility = MessageOptionItemVisibility(),
                ),
            ) {
                MessagesScreen(
                    viewModelFactory = factory,
                    reactionSorting = ReactionSortingByLastReactionAt,
                    onBackPressed = { finish() },
                    onHeaderTitleClick = {},
                    onUserAvatarClick = { user ->
                        Log.i("MessagesActivity", "user avatar clicked: ${user.id}")
                    },
                )
            }
        }
    }

    override fun onPause() {
        super.onPause()
        if (statefulStreamMediaRecorder.mediaRecorderState.value == MediaRecorderState.RECORDING) {
            streamMediaRecorder.stopRecording()
        } else {
            streamMediaRecorder.release()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        streamMediaRecorder.stopRecording()
    }

    companion object {
        private const val TAG = "MessagesActivity"
        private const val KEY_CHANNEL_ID = "channelId"
        private const val KEY_MESSAGE_ID = "messageId"
        private const val KEY_PARENT_MESSAGE_ID = "parentMessageId"

        fun createIntent(
            context: Context,
            channelId: String,
            messageId: String? = null,
            parentMessageId: String? = null,
        ): Intent {
            return Intent(context, MessagesActivity::class.java).apply {
                putExtra(KEY_CHANNEL_ID, channelId)
                putExtra(KEY_MESSAGE_ID, messageId)
                putExtra(KEY_PARENT_MESSAGE_ID, parentMessageId)
            }
        }
    }
}
