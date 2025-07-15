package com.example.tujofficehoursapp.data

import kotlinx.coroutines.flow.Flow

// new repository to handle settings data operations
class SettingsRepository(private val dao: UserSettingsDao) {
    fun getSettings(): Flow<UserSettings?> = dao.getSettings()

    suspend fun saveSettings(settings: UserSettings) {
        dao.upsertSettings(settings)
    }
}