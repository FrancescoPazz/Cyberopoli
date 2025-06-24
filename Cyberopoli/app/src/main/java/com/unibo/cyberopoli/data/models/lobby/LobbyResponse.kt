package com.unibo.cyberopoli.data.models.lobby

sealed interface LobbyResponse {
    data object Success : LobbyResponse

    data object AlreadyStarted : LobbyResponse
}
