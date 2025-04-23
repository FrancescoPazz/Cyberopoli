package com.unibo.cyberopoli.data.models.auth

import java.security.Timestamp


data class GuestData(
    val uid: String = "",
    val name: String = "",
    val creationDate: Timestamp? = null
)