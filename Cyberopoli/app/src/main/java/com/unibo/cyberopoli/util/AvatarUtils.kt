package com.unibo.cyberopoli.util

import androidx.annotation.DrawableRes
import com.unibo.cyberopoli.R
import com.unibo.cyberopoli.data.models.auth.User
import com.unibo.cyberopoli.data.models.game.GamePlayer

object AvatarUtils {
    private const val AVATAR_MALE_1 = "avatar_male_1"
    private const val AVATAR_MALE_2 = "avatar_male_2"
    private const val AVATAR_MALE_3 = "avatar_male_3"
    private const val AVATAR_FEMALE_1 = "avatar_female_1"
    private const val AVATAR_FEMALE_2 = "avatar_female_2"
    private const val AVATAR_FEMALE_3 = "avatar_female_3"

    const val DEFAULT_AVATAR = AVATAR_MALE_1

    private val AVATAR_LIST = listOf(
        AVATAR_MALE_1,
        AVATAR_MALE_2,
        AVATAR_MALE_3,
        AVATAR_FEMALE_1,
        AVATAR_FEMALE_2,
        AVATAR_FEMALE_3
    )

    @DrawableRes
    fun getAvatarResource(avatarUrl: String?): Int {
        return when (avatarUrl) {
            AVATAR_MALE_1 -> R.drawable.avatar_male_1
            AVATAR_MALE_2 -> R.drawable.avatar_male_2
            AVATAR_MALE_3 -> R.drawable.avatar_male_3
            AVATAR_FEMALE_1 -> R.drawable.avatar_female_1
            AVATAR_FEMALE_2 -> R.drawable.avatar_female_2
            AVATAR_FEMALE_3 -> R.drawable.avatar_female_3
            else -> R.drawable.avatar_male_1
        }
    }

    @DrawableRes
    fun getAvatarResourceForPlayer(player: GamePlayer): Int {
        return getAvatarResource(player.user?.avatarUrl)
    }

    @DrawableRes
    fun getAvatarResourceForUser(user: User?): Int {
        return getAvatarResource(user?.avatarUrl)
    }

    fun getNextAvatar(currentAvatar: String?): String {
        val safeCurrentAvatar = currentAvatar ?: DEFAULT_AVATAR
        val currentIndex = AVATAR_LIST.indexOf(safeCurrentAvatar).takeIf { it >= 0 } ?: 0
        val nextIndex = if (currentIndex == AVATAR_LIST.lastIndex) 0 else currentIndex + 1
        return AVATAR_LIST[nextIndex]
    }
}