package com.example.sacrenabymehuljadhav.data

import io.getstream.chat.android.models.User


object PredefinedUserCredentials {

    const val API_KEY: String = "7vc4fsx6erje"

    val availableUsers: List<UserCredentials> = listOf(
        UserCredentials(
            apiKey = API_KEY,
            user = User(
                "Jenna_98846a13-d436-47b9-9f2f-0a86eb59ba5d",
                name = "Jenna",
                image = "https://reqres.in/img/faces/7-image.jpg",
            ),
            token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX2lkIjoiSmVubmFfOTg4NDZhMTMtZDQzNi00N2I5LTlmMmYtMGE4NmViNTliYTVkIn0.SpD4CqccSf04lHDJFEoPxoCvkrZ1ydJq1aMSKEaB-_g",
        ),
        UserCredentials(
            apiKey = API_KEY,
            user = User(
                "Emery_f0fa4e7a-8acf-463b-9686-2f06a9d4f3e3",
                name = "Emery",
                image = "https://reqres.in/img/faces/2-image.jpg",
            ),
            token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX2lkIjoiRW1lcnlfZjBmYTRlN2EtOGFjZi00NjNiLTk2ODYtMmYwNmE5ZDRmM2UzIn0.Fq2R8fWrTK7C8eIRo8DlxImeZRrCBgPuwFy9S47_e5s",
        ),
        UserCredentials(
            apiKey = API_KEY,
            user = User(
                "Alice_1f932c12-17c7-4054-b90d-b9d9613ec84a",
                name = "Alice",
                image = "https://reqres.in/img/faces/12-image.jpg",
            ),
            token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX2lkIjoiQWxpY2VfMWY5MzJjMTItMTdjNy00MDU0LWI5MGQtYjlkOTYxM2VjODRhIn0._2NFYYatvGnWBpJdegvHeuqb9HYV1AyIl0UBVYET8g8",
        ),
    )
}
