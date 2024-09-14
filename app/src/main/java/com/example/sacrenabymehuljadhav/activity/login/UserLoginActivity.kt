package com.example.sacrenabymehuljadhav.activity.login

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.Card
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.sacrenabymehuljadhav.ChatHelper
import com.example.sacrenabymehuljadhav.R
import com.example.sacrenabymehuljadhav.activity.ChannelsActivity
import com.example.sacrenabymehuljadhav.data.PredefinedUserCredentials
import com.example.sacrenabymehuljadhav.data.UserCredentials
import com.example.sacrenabymehuljadhav.ui.theme.Typography
import com.example.sacrenabymehuljadhav.ui.theme.myBackgroundColor
import com.example.sacrenabymehuljadhav.ui.theme.primaryDark
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.compose.ui.components.avatar.ImageAvatar
import io.getstream.chat.android.compose.ui.components.avatar.UserAvatar
import io.getstream.chat.android.compose.ui.theme.ChatTheme
import kotlinx.coroutines.launch


class UserLoginActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window?.let { window ->
            window.statusBarColor = ContextCompat.getColor(this, R.color.background)
        }

        setContent {
            ChatTheme(allowUIAutomationTest = true) {
                UserLoginScreen(
                    onUserItemClick = { userCredentials ->
                        lifecycleScope.launch {
                            if (ChatClient.instance().config.apiKey != userCredentials.apiKey) {
                                // If the user attempted to login with custom credentials on the custom
                                // login screen then we need to reinitialize the SDK with our API key.
                                ChatHelper.initializeSdk(applicationContext, userCredentials.apiKey)
                            }
                            ChatHelper.connectUser(userCredentials = userCredentials)
                            openChannels()
                        }
                    },
                    onCustomLoginClick = ::openCustomLogin,
                )
            }
        }
    }

    @Composable
    fun UserLoginScreen(
        onUserItemClick: (UserCredentials) -> Unit,
        onCustomLoginClick: () -> Unit,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(myBackgroundColor),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.height(36.dp))

            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = null,
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                modifier = Modifier.padding(horizontal = 16.dp),
                text = stringResource(R.string.sacrena),
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                style = Typography.titleLarge,
                color = Color.White,
            )

            Spacer(modifier = Modifier.height(12.dp))

            Spacer(modifier = Modifier.weight(1f))

            LazyColumn(
                modifier = Modifier
                    .testTag("Stream_UserLogin")
                    .fillMaxWidth()

            ) {
                items(items = PredefinedUserCredentials.availableUsers) { userCredentials ->
                    UserLoginItem(
                        modifier = Modifier.testTag("Stream_UserLoginItem"),
                        userCredentials = userCredentials,
                        onItemClick = onUserItemClick,
                    )

                }

                item {
                    CustomLoginItem(onItemClick = onCustomLoginClick)
                }
            }

            Spacer(modifier = Modifier.height(50.dp))

            ClickableText()

        }
    }

    @Composable
    fun ClickableText() {

        val clickableText = "Mehul Jadhav"
        val url = "https://zurichblade.github.io/my-portfolio/"

        // Create an AnnotatedString
        val annotatedString = buildAnnotatedString {
            withStyle(
                style = SpanStyle(
                    fontSize = 14.sp,
                    color = ChatTheme.colors.textLowEmphasis
                )
            ) {
                append("Developed by ")
            }
            withStyle(
                style = SpanStyle(
                    fontSize = 14.sp,
                    color = primaryDark,
                )
            ) {
                pushStringAnnotation(tag = "clickable", annotation = url)
                append(clickableText)
            }
        }

        val context = LocalContext.current

        ClickableText(
            text = annotatedString,
            onClick = { offset ->
                val annotations = annotatedString.getStringAnnotations(
                    tag = "clickable",
                    start = offset,
                    end = offset
                )
                if (annotations.isNotEmpty()) {
                    val url = annotations.first().item
                    val intent = Intent(Intent.ACTION_VIEW).apply {
                        data = Uri.parse(url)
                    }
                    context.startActivity(intent)
                }
            },
            modifier = Modifier.padding(16.dp),
            style = Typography.bodyMedium.copy(
                color = ChatTheme.colors.textLowEmphasis,
                fontSize = 14.sp
            )
        )
    }

    /**
     * Represents a user whose credentials will be used for login.
     */
    @Composable
    fun UserLoginItem(
        modifier: Modifier,
        userCredentials: UserCredentials,
        onItemClick: (UserCredentials) -> Unit,
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 50.dp, vertical = 6.dp)
                .clickable(onClick = { onItemClick(userCredentials) },
                    indication = rememberRipple(),
                    interactionSource = remember { MutableInteractionSource() }),
            backgroundColor = primaryDark,
            shape = RoundedCornerShape(50),
            elevation = 4.dp,
        ) {
            Row(
                modifier = Modifier.padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                UserAvatar(
                    modifier = Modifier.size(46.dp),
                    user = userCredentials.user,
                )

                Text(
                    text = userCredentials.user.name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    style = Typography.titleMedium
                )

                Icon(
                    modifier = Modifier
                        .wrapContentSize()
                        .padding(end = 16.dp),
                    painter = painterResource(id = R.drawable.baseline_arrow_forward_24),
                    contentDescription = null,
                    tint = Color.Black,
                )
            }
        }
    }

    /**
     * Represents the "Advanced option" list item.
     */
    @Composable
    private fun CustomLoginItem(onItemClick: () -> Unit) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 50.dp, vertical = 6.dp)
                .clickable(onClick = { onItemClick() },
                    indication = rememberRipple(),
                    interactionSource = remember { MutableInteractionSource() }),
            backgroundColor = primaryDark,
            shape = RoundedCornerShape(50),
            elevation = 4.dp,
        ) {
            Row(
                modifier = Modifier.padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                ImageAvatar(
                    modifier = Modifier.size(46.dp),
                    painter = painterResource(id = R.drawable.baseline_settings_24)
                )

                Text(
                    text = "Custom User",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    style = Typography.titleMedium
                )

                Icon(
                    modifier = Modifier
                        .wrapContentSize()
                        .padding(end = 16.dp),
                    painter = painterResource(id = R.drawable.baseline_arrow_forward_24),
                    contentDescription = null,
                    tint = Color.Black,
                )
            }
        }
    }

    @Composable
    private fun DividerItem() {
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(0.5.dp)
                .background(color = ChatTheme.colors.borders),
        )
    }

    private fun openChannels() {
        startActivity(ChannelsActivity.createIntent(this))
        finish()
    }

    private fun openCustomLogin() {
        startActivity(CustomLoginActivity.createIntent(this))
    }

    companion object {
        fun createIntent(context: Context): Intent {
            return Intent(context, UserLoginActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
        }
    }
}
