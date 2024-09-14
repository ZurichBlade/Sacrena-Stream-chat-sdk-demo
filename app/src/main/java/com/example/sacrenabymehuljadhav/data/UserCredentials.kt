package com.example.sacrenabymehuljadhav.data

import io.getstream.chat.android.models.User

data class UserCredentials(
    val apiKey: String,
    val user: User,
    val token: String,
)
