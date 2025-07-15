package com.example.tujofficehoursapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey

// new entity to define the settings table in the Room database
@Entity(tableName = "user_settings")
data class UserSettings(
    @PrimaryKey val id: String = "user_settings", // Using a fixed ID for single-user settings per device
    val timezone: String = "Asia/Tokyo", // Default to JST
    val is24Hour: Boolean = true
)