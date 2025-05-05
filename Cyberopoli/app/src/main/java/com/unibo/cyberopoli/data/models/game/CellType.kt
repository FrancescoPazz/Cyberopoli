package com.unibo.cyberopoli.data.models.game

import com.unibo.cyberopoli.R

enum class CellType(val resource: Int?) {
    START(R.drawable.ic_start),
    YOUTUBE(R.drawable.ic_youtube),
    WHATSAPP(R.drawable.ic_whatsapp),
    TIKTOK(R.drawable.ic_tiktok),
    INSTAGRAM(R.drawable.ic_instagram),
    FACEBOOK(R.drawable.ic_facebook),
    TELEGRAM(R.drawable.ic_telegram),
    DISCORD(R.drawable.ic_discord),
    SNAPCHAT(R.drawable.ic_snap),
    LINKEDIN(R.drawable.ic_linkedin),
    REDDIT(R.drawable.ic_reddit),
    TWITCH(R.drawable.ic_twitch),
    CHANCE(R.drawable.ic_chance),
    HACKER(R.drawable.ic_hacker),
    COMMON(null)
}