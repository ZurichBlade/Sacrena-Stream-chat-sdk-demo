package com.example.sacrenabymehuljadhav.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.ui.res.painterResource
import androidx.core.content.ContextCompat
import com.example.sacrenabymehuljadhav.ChatApp
import com.example.sacrenabymehuljadhav.R
import com.example.sacrenabymehuljadhav.streamcustomviews.message.MessagesScreen
import io.getstream.chat.android.compose.state.messages.attachments.StatefulStreamMediaRecorder
import io.getstream.chat.android.compose.ui.components.messageoptions.MessageOptionItemVisibility
import io.getstream.chat.android.compose.ui.theme.AttachmentPickerTheme
import io.getstream.chat.android.compose.ui.theme.ChatTheme
import io.getstream.chat.android.compose.ui.theme.MessageComposerTheme
import io.getstream.chat.android.compose.ui.theme.MessageOptionsTheme
import io.getstream.chat.android.compose.ui.theme.ReactionOptionsTheme
import io.getstream.chat.android.compose.ui.theme.StreamColors
import io.getstream.chat.android.compose.ui.theme.StreamTypography
import io.getstream.chat.android.compose.viewmodel.messages.AttachmentsPickerViewModel
import io.getstream.chat.android.compose.viewmodel.messages.MessageComposerViewModel
import io.getstream.chat.android.compose.viewmodel.messages.MessageListViewModel
import io.getstream.chat.android.compose.viewmodel.messages.MessagesViewModelFactory
import io.getstream.chat.android.models.ReactionSortingByLastReactionAt
import io.getstream.chat.android.ui.common.state.messages.list.DeletedMessageVisibility
import io.getstream.sdk.chat.audio.recording.DefaultStreamMediaRecorder
import io.getstream.sdk.chat.audio.recording.MediaRecorderState
import io.getstream.sdk.chat.audio.recording.StreamMediaRecorder

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
            autoTranslationEnabled = ChatApp.autoTranslationEnabled,
            isComposerLinkPreviewEnabled = ChatApp.isComposerLinkPreviewEnabled,
            deletedMessageVisibility = DeletedMessageVisibility.ALWAYS_VISIBLE,
            messageId = intent.getStringExtra(KEY_MESSAGE_ID),
            parentMessageId = intent.getStringExtra(KEY_PARENT_MESSAGE_ID),
        )
    }

    private val listViewModel by viewModels<MessageListViewModel>(factoryProducer = { factory })

    private val attachmentsPickerViewModel by viewModels<AttachmentsPickerViewModel>(factoryProducer = { factory })
    private val composerViewModel by viewModels<MessageComposerViewModel>(factoryProducer = { factory })

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
                dateFormatter = ChatApp.dateFormatter,
                autoTranslationEnabled = ChatApp.autoTranslationEnabled,
                isComposerLinkPreviewEnabled = ChatApp.isComposerLinkPreviewEnabled,
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

