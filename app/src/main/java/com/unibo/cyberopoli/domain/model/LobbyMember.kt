package com.unibo.cyberopoli.domain.model

import java.time.Instant

data class LobbyMember(
    val user: User,
    val isReady: Boolean,
    val joinedAt: Instant
)
