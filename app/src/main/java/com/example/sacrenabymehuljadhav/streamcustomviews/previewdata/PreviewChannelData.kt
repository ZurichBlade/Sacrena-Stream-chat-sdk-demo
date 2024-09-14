package com.example.sacrenabymehuljadhav.streamcustomviews.previewdata

import io.getstream.chat.android.models.Channel
import io.getstream.chat.android.models.Member
import java.util.Date

/**
 * Provides sample channels that will be used to render previews.
 */
public object PreviewChannelData {

    public val channelWithImage: Channel = Channel(
        type = "channelType",
        id = "channelId1",
        image = "https://picsum.photos/id/237/128/128",
        members = listOf(
            Member(user = PreviewUserData.user1),
            Member(user = PreviewUserData.user2),
        ),
    )

    public val channelWithOneUser: Channel = Channel(
        type = "channelType",
        id = "channelId2",
        members = listOf(
            Member(user = PreviewUserData.user1),
        ),
    )

    public val channelWithOnlineUser: Channel = Channel(
        type = "channelType",
        id = "channelId2",
        members = listOf(
            Member(user = PreviewUserData.user1),
            Member(user = PreviewUserData.user2.copy(online = true)),
        ),
    )

    public val channelWithFewMembers: Channel = Channel(
        type = "channelType",
        id = "channelId3",
        members = listOf(
            Member(user = PreviewUserData.user1),
            Member(user = PreviewUserData.user2),
            Member(user = PreviewUserData.user3),
        ),
    )

    public val channelWithManyMembers: Channel = Channel(
        type = "channelType",
        id = "channelId4",
        members = listOf(
            Member(user = PreviewUserData.user1),
            Member(user = PreviewUserData.user2),
            Member(user = PreviewUserData.user3),
            Member(user = PreviewUserData.user4),
            Member(user = PreviewUserData.userWithoutImage),
        ),
    )

    public val channelWithMessages: Channel = Channel(
        type = "channelType",
        id = "channelId5",
        members = listOf(
            Member(user = PreviewUserData.user1),
            Member(user = PreviewUserData.user2),
        ),
        messages = listOf(
            PreviewMessageData.message1,
            PreviewMessageData.message2,
        ),
        lastMessageAt = Date(),
    )
}