package com.unibo.cyberopoli.data.repositories.user

import com.unibo.cyberopoli.data.models.auth.User

interface IUserInterface {
    suspend fun loadUserData(): User
}