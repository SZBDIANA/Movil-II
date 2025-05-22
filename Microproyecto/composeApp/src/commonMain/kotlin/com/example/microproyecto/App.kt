package com.example.microproyecto


import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*




@Composable
fun App() {

    MaterialTheme {
        // Puedes hacer que la ciudad sea dinámica en el futuro
        WeatherScreen()
    }
}
