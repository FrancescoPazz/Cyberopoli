package com.unibo.cyberopoli.data.repositories.settings

import com.unibo.cyberopoli.data.models.theme.Theme

interface ISettingsRepository {
    suspend fun setTheme(theme: Theme)
}
