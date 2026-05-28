package com.example.data

import android.content.Context
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class HabitRepository(private val habitDao: HabitDao, context: Context) {
    val habits: Flow<List<Habit>> = habitDao.getAllHabits()
    val logs: Flow<List<HabitLog>> = habitDao.getAllLogs()

    private val sharedPrefs = context.getSharedPreferences("habit_pulse_prefs", Context.MODE_PRIVATE)
    private val _username = MutableStateFlow(sharedPrefs.getString("username", "") ?: "") // empty initially for onboarding
    val username: StateFlow<String> = _username

    private val _themeIndex = MutableStateFlow(sharedPrefs.getInt("theme_index", 0))
    val themeIndex: StateFlow<Int> = _themeIndex

    private val _shouldShowStarterDialog = MutableStateFlow(sharedPrefs.getBoolean("show_starter_dialog", true))
    val shouldShowStarterDialog: StateFlow<Boolean> = _shouldShowStarterDialog

    private val _isOnboarded = MutableStateFlow(sharedPrefs.getBoolean("is_onboarded", false))
    val isOnboarded: StateFlow<Boolean> = _isOnboarded

    private val _preferredGoal = MutableStateFlow(sharedPrefs.getString("preferred_goal", "Mindfulness") ?: "Mindfulness")
    val preferredGoal: StateFlow<String> = _preferredGoal

    private val _preferredTimeOfDay = MutableStateFlow(sharedPrefs.getString("preferred_time_of_day", "Morning") ?: "Morning")
    val preferredTimeOfDay: StateFlow<String> = _preferredTimeOfDay

    fun updateUsername(newName: String) {
        sharedPrefs.edit().putString("username", newName).apply()
        _username.value = newName
    }

    fun updateThemeIndex(index: Int) {
        sharedPrefs.edit().putInt("theme_index", index).apply()
        _themeIndex.value = index
    }

    fun setShouldShowStarterDialog(show: Boolean) {
        sharedPrefs.edit().putBoolean("show_starter_dialog", show).apply()
        _shouldShowStarterDialog.value = show
    }

    fun completeOnboarding(
        name: String,
        themeIdx: Int,
        starterRituals: Boolean,
        goal: String,
        timeOfDay: String
    ) {
        sharedPrefs.edit()
            .putString("username", name)
            .putInt("theme_index", themeIdx)
            .putBoolean("show_starter_dialog", starterRituals)
            .putString("preferred_goal", goal)
            .putString("preferred_time_of_day", timeOfDay)
            .putBoolean("is_onboarded", true)
            .apply()
        _username.value = name
        _themeIndex.value = themeIdx
        _shouldShowStarterDialog.value = starterRituals
        _preferredGoal.value = goal
        _preferredTimeOfDay.value = timeOfDay
        _isOnboarded.value = true
    }

    fun resetOnboardingForTesting() {
        sharedPrefs.edit()
            .putBoolean("is_onboarded", false)
            .putString("username", "")
            .apply()
        _isOnboarded.value = false
        _username.value = ""
    }

    suspend fun insertHabit(habit: Habit): Long {
        return habitDao.insertHabit(habit)
    }

    suspend fun updateHabit(habit: Habit) {
        habitDao.updateHabit(habit)
    }

    suspend fun deleteHabit(habitId: Int) {
        habitDao.deleteLogsByHabitId(habitId)
        habitDao.deleteHabitById(habitId)
    }

    suspend fun addLog(habitId: Int, dateStr: String) {
        habitDao.insertLog(HabitLog(habitId, dateStr))
    }

    suspend fun removeLog(habitId: Int, dateStr: String) {
        habitDao.deleteLog(habitId, dateStr)
    }

    suspend fun clearAllData() {
        habitDao.deleteAllHabits()
        habitDao.deleteAllLogs()
    }
}
