package com.unibo.cyberopoli.data.repositories.user

import com.unibo.cyberopoli.data.models.auth.User

interface IUserRepository {
    suspend fun loadUserData(): User

    fun changeAvatar()

    suspend fun updateUserInfo(
        newName: String?,
        newSurname: String?,
    )

    suspend fun changePassword(
        oldPassword: String,
        newPassword: String,
    )

    fun clearUserData()
}
