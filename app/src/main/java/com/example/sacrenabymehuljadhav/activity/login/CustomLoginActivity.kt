package com.example.sacrenabymehuljadhav.activity.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.sacrenabymehuljadhav.ChatHelper
import com.example.sacrenabymehuljadhav.R
import com.example.sacrenabymehuljadhav.activity.ChannelsActivity
import com.example.sacrenabymehuljadhav.data.UserCredentials
import com.example.sacrenabymehuljadhav.ui.theme.Typography
import com.example.sacrenabymehuljadhav.ui.theme.myBackgroundColor
import com.example.sacrenabymehuljadhav.ui.theme.myDivider
import com.example.sacrenabymehuljadhav.ui.theme.myForegroundColor
import com.example.sacrenabymehuljadhav.ui.theme.primaryDark
import io.getstream.chat.android.compose.ui.theme.ChatTheme
import io.getstream.chat.android.models.User
import io.getstream.result.Error
import kotlinx.coroutines.launch


class CustomLoginActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window?.let { window ->
            window.statusBarColor = ContextCompat.getColor(this, R.color.background)
        }

        setContent {
            ChatTheme(allowUIAutomationTest = true) {
                CustomLoginScreen(
                    onBackButtonClick = ::finish,
                    onLoginButtonClick = { userCredentials ->
                        ChatHelper.initializeSdk(applicationContext, userCredentials.apiKey)

                        lifecycleScope.launch {
                            ChatHelper.connectUser(
                                userCredentials = userCredentials,
                                onSuccess = ::openChannels,
                                onError = ::showError,
                            )
                        }
                    },
                )
            }
        }
    }

    @Composable
    fun CustomLoginScreen(
        onBackButtonClick: () -> Unit,
        onLoginButtonClick: (UserCredentials) -> Unit,
    ) {
        Scaffold(
            topBar = { CustomLoginToolbar(onClick = onBackButtonClick) },
            content = {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(myBackgroundColor)
                        .padding(it)
                        .padding(start = 16.dp, end = 16.dp, top = 16.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    var apiKeyText by remember { mutableStateOf("") }
                    var userIdText by remember { mutableStateOf("") }
                    var userTokenText by remember { mutableStateOf("") }
                    var userNameText by remember { mutableStateOf("") }

                    val isLoginButtonEnabled =
                        apiKeyText.isNotEmpty() && userIdText.isNotEmpty() && userTokenText.isNotEmpty()

                    CustomLoginInputField(
                        hint = "Chat API Key",
                        value = apiKeyText,
                        onValueChange = { apiKeyText = it },
                    )

                    CustomLoginInputField(
                        hint = "User ID",
                        value = userIdText,
                        onValueChange = { userIdText = it },
                    )

                    CustomLoginInputField(
                        hint = "User Token",
                        value = userTokenText,
                        onValueChange = { userTokenText = it },
                    )

                    CustomLoginInputField(
                        hint = "Username(optional)",
                        value = userNameText,
                        onValueChange = { userNameText = it },
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    CustomLoginButton(
                        enabled = isLoginButtonEnabled,
                        onClick = {
                            onLoginButtonClick(
                                UserCredentials(
                                    apiKey = apiKeyText,
                                    user = User(
                                        id = userIdText,
                                        name = userNameText,
                                    ),
                                    token = userTokenText,
                                ),
                            )
                        },
                    )
                    Spacer(modifier = Modifier.height(50.dp))
                }
            },
        )
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun CustomLoginToolbar(onClick: () -> Unit) {
        TopAppBar(
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = myBackgroundColor
            ),
            title = {
                Text(
                    text = stringResource(R.string.custom_user),
                    style = Typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            },
            navigationIcon = {
                IconButton(
                    onClick = onClick,
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.round_arrow_back_ios_24),
                        contentDescription = null,
                        tint = Color.White
                    )
                }
            },

            )
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun CustomLoginInputField(
        hint: String,
        value: String,
        onValueChange: (String) -> Unit,
    ) {
        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
                .height(56.dp),
            value = value,
            onValueChange = { onValueChange(it) },
            singleLine = true,
            label = { Text(hint, style = Typography.bodySmall, color = Color.White) },
            textStyle = Typography.bodyMedium,
            colors = TextFieldDefaults.textFieldColors(
                textColor = primaryDark,
                unfocusedLabelColor = Color.White,
                backgroundColor = Color.Transparent,
                cursorColor = myForegroundColor,
                focusedIndicatorColor = myForegroundColor,
                unfocusedIndicatorColor = myDivider
            ),
        )
    }

    @Composable
    private fun CustomLoginButton(
        enabled: Boolean,
        onClick: () -> Unit = {},
    ) {
        Button(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            enabled = enabled,
            shape = RoundedCornerShape(50.dp),
            colors = ButtonDefaults.buttonColors(
                backgroundColor = primaryDark,
                disabledBackgroundColor = Color.DarkGray,
            ),
            onClick = onClick,
        ) {
            Text(
                text = stringResource(R.string.login_user),
                fontSize = 16.sp,
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                style = Typography.bodyMedium
            )
        }
    }

    private fun openChannels() {
        startActivity(ChannelsActivity.createIntent(this))
        finish()
    }

    private fun showError(error: Error) {
        Toast.makeText(this, "Login failed ${error.message}", Toast.LENGTH_SHORT).show()
    }

    companion object {
        fun createIntent(context: Context): Intent {
            return Intent(context, CustomLoginActivity::class.java)
        }
    }
}
