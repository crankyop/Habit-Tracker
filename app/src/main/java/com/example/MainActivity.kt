package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModelProvider
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.HabitDatabase
import com.example.data.HabitRepository
import com.example.ui.HabitTrackerApp
import com.example.ui.HabitViewModel
import com.example.ui.HabitViewModelFactory
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Beautiful modern Android status and navigation bar rendering
        enableEdgeToEdge()

        // Room database bootstrap
        val database = HabitDatabase.getDatabase(this)
        val repository = HabitRepository(database.habitDao, this)
        val viewModel = ViewModelProvider(
            this, 
            HabitViewModelFactory(repository)
        )[HabitViewModel::class.java]

        setContent {
            val themeIndex by viewModel.themeIndex.collectAsStateWithLifecycle()
            MyApplicationTheme(themeIndex = themeIndex) {
                HabitTrackerApp(viewModel = viewModel)
            }
        }
    }
}
