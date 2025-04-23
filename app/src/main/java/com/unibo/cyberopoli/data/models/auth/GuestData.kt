package com.unibo.cyberopoli.data.models.auth

import com.google.firebase.Timestamp

data class GuestData(
    val uid: String = "",
    val name: String = "",
    val creationDate: Timestamp? = null
)