package com.example.sacrenabymehuljadhav.streamcustomviews.previewdata

import io.getstream.chat.android.models.User


/**
 * Provides sample users that will be used to render component previews.
 */
public object PreviewUserData {

    /**
     * A collection of users with names and avatars.
     */
    public val user1: User = User(
        id = "jc",
        name = "Jc Miñarro",
        image = "https://ca.slack-edge.com/T02RM6X6B-U011KEXDPB2-891dbb8df64f-128",
    )
    public val user2: User = User(
        id = "amit",
        name = "Amit Kumar",
        image = "https://ca.slack-edge.com/T02RM6X6B-U027L4AMGQ3-9ca65ea80b60-128",
    )
    public val user3: User = User(
        id = "belal",
        name = "Belal Khan",
        image = "https://ca.slack-edge.com/T02RM6X6B-U02DAP0G2AV-2072330222dc-128",
    )
    public val user4: User = User(
        id = "dmitrii",
        name = "Dmitrii Bychkov",
        image = "https://ca.slack-edge.com/T02RM6X6B-U01CDPY6YE8-b74b0739493e-128",
    )
    public val user5: User = User(
        id = "filip",
        name = "Filip Babić",
        image = "https://ca.slack-edge.com/T02RM6X6B-U022AFX9D2S-f7bcb3d56180-128",
    )

    private val user6: User = User(
        id = "jaewoong",
        name = "Jaewoong Eum",
        image = "https://ca.slack-edge.com/T02RM6X6B-U02HU1XR9LM-626fb91c334e-128",
    )

    /**
     * Users with specific properties.
     */

    public val userWithImage: User = user1
    public val userWithOnlineStatus: User = user2.copy(online = true)
    public val userWithoutImage: User = user6.copy(image = "")
}