package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.Habit
import com.example.data.HabitLog
import com.example.data.HabitRepository
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class HabitUiState(
    val habits: List<Habit> = emptyList(),
    val logsByDate: Map<String, Set<Int>> = emptyMap(), // dateStr -> set of habitId
    val selectedDate: LocalDate = LocalDate.now()
)

class HabitViewModel(private val repository: HabitRepository) : ViewModel() {

    val username: StateFlow<String> = repository.username
    val themeIndex: StateFlow<Int> = repository.themeIndex
    val shouldShowStarterDialog: StateFlow<Boolean> = repository.shouldShowStarterDialog
    val isOnboarded: StateFlow<Boolean> = repository.isOnboarded
    val preferredGoal: StateFlow<String> = repository.preferredGoal
    val preferredTimeOfDay: StateFlow<String> = repository.preferredTimeOfDay

    fun updateUsername(newName: String) {
        repository.updateUsername(newName)
    }

    fun updateThemeIndex(index: Int) {
        repository.updateThemeIndex(index)
    }

    fun setShouldShowStarterDialog(show: Boolean) {
        repository.setShouldShowStarterDialog(show)
    }

    fun completeOnboarding(
        name: String,
        themeIdx: Int,
        starterRituals: Boolean,
        goal: String,
        timeOfDay: String
    ) {
        viewModelScope.launch {
            repository.completeOnboarding(name, themeIdx, starterRituals, goal, timeOfDay)
            if (starterRituals) {
                seedInitialHabits()
            }
        }
    }

    private val _selectedDate = MutableStateFlow(LocalDate.now())

