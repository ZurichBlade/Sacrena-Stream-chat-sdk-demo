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

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastAny
import com.example.sacrenabymehuljadhav.streamcustomviews.message.isEmojiOnlyWithoutBubble
import com.example.sacrenabymehuljadhav.streamcustomviews.message.isFewEmoji
import com.example.sacrenabymehuljadhav.streamcustomviews.message.isSingleEmoji
import io.getstream.chat.android.compose.ui.theme.ChatTheme
import io.getstream.chat.android.core.internal.InternalStreamChatApi
import io.getstream.chat.android.models.Message
import io.getstream.chat.android.models.User
import io.getstream.chat.android.ui.common.utils.extensions.isMine

/**
 * Default text element for messages, with extra styling and padding for the chat bubble.
 *
 * It detects if we have any annotations/links in the message, and if so, it uses the [ClickableText]
 * component to allow for clicks on said links, that will open the link.
 *
 * Alternatively, it just shows a basic [Text] element.
 *
 * @param message Message to show.
 * @param currentUser The currently logged in user.
 * @param modifier Modifier for styling.
 * @param onLongItemClick Handler used for long pressing on the message text.
 * @param onLinkClick Handler used for clicking on a link in the message.
 */
@OptIn(InternalStreamChatApi::class)
@Composable
public fun MessageText(
    message: Message,
    currentUser: User?,
    modifier: Modifier = Modifier,
    onLongItemClick: (Message) -> Unit,
    onLinkClick: ((Message, String) -> Unit)? = null,
) {
    val context = LocalContext.current

    val styledText = ChatTheme.messageTextFormatter.format(message, currentUser)

    // Apply color styling on top of the formatted text
    val coloredText = AnnotatedString.Builder(styledText).apply {
        addStyle(SpanStyle(color = Color.Black), start = 0, end = styledText.length) // Adjust start and end as needed
    }.toAnnotatedString()

    val annotations = styledText.getStringAnnotations(0, styledText.lastIndex)

    // TODO: Fix emoji font padding once this is resolved and exposed: https://issuetracker.google.com/issues/171394808
    val style = when {
        message.isSingleEmoji() -> ChatTheme.typography.singleEmoji
        message.isFewEmoji() -> ChatTheme.typography.emojiOnly
        else -> if (message.isMine(currentUser)) {
            ChatTheme.ownMessageTheme.textStyle
        } else {
            ChatTheme.otherMessageTheme.textStyle
        }
    }

    if (annotations.fastAny { it.tag == AnnotationTagUrl || it.tag == AnnotationTagEmail }) {
        ClickableText(
            modifier = modifier
                .padding(
                    start = 12.dp,
                    end = 12.dp,
                    top = 8.dp,
                    bottom = 8.dp,
                ),
            text = coloredText,
            style = com.example.sacrenabymehuljadhav.ui.theme.Typography.bodyMedium,
            onLongPress = { onLongItemClick(message) },
        ) { position ->
            val targetUrl = annotations.firstOrNull {
                position in it.start..it.end
            }?.item

            if (!targetUrl.isNullOrEmpty()) {
                onLinkClick?.invoke(message, targetUrl) ?: run {
                    context.startActivity(
                        Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse(targetUrl),
                        ),
                    )
                }
            }
        }
    } else {
        val horizontalPadding = if (message.isEmojiOnlyWithoutBubble()) 0.dp else 12.dp
        val verticalPadding = if (message.isEmojiOnlyWithoutBubble()) 0.dp else 8.dp
        Text(
            modifier = modifier
                .padding(
                    horizontal = horizontalPadding,
                    vertical = verticalPadding,
                )
                .clipToBounds(),
            text = coloredText,
            style = com.example.sacrenabymehuljadhav.ui.theme.Typography.bodyMedium,
            fontWeight = FontWeight.SemiBold
        )
    }
}

/**
 * A spin-off of a Foundation component that allows calling long press handlers.
 * Contains only one additional parameter.
 *
 * @param onLongPress Handler called on long press.
 *
 * @see androidx.compose.foundation.text.ClickableText
 */
@Composable
private fun ClickableText(
    text: AnnotatedString,
    modifier: Modifier = Modifier,
    style: TextStyle = TextStyle.Default,
    softWrap: Boolean = true,
    overflow: TextOverflow = TextOverflow.Clip,
    maxLines: Int = Int.MAX_VALUE,
    onTextLayout: (TextLayoutResult) -> Unit = {},
    onLongPress: () -> Unit,
    onClick: (Int) -> Unit,
) {
    val layoutResult = remember { mutableStateOf<TextLayoutResult?>(null) }
    val pressIndicator = Modifier.pointerInput(onClick, onLongPress) {
        detectTapGestures(
            onLongPress = { onLongPress() },
            onTap = { pos ->
                layoutResult.value?.let { layoutResult ->
                    onClick(layoutResult.getOffsetForPosition(pos))
                }
            },
        )
    }

    BasicText(
        text = text,
        modifier = modifier.then(pressIndicator),
        style = style,
        softWrap = softWrap,
        overflow = overflow,
        maxLines = maxLines,
        onTextLayout = {
            layoutResult.value = it
            onTextLayout(it)
        },
    )
}

@Preview
@Composable
private fun MessageTextPreview() {
    ChatTheme {
        MessageText(
            message = Message(text = "Hello World!"),
            currentUser = null,
            onLongItemClick = {},
        )
    }
}

internal typealias AnnotationTag = String

internal const val AnnotationTagUrl: AnnotationTag = "URL"

/**
 * The tag used to annotate emails in the message text.
 */
internal const val AnnotationTagEmail: AnnotationTag = "EMAIL"