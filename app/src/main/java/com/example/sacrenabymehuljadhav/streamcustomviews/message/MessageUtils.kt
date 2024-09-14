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

package com.example.sacrenabymehuljadhav.streamcustomviews.message

import android.content.Context
import io.getstream.chat.android.client.extensions.uploadId
import io.getstream.chat.android.client.utils.attachment.isAudio
import io.getstream.chat.android.client.utils.attachment.isFile
import io.getstream.chat.android.client.utils.attachment.isVideo
import io.getstream.chat.android.compose.R
import io.getstream.chat.android.models.Attachment
import io.getstream.chat.android.models.Message
import io.getstream.chat.android.models.User

/**
 * Takes the current message and returns the sender display name.
 *
 * @return Sender display name.
 */
internal fun Message.getSenderDisplayName(
    context: Context,
    currentUser: User?,
): String? =
    when (user.id) {
        currentUser?.id -> context.getString(R.string.stream_compose_channel_list_you)
        else -> null
    }


/**
 * @return If the message contains an attachment that is currently being uploaded.
 */
internal fun Message.isUploading(): Boolean = attachments.any { it.isUploading() }

/**
 * @return If the message is emoji only or not.
 */
internal fun Message.isEmojiOnly(): Boolean = EmojiUtil.isEmojiOnly(this)

/**
 * @return If the message is single emoji only or not.
 */
internal fun Message.isSingleEmoji(): Boolean = EmojiUtil.isSingleEmoji(this)

/**
 * @return The number of emoji inside the message.
 */
internal fun Message.getEmojiCount(): Int = EmojiUtil.getEmojiCount(this)

/**
 * @return If the message should has less or equal to [MaxFullSizeEmoji] emoji count.
 */
internal fun Message.isFewEmoji(): Boolean = isEmojiOnly() && getEmojiCount() <= MaxFullSizeEmoji

/**
 * @return If the message is emoji only and should be shown without a message bubble or not.
 */
internal fun Message.isEmojiOnlyWithoutBubble(): Boolean = isFewEmoji() &&
        replyTo == null

/**
 * Max number of emoji without showing it inside a bubble.
 */
internal const val MaxFullSizeEmoji: Int = 3


public fun Attachment.isAnyFileType(): Boolean {
    return uploadId != null ||
            upload != null ||
            isFile() ||
            isVideo() ||
            isAudio()
}

/**
 * @return If the attachment is currently being uploaded to the server.
 */
public fun Attachment.isUploading(): Boolean {
    return (uploadState is Attachment.UploadState.InProgress || uploadState is Attachment.UploadState.Idle) &&
            upload != null &&
            uploadId != null
}

/**
 * @return If the attachment has been failed when uploading to the server.
 */
public fun Attachment.isFailed(): Boolean {
    return (uploadState is Attachment.UploadState.Failed) &&
            upload != null &&
            uploadId != null
}

/**
 * @return If the [Attachment] is a link attachment or not.
 */
public fun Attachment.hasLink(): Boolean = titleLink != null || ogUrl != null

public object EmojiUtil {

    /**
     * Checks whether the message consists of emoji only.
     *
     * @param message The message that was sent/received by user.
     */
    public fun isEmojiOnly(message: Message): Boolean {
        return message.text.isNotBlank() && message.text.replace(EMOJI_REGEX, "")
            .isEmpty() && message.deletedAt == null
    }

    /**
     * Checks whether the message consists of single emoji.
     *
     * @param message The message that was sent/received by user.
     */
    public fun isSingleEmoji(message: Message): Boolean {
        return isEmojiOnly(message) && message.text.replaceFirst(EMOJI_REGEX, "").isEmpty()
    }

    /**
     * Counts the number of emoji inside a message.
     *
     * @return The number of emojis inside a message.
     */
    public fun getEmojiCount(message: Message): Int = EMOJI_REGEX.findAll(message.text).count()

    /**
     * Regex which matches emojis inside a string.
     */
    private val EMOJI_REGEX = Regex(
        "(?:[\\u2700-\\u27bf]|(?:[\\ud83c\\udde6-\\ud83c\\uddff]){2}|[\\ud800\\udc00-\\uDBFF\\uDFFF]|" +
                "[\\u2600-\\u26FF])[\\ufe0e\\ufe0f]?(?:[\\u0300-\\u036f\\ufe20-\\ufe23\\u20d0-\\u20f0]" +
                "|[\\ud83c\\udffb-\\ud83c\\udfff])?(?:\\u200d(?:[^\\ud800-\\udfff]|(?:[\\ud83c\\udde6-\\ud83c\\uddff])" +
                "{2}|[\\ud800\\udc00-\\uDBFF\\uDFFF]|[\\u2600-\\u26FF])[\\ufe0e\\ufe0f]" +
                "?(?:[\\u0300-\\u036f\\ufe20-\\ufe23\\u20d0-\\u20f0]|[\\ud83c\\udffb-\\ud83c\\udfff])?)" +
                "*|[\\u0023-\\u0039]\\ufe0f?\\u20e3|\\u3299|\\u3297|\\u303d|\\u3030|\\u24c2|" +
                "[\\ud83c\\udd70-\\ud83c\\udd71]|[\\ud83c\\udd7e-\\ud83c\\udd7f]|\\ud83c\\udd8e|" +
                "[\\ud83c\\udd91-\\ud83c\\udd9a]|[\\ud83c\\udde6-\\ud83c\\uddff]|[\\ud83c\\ude01-\\ud83c\\ude02]" +
                "|\\ud83c\\ude1a|\\ud83c\\ude2f|[\\ud83c\\ude32-\\ud83c\\ude3a]|[\\ud83c\\ude50-\\ud83c\\ude51]" +
                "|\\u203c|\\u2049|[\\u25aa-\\u25ab]|\\u25b6|\\u25c0|[\\u25fb-\\u25fe]|\\u00a9|\\u00ae|\\u2122" +
                "|\\u2139|\\ud83c\\udc04|[\\u2600-\\u26FF]|\\u2b05|\\u2b06|\\u2b07|\\u2b1b|\\u2b1c|\\u2b50|\\u2b55|" +
                "\\u231a|\\u231b|\\u2328|\\u23cf|[\\u23e9-\\u23f3]|[\\u23f8-\\u23fa]|\\ud83c\\udccf|\\u2934|\\u2935|" +
                "[\\u2190-\\u21ff]",
    )
}
