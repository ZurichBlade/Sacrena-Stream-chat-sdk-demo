/*
 * Copyright (c) 2014-2022 Stream.io Inc. All rights reserved.
 *
 * Licensed under the Stream License;
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    https://github.com/GetStream/stream-chat-android/blob/main/LICENSE
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.sacrenabymehuljadhav.streamcustomviews.componenets

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import io.getstream.chat.android.compose.R
import io.getstream.chat.android.compose.state.DateFormatType
import io.getstream.chat.android.compose.ui.components.Timestamp
import io.getstream.chat.android.compose.ui.components.messages.MessageThreadFooter
import io.getstream.chat.android.compose.ui.components.messages.MessageTranslatedLabel
import io.getstream.chat.android.compose.ui.theme.ChatTheme
import io.getstream.chat.android.core.internal.InternalStreamChatApi
import io.getstream.chat.android.models.Message
import io.getstream.chat.android.ui.common.state.messages.list.MessageItemState
import java.util.Date

/**
 * Default message footer, which contains either [MessageThreadFooter] or the default footer, which
 * holds the sender name and the timestamp.
 *
 * @param messageItem Message to show.
 */
@Composable
@Suppress("LongMethod")
public fun MessageFooter(
    messageItem: MessageItemState,
) {
    val message = messageItem.message
    val hasThread = message.threadParticipants.isNotEmpty()
    val alignment = ChatTheme.messageAlignmentProvider.provideMessageAlignment(messageItem)

    if (hasThread && !messageItem.isInThread) {
        val replyCount = message.replyCount
        MessageThreadFooter(
            participants = message.threadParticipants,
            messageAlignment = alignment,
            text = LocalContext.current.resources.getQuantityString(
                R.plurals.stream_compose_message_list_thread_footnote,
                replyCount,
                replyCount,
            ),
        )
    }

    Column(horizontalAlignment = alignment.contentAlignment) {
        MessageTranslatedLabel(messageItem)
        if (messageItem.showMessageFooter) {
            val showEditLabel = message.messageTextUpdatedAt != null
            var showEditInfo by remember { mutableStateOf(false) }
            Row(
                modifier = Modifier
                    .padding(top = 4.dp, bottom = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // if (!messageItem.isMine) {
                //     Text(
                //         modifier = Modifier
                //             .padding(end = 8.dp)
                //             .weight(1f, fill = false),
                //         text = message.user.name,
                //         style = ChatTheme.typography.footnote,
                //         overflow = TextOverflow.Ellipsis,
                //         maxLines = 1,
                //         color = ChatTheme.colors.textLowEmphasis,
                //     )
                // } else {
                //     MessageReadStatusIcon(
                //         modifier = Modifier.padding(end = 4.dp),
                //         message = messageItem.message,
                //         isMessageRead = messageItem.isMessageRead,
                //         readCount = messageItem.messageReadBy.size,
                //     )
                // }

                val updatedAt = message.updatedAt
                val createdAt = message.createdAt ?: message.createdLocallyAt
                val date = when {
                    createdAt == null -> updatedAt
                    updatedAt == null -> createdAt
                    else -> when (updatedAt.after(createdAt)) {
                        true -> updatedAt
                        else -> createdAt
                    }
                }
                if (date != null) {
                    // Timestamp(date = date, formatType = DateFormatType.TIME)
                }
                if (showEditLabel && !showEditInfo) {
                    Text(
                        modifier = Modifier.padding(start = 4.dp, end = 4.dp),
                        text = "·",
                        style = ChatTheme.typography.footnote,
                        color = ChatTheme.colors.textLowEmphasis,
                    )
                    Text(
                        modifier = Modifier.clickable { showEditInfo = !showEditInfo },
                        text = LocalContext.current.getString(R.string.stream_compose_message_list_footnote_edited),
                        style = ChatTheme.typography.footnote,
                        color = ChatTheme.colors.textLowEmphasis,
                    )
                }
            }
            if (showEditInfo) {
                Row(
                    modifier = Modifier
                        .padding(bottom = 4.dp)
                        .clickable { showEditInfo = !showEditInfo },
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        modifier = Modifier
                            .padding(end = 4.dp)
                            .weight(1f, fill = false),
                        text = LocalContext.current.getString(R.string.stream_compose_message_list_footnote_edited),
                        style = ChatTheme.typography.footnote,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1,
                        color = ChatTheme.colors.textLowEmphasis,
                    )
                    MessageEditedTimestamp(message = message)
                }
            }
        }
    }
}

/**
 * Composable function to display the timestamp for when a message was last edited.
 *
 * This function adjusts the `message.messageTextUpdatedAt` time to ensure it does not exceed the current system time.
 * If the `messageTextUpdatedAt` time is slightly in the future, it resets the time to the current system time.
 *
 * @param message The message object containing the `messageTextUpdatedAt` timestamp.
 * @param modifier Modifier for styling.
 * @param formatType The type of formatting to provide. By default, it's [DateFormatType.RELATIVE].
 */
@OptIn(InternalStreamChatApi::class)
@Composable
internal fun MessageEditedTimestamp(
    message: Message,
    modifier: Modifier = Modifier,
    formatType: DateFormatType = DateFormatType.RELATIVE,
) {
    val editedAt = message.messageTextUpdatedAt?.truncateFuture()
    Timestamp(date = editedAt, modifier = modifier, formatType = formatType)
}


@InternalStreamChatApi
public inline fun Date?.truncateFuture(
    now: () -> Long = { System.currentTimeMillis() },
): Date? = this?.apply {
    if (time > now()) {
        time = now()
    }
}