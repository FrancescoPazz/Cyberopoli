package com.unibo.cyberopoli.data.repositories.user

import android.util.Log
import com.unibo.cyberopoli.data.models.auth.User
import com.unibo.cyberopoli.util.AvatarUtils
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.unibo.cyberopoli.data.repositories.user.IUserRepository as DomainUserRepository

class UserRepository(
    private val supabase: SupabaseClient,
) : DomainUserRepository {
    override suspend fun getUser(): User {
        val userId = supabase.auth.currentUserOrNull()?.id
            ?: throw IllegalStateException("User not logged in")
        return try {
            supabase.from("users").select {
                filter {
                    eq("id", userId)
                }
            }.decodeSingle<User>()
        } catch (e: Exception) {
            Log.e("UserRepository", "Exception loading user data", e)
            throw e
        }
    }

    override suspend fun changeAvatar() {
        val userId = supabase.auth.currentUserOrNull()?.id ?: run {
            return
        }

        val currentAvatar = getUser().avatarUrl ?: AvatarUtils.DEFAULT_AVATAR
        val newAvatar = AvatarUtils.getNextAvatar(currentAvatar)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                supabase.from("users").update(mapOf("avatar_url" to newAvatar)) {
                    filter {
                        eq("id", userId)
                    }
                }
            } catch (e: Exception) {
                Log.e("UserRepository", "Error updating avatar", e)
            }
        }
    }

    override suspend fun updateUserInfo(
        newName: String?,
        newSurname: String?,
    ) {
        val userId = supabase.auth.currentUserOrNull()?.id ?: return
        try {
            supabase.from("users").update(mapOf("name" to newName, "surname" to newSurname)) {
                filter {
                    eq("id", userId)
                }
                select()
            }.decodeSingle<User>()
        } catch (e: Exception) {
            Log.e("UserRepository", "Error loading user data", e)
        }
    }

    override suspend fun changePassword(
        oldPassword: String,
        newPassword: String,
    ) {
        val email = supabase.auth.currentUserOrNull()?.email ?: return
        supabase.auth.signInWith(Email) {
            this.email = email
            this.password = oldPassword
        }
        supabase.auth.updateUser {
            password = newPassword
        }
    }
}