    val uiState: StateFlow<HabitUiState> = combine(
        repository.habits,
        repository.logs,
        _selectedDate
    ) { habits, logs, selectedDate ->
        // Group logs by date
        val logsByDate = logs.groupBy { it.dateStr }
            .mapValues { entry -> entry.value.map { it.habitId }.toSet() }

        // Automatically calculate and sync streaks for habits
        val updatedHabits = habits.map { habit ->
            val habitLogs = logs.filter { it.habitId == habit.id }
                .mapNotNull {
                    try {
                        LocalDate.parse(it.dateStr)
                    } catch (e: Exception) {
                        null
                    }
                }.toSet()

            val (currentStreak, maxStreak) = calculateStreak(habitLogs)
            if (habit.streak != currentStreak || habit.maxStreak != maxStreak) {
                // Return updated habit and asynchronously update database
                val refreshed = habit.copy(streak = currentStreak, maxStreak = maxStreak)
                viewModelScope.launch {
                    repository.updateHabit(refreshed)
                }
                refreshed
            } else {
                habit
            }
        }

        HabitUiState(
            habits = updatedHabits,
            logsByDate = logsByDate,
            selectedDate = selectedDate
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HabitUiState()
    )

    fun selectDate(date: LocalDate) {
        _selectedDate.value = date
    }

    fun addHabit(
        name: String,
        description: String,
        category: String,
        frequency: String,
        targetDaysPerWeek: Int,
        colorHex: String,
        iconName: String
    ) {
        viewModelScope.launch {
            val habit = Habit(
                name = name,
                description = description,
                category = category,
                frequency = frequency,
                targetDaysPerWeek = targetDaysPerWeek,
                colorHex = colorHex,
                iconName = iconName,
                streak = 0,
                maxStreak = 0
            )
            repository.insertHabit(habit)
        }
    }

    fun toggleHabit(habit: Habit, date: LocalDate) {
        viewModelScope.launch {
            val dateStr = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
            val completionLogs = uiState.value.logsByDate[dateStr] ?: emptySet()
            if (completionLogs.contains(habit.id)) {
                repository.removeLog(habit.id, dateStr)
            } else {
                repository.addLog(habit.id, dateStr)
            }
        }
    }

    fun deleteHabit(habitId: Int) {
        viewModelScope.launch {
            repository.deleteHabit(habitId)
        }
    }

    fun getCompletionRateForHabit(habitId: Int, daysToLookBack: Int): Float {
        val today = LocalDate.now()
        val totalDays = daysToLookBack.toFloat()
        var completedCount = 0
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val state = uiState.value

        for (i in 0 until daysToLookBack) {
            val checkDateStr = today.minusDays(i.toLong()).format(formatter)
            val completions = state.logsByDate[checkDateStr] ?: emptySet()
            if (completions.contains(habitId)) {
                completedCount++
            }
        }
        return if (totalDays > 0) completedCount / totalDays else 0f
    }

    fun seedInitialHabits() {
        viewModelScope.launch {
            val h1 = Habit(
                name = "Morning Hydration",
                description = "2L Goal • Hydrate clean water",
                category = "Health",
                frequency = "Daily",
                targetDaysPerWeek = 7,
                colorHex = "#6750A4",
                iconName = "Water",
                streak = 5,
                maxStreak = 12
            )
            val h2 = Habit(
                name = "Deep Meditation",
                description = "20 mins • Breathe deeply",
                category = "Mind",
                frequency = "Daily",
                targetDaysPerWeek = 7,
                colorHex = "#9D4EDD",
                iconName = "Mind",
                streak = 12,
                maxStreak = 15
            )
            val h3 = Habit(
                name = "Focus Reading",
                description = "30 pages • Enrich thoughts",
                category = "Productivity",
                frequency = "Daily",
                targetDaysPerWeek = 5,
                colorHex = "#4E9F3D",
                iconName = "Book",
                streak = 3,
                maxStreak = 9
            )
            
            val h1Id = repository.insertHabit(h1).toInt()
            val h2Id = repository.insertHabit(h2).toInt()
            val h3Id = repository.insertHabit(h3).toInt()

            // Pre-seed some logs as well so streaks match the design spec exactly
            val today = LocalDate.now()
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

            // Seed h1 logs
            for (i in 0 until 5) {
                repository.addLog(h1Id, today.minusDays(i.toLong()).format(formatter))
            }
            // Seed h2 logs
            for (i in 0 until 12) {
                repository.addLog(h2Id, today.minusDays(i.toLong()).format(formatter))
            }
            // Seed h3 logs
            for (i in 0 until 3) {
                repository.addLog(h3Id, today.minusDays(i.toLong()).format(formatter))
            }
        }
    }

    fun clearAllData() {
        viewModelScope.launch {
            repository.clearAllData()
            repository.resetOnboardingForTesting()
        }
    }

    private fun calculateStreak(logsForHabit: Set<LocalDate>): Pair<Int, Int> {
        if (logsForHabit.isEmpty()) return Pair(0, 0)
        
        val sortedDates = logsForHabit.sortedDescending()
        val today = LocalDate.now()
        val yesterday = today.minusDays(1)
        
        val mostRecent = sortedDates.first()
        if (mostRecent != today && mostRecent != yesterday) {
            return Pair(0, calculateMaxStreak(sortedDates))
        }
        
        var currentStreak = 0
        var checkDate = mostRecent
        
        for (date in sortedDates) {
            if (date == checkDate) {
                currentStreak++
                checkDate = checkDate.minusDays(1)
            } else {
                break
            }
        }
        
        return Pair(currentStreak, maxOf(currentStreak, calculateMaxStreak(sortedDates)))
    }

    private fun calculateMaxStreak(sortedDates: List<LocalDate>): Int {
        if (sortedDates.isEmpty()) return 0
        var maxStreak = 0
        var currentStreak = 0
        var checkDate: LocalDate? = null
        
        for (date in sortedDates) {
            if (checkDate == null || date == checkDate) {
                currentStreak++
                checkDate = date.minusDays(1)
            } else {
                maxStreak = maxOf(maxStreak, currentStreak)
                currentStreak = 1
                checkDate = date.minusDays(1)
            }
        }
        maxStreak = maxOf(maxStreak, currentStreak)
        return maxStreak
    }
}

class HabitViewModelFactory(private val repository: HabitRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HabitViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HabitViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
