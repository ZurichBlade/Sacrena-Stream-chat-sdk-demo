

package com.example.sacrenabymehuljadhav.streamcustomviews.componenets

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.sacrenabymehuljadhav.ui.theme.myForegroundColor

/**
 * Component that represents an online indicator to be used with
 * [io.getstream.chat.android.compose.ui.components.avatar.UserAvatar].
 *
 * @param modifier Modifier for styling.
 */
@Composable
public fun OnlineIndicator(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(10.dp)
            .offset(x = 2.dp, y = (-2).dp)
            .padding(0.dp)
            .background(myForegroundColor, CircleShape),
    )
}
