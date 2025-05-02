package com.unibo.cyberopoli.data.repositories.profile

import com.unibo.cyberopoli.data.models.auth.User

interface IUserInterface {
    fun loadUserData(): User?

    fun loadUserData(userId: String): User?
}