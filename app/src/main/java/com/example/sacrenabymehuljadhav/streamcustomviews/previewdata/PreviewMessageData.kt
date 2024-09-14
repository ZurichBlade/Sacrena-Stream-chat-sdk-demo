package com.example.sacrenabymehuljadhav.streamcustomviews.previewdata

import io.getstream.chat.android.models.Message
import io.getstream.chat.android.models.MessageType
import io.getstream.chat.android.models.Reaction
import java.util.Date

/**
 * Provides sample messages that will be used to render previews.
 */
public object PreviewMessageData {

    public val message1: Message = Message(
        id = "message-id-1",
        text = "Lorem ipsum dolor sit amet, consectetuer adipiscing elit.",
        createdAt = Date(),
        type = MessageType.REGULAR,
    )

    public val message2: Message = Message(
        id = "message-id-2",
        text = "Aenean commodo ligula eget dolor.",
        createdAt = Date(),
        type = MessageType.REGULAR,
    )

    public val messageWithOwnReaction: Message = Message(
        id = "message-id-3",
        text = "Pellentesque leo dui, finibus et nibh et, congue aliquam lectus",
        createdAt = Date(),
        type = MessageType.REGULAR,
        ownReactions = mutableListOf(Reaction(messageId = "message-id-3", type = "haha")),
    )

    public val messageWithError: Message = Message(
        id = "message-id-4",
        text = "Lorem ipsum dolor sqit amet, consectetuer adipiscing elit.",
        createdAt = Date(),
        type = MessageType.ERROR,
    )

//    public val messageWithPoll: Message = Message(
//        id = "message-id-5",
//        createdAt = Date(),
//        type = MessageType.REGULAR,
//        poll = PreviewPollData.poll1,
//    )
}
