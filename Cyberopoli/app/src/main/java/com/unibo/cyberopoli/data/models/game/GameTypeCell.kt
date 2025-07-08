package com.unibo.cyberopoli.data.models.game

import com.unibo.cyberopoli.R

enum class GameTypeCell(val resource: Int? = null) {
    START(R.drawable.ic_start),

    YOUTUBE(R.drawable.ic_youtube), WHATSAPP(R.drawable.ic_whatsapp), TIKTOK(R.drawable.ic_tiktok), INSTAGRAM(
        R.drawable.ic_instagram
    ),
    FACEBOOK(R.drawable.ic_facebook), TELEGRAM(R.drawable.ic_telegram), DISCORD(R.drawable.ic_discord), TWITCH(
        R.drawable.ic_twitch
    ),

    CHANCE(R.drawable.ic_chance), HACKER(R.drawable.ic_hacker), BLOCK(R.drawable.ic_stop), BROKEN_ROUTER(
        R.drawable.ic_broken_router
    ),
    VPN(R.drawable.ic_vpn), OCCUPIED(R.drawable.ic_content), COMMON,
}
