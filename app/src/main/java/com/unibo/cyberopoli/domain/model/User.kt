package com.unibo.cyberopoli.domain.model

data class User(
    val id: String,
    val displayName: String,
    val isGuest: Boolean,
    val email: String? = null,
    val avatarUrl: String? = null
)