//    @Composable
    /*    fun MyCustomUi() {
            val isShowingAttachments = attachmentsPickerViewModel.isShowingAttachments
            val selectedMessageState = listViewModel.currentMessagesState.selectedMessageState
            val user by listViewModel.user.collectAsState()
            val lazyListState = rememberMessageListState()

            Box(modifier = Modifier.fillMaxSize()) {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        MyCustomComposer()
                    },
                ) {
                    MessageList(
                        modifier = Modifier
                            .padding(it)
                            .background(ChatTheme.colors.appBackground)
                            .fillMaxSize(),
                        viewModel = listViewModel,
                        reactionSorting = ReactionSortingByFirstReactionAt,
                        messagesLazyListState = if (listViewModel.isInThread) rememberMessageListState() else lazyListState,
                        onThreadClick = { message ->
                            composerViewModel.setMessageMode(MessageMode.MessageThread(message))
                            listViewModel.openMessageThread(message)
                        },
                        onMediaGalleryPreviewResult = { result ->
                            when (result?.resultType) {
                                MediaGalleryPreviewResultType.QUOTE -> {
                                    val message = listViewModel.getMessageById(result.messageId)

                                    if (message != null) {
                                        composerViewModel.performMessageAction(Reply(message))
                                    }
                                }

                                MediaGalleryPreviewResultType.SHOW_IN_CHAT -> {
                                }

                                null -> Unit
                            }
                        },
                    )
                }

                if (isShowingAttachments) {
                    var isFullScreenContent by rememberSaveable { mutableStateOf(false) }
                    val screenHeight = LocalConfiguration.current.screenHeightDp
                    val pickerHeight by animateDpAsState(
                        targetValue = if (isFullScreenContent) screenHeight.dp else 350.dp,
                        label = "full sized picker animation",
                    )

                    AttachmentsPicker(
                        attachmentsPickerViewModel = attachmentsPickerViewModel,
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .height(pickerHeight),
                        shape = if (isFullScreenContent) {
                            RoundedCornerShape(0.dp)
                        } else {
                            ChatTheme.shapes.bottomSheet
                        },
                        onAttachmentsSelected = { attachments ->
                            attachmentsPickerViewModel.changeAttachmentState(false)
                            composerViewModel.addSelectedAttachments(attachments)
                        },
                        onTabClick = { _, tab -> isFullScreenContent = tab.isFullContent },
                        onAttachmentPickerAction = { action ->
                            if (action is AttachmentPickerPollCreation) {
                                composerViewModel.createPoll(
                                    pollConfig = PollConfig(
                                        name = action.question,
                                        options = action.options.filter { it.title.isNotEmpty() }.map { it.title },
                                        description = action.question,
                                        allowUserSuggestedOptions = action.switches.any { it.key == "allowUserSuggestedOptions" && it.enabled },
                                        votingVisibility = if (action.switches.any { it.key == "votingVisibility" && it.enabled }) {
                                            VotingVisibility.ANONYMOUS
                                        } else {
                                            VotingVisibility.PUBLIC
                                        },
                                        maxVotesAllowed = if (action.switches.any { it.key == "maxVotesAllowed" && it.enabled }) {
                                            action.switches.first { it.key == "maxVotesAllowed" }.pollSwitchInput?.value.toString()
                                                .toInt()
                                        } else {
                                            1
                                        },
                                    ),
                                )
                            }
                        },
                        onDismiss = {
                            attachmentsPickerViewModel.changeAttachmentState(false)
                            attachmentsPickerViewModel.dismissAttachments()
                        },
                    )
                }

                if (selectedMessageState != null) {
                    val selectedMessage = selectedMessageState.message
                    when (selectedMessageState) {
                        is SelectedMessageOptionsState -> {
                            SelectedMessageMenu(
                                modifier = Modifier
                                    .align(Alignment.Center)
                                    .padding(horizontal = 20.dp)
                                    .wrapContentSize(),
                                shape = ChatTheme.shapes.attachment,
                                messageOptions = defaultMessageOptionsState(
                                    selectedMessage = selectedMessage,
                                    currentUser = user,
                                    isInThread = listViewModel.isInThread,
                                    ownCapabilities = selectedMessageState.ownCapabilities,
                                ),
                                message = selectedMessage,
                                ownCapabilities = selectedMessageState.ownCapabilities,
                                onMessageAction = { action ->
                                    composerViewModel.performMessageAction(action)
                                    listViewModel.performMessageAction(action)
                                },
                                onShowMoreReactionsSelected = {
                                    listViewModel.selectExtendedReactions(selectedMessage)
                                },
                                onDismiss = { listViewModel.removeOverlay() },
                            )
                        }

                        is SelectedMessageReactionsState -> {
                            SelectedReactionsMenu(
                                modifier = Modifier
                                    .align(Alignment.Center)
                                    .padding(horizontal = 20.dp)
                                    .wrapContentSize(),
                                shape = ChatTheme.shapes.attachment,
                                message = selectedMessage,
                                currentUser = user,
                                onMessageAction = { action ->
                                    composerViewModel.performMessageAction(action)
                                    listViewModel.performMessageAction(action)
                                },
                                onShowMoreReactionsSelected = {
                                    listViewModel.selectExtendedReactions(selectedMessage)
                                },
                                onDismiss = { listViewModel.removeOverlay() },
                                ownCapabilities = selectedMessageState.ownCapabilities,
                            )
                        }

                        is SelectedMessageReactionsPickerState -> {
                            ReactionsPicker(
                                modifier = Modifier
                                    .align(Alignment.Center)
                                    .padding(horizontal = 20.dp)
                                    .wrapContentSize(),
                                shape = ChatTheme.shapes.attachment,
                                message = selectedMessage,
                                onMessageAction = { action ->
                                    composerViewModel.performMessageAction(action)
                                    listViewModel.performMessageAction(action)
                                },
                                onDismiss = { listViewModel.removeOverlay() },
                            )
                        }

                        else -> Unit
                    }
                }
            }
        }*/

//    @Composable
    /*   fun MyCustomComposer() {
           MessageComposer(
               modifier = Modifier
                   .fillMaxWidth()
                   .wrapContentHeight(),
               viewModel = composerViewModel,
               statefulStreamMediaRecorder = statefulStreamMediaRecorder,
               integrations = {},
               input = { inputState ->
                   MessageInput(
                       modifier = Modifier
                           .fillMaxWidth()
                           .weight(7f)
                           .padding(start = 8.dp),
                       messageComposerState = inputState,
                       onValueChange = { composerViewModel.setMessageInput(it) },
                       onAttachmentRemoved = { composerViewModel.removeSelectedAttachment(it) },
                       label = {
                           Row(
                               Modifier.wrapContentWidth(),
                               verticalAlignment = Alignment.CenterVertically,
                           ) {
                               Icon(
                                   painter = painterResource(id = R.drawable.stream_compose_ic_gallery),
                                   contentDescription = null,
                               )

                               Text(
                                   modifier = Modifier.padding(start = 4.dp),
                                   text = "Type something",
                                   color = ChatTheme.colors.textLowEmphasis,
                               )
                           }
                       },
                       innerTrailingContent = {
                           Icon(
                               modifier = Modifier
                                   .size(24.dp)
                                   .clickable(
                                       interactionSource = remember { MutableInteractionSource() },
                                       indication = rememberRipple(),
                                   ) {
                                       val state = composerViewModel.messageComposerState.value

                                       composerViewModel.sendMessage(
                                           composerViewModel.buildNewMessage(
                                               state.inputValue,
                                               state.attachments,
                                           ),
                                       )
                                   },
                               painter = painterResource(id = R.drawable.stream_compose_ic_send),
                               tint = ChatTheme.colors.primaryAccent,
                               contentDescription = null,
                           )
                       },
                   )
               },
               trailingContent = { Spacer(modifier = Modifier.size(8.dp)) },
           )
       }*/

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
