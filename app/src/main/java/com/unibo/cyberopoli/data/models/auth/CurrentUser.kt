package com.unibo.cyberopoli.data.models.auth

sealed class CurrentUser {
    data class Registered(val data: UserData) : CurrentUser()
    data class Guest(val data: GuestData) : CurrentUser()
}