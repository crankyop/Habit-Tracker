package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "habits")
data class Habit(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val description: String,
    val category: String,
    val frequency: String, // "Daily" or "Weekly"
    val targetDaysPerWeek: Int,
    val colorHex: String,
    val iconName: String,
    val streak: Int,
    val maxStreak: Int
)
