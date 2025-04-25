package com.unibo.cyberopoli.data.repositories

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.unibo.cyberopoli.data.models.auth.UserData
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UserRepository(
    private val supabase: SupabaseClient
) {
    val currentUserLiveData = MutableLiveData<UserData?>()

    fun loadUserData() {
        val userId = supabase.auth.currentUserOrNull()?.id ?: Log.d(
            "UserRepository", "loadUserData: no authenticated user"
        )

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val resp = supabase.from("users").select {
                    filter {
                        eq("id", userId)
                    }
                }

                val userData = resp.decodeList<UserData>()
                Log.d("UserRepository", "loadUserData: $userData")
                if (userData.isNotEmpty()) {
                    currentUserLiveData.postValue(userData[0])
                } else {
                    Log.d("UserRepository", "loadUserData: no user data found")
                    currentUserLiveData.postValue(null)
                }

            } catch (e: Exception) {
                Log.e("UserRepository", "Exception loading user data", e)
                currentUserLiveData.postValue(null)
            }
        }
    }

    fun changeAvatar() {
        val userId = supabase.auth.currentUserOrNull()?.id ?: run {
            Log.d("UserRepository", "changeAvatar: no authenticated user")
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

                loadUserData()
            } catch (e: Exception) {
                Log.e("UserRepository", "Error updating avatar", e)
            }
        }
    }

    fun clearUserData() {
        currentUserLiveData.value = null
    }
}
