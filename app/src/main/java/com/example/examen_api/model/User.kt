package com.example.examen_api.model

data class User(
    val id: Int? = null,
    val name: String,
    val phone: String,
    val email: String,
    // Mapeamos el objeto anidado del JSON
    val image: UserImage? = null
)

data class UserImage(
    val url: String
)