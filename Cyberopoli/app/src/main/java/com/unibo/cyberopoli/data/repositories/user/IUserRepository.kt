package com.unibo.cyberopoli.data.repositories.user

import com.unibo.cyberopoli.data.models.auth.User

interface IUserRepository {
    suspend fun getUser() : User

    suspend fun changeAvatar()

    suspend fun updateUserInfo(
        newName: String?,
        newSurname: String?,
    )

    suspend fun changePassword(
        oldPassword: String,
        newPassword: String,
    )
}
