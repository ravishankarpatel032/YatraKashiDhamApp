package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "saved_places")
data class SavedPlace(
    @PrimaryKey val id: String,
    val name: String,
    val type: String, // "Ghat", "Temple", "Food", "Other"
    val description: String,
    val timestamp: Long = System.currentTimeMillis()
)
