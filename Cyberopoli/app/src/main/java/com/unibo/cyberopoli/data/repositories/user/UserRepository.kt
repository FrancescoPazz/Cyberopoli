package com.unibo.cyberopoli.data.repositories.user

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.unibo.cyberopoli.data.models.auth.User
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.unibo.cyberopoli.data.repositories.user.IUserRepository as DomainUserRepository

class UserRepository(
    private val supabase: SupabaseClient
) : DomainUserRepository {
    val currentUserLiveData = MutableLiveData<User?>()

    override suspend fun loadUserData(): User {
        val userId = supabase.auth.currentUserOrNull()?.id ?: throw IllegalStateException("User not logged in")
        try {
            val user = supabase.from("users").select {
                filter {
                    eq("id", userId)
                }
            }.decodeSingle<User>()
            currentUserLiveData.postValue(user)
            return user
        } catch (e: Exception) {
            throw e
        }
    }

    fun changeAvatar() {
        val userId = supabase.auth.currentUserOrNull()?.id ?: run {
            return
        }

        val currentAvatar = currentUserLiveData.value?.avatarUrl ?: "avatar_male_1"
        val avatarList =
            listOf("avatar_male_1", "avatar_male_2", "avatar_female_1", "avatar_female_2")
        val currentIndex = avatarList.indexOf(currentAvatar).takeIf { it >= 0 } ?: 0
        val nextIndex = if (currentIndex == avatarList.lastIndex) 0 else currentIndex + 1
        val newAvatar = avatarList[nextIndex]

        CoroutineScope(Dispatchers.IO).launch {
            try {
                supabase.from("users").update(mapOf("avatar_url" to newAvatar)) {
                    filter {
                        eq("id", userId)
                    }
                }

                currentUserLiveData.postValue(currentUserLiveData.value?.copy(avatarUrl = newAvatar))
            } catch (e: Exception) {
                Log.e("UserRepository", "Error updating avatar", e)
            }
        }
    }

    override suspend fun updateUserInfo(newName: String?, newSurname: String?) {
        val userId = supabase.auth.currentUserOrNull()?.id ?: return
        try {
            val user = supabase.from("users").update(mapOf("name" to newName, "surname" to newSurname)) {
                filter {
                    eq("id", userId)
                }
                select()
            }.decodeSingle<User>()

            currentUserLiveData.postValue(user)
        } catch (e: Exception) {
            Log.e("UserRepository", "Error loading user data", e)
        }

        loadUserData()
    }

    override suspend fun changePassword(oldPassword: String, newPassword: String) {
        val email = supabase.auth.currentUserOrNull()?.email ?: return
        supabase.auth.signInWith(Email) {
            this.email = email
            this.password = oldPassword
        }
        supabase.auth.updateUser {
            password = newPassword
        }
    }

    fun clearUserData() {
        currentUserLiveData.value = null
    }
}
