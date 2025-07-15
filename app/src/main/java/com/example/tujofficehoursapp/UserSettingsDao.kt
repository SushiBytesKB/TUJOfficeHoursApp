package com.example.tujofficehoursapp.data

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

// new DAO for interacting with the UserSettings table
@Dao
interface UserSettingsDao {
    @Upsert // Inserts if new, updates if it exists
    suspend fun upsertSettings(settings: UserSettings)

    @Query("SELECT * FROM user_settings WHERE id = 'user_settings'")
    fun getSettings(): Flow<UserSettings?>
}