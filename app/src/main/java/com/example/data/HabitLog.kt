package com.example.data

import androidx.room.Entity

@Entity(tableName = "habit_logs", primaryKeys = ["habitId", "dateStr"])
data class HabitLog(
    val habitId: Int,
    val dateStr: String // Date in format "yyyy-MM-dd"
)
