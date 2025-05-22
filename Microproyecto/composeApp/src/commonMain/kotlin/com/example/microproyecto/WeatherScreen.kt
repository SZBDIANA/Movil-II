package com.example.microproyecto

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource


import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text


@Composable
fun TitleWithIcon() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(bottom = 24.dp)
    ) {
        Icon(
            imageVector = Icons.Filled.Cloud,
            contentDescription = "Nube",
            tint = Color(0xFF6A4C93),
            modifier = Modifier.size(32.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            "Consulta el Clima",
            style = MaterialTheme.typography.headlineLarge.copy(
                fontWeight = FontWeight.Bold,
                color = Color(0xFF6A4C93)
            )
        )
    }
}

@Composable
fun WeatherScreen() {
    var cityInput by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var weather by remember { mutableStateOf<WeatherResponse?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(city) {
        if (city.isNotBlank()) {
            isLoading = true
            errorMessage = null
            try {
                val data = WeatherApi.getWeather(city)
                weather = data
            } catch (e: Exception) {
                errorMessage = e.message ?: "Error desconocido"
                weather = null
            }
            isLoading = false
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFFFFE0F0), Color(0xFFB3D1FF))
                )
            )
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            TitleWithIcon()

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.White.copy(alpha = 0.3f))
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextField(
                    value = cityInput,
                    onValueChange = { cityInput = it },
                    placeholder = { Text("Ingresa una ciudad") },
                    singleLine = true,
                    modifier = Modifier.weight(1f),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent,
                        cursorColor = Color(0xFF5E60CE),
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedLabelColor = Color(0xFF5E60CE),
                        unfocusedLabelColor = Color.Gray,
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black
                    )
                )
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = { if (cityInput.isNotBlank()) city = cityInput.trim() },
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFBE95C4)
                    )
                ) {
                    Text("Buscar", color = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            when {
                isLoading -> CircularProgressIndicator(color = Color(0xFF6A4C93))
                errorMessage != null -> Text(
                    "Error: $errorMessage",
                    color = Color.Red,
                    style = MaterialTheme.typography.bodyMedium
                )
                weather != null -> WeatherCard(weather!!)
            }
        }
    }
}

@Composable
fun WeatherCard(weather: WeatherResponse) {
    val iconUrl = "https://openweathermap.org/img/wn/${weather.weather.first().icon}@4x.png"
    val resource = asyncPainterResource(iconUrl)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clip(RoundedCornerShape(32.dp))
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFFFE0F0),
                        Color(0xFFE0C3FC),
                        Color(0xFFC1D3FE)
                    )
                )
            )
            .border(2.dp, Color.White.copy(alpha = 0.6f), RoundedCornerShape(32.dp))
            .padding(24.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = weather.name,
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF5C2A9D)
                )
            )

            Text(
                text = weather.weather.first().description.replaceFirstChar { it.uppercase() },
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = Color(0xFF6D4E9C)
                )
            )

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                KamelImage(
                    resource = asyncPainterResource(iconUrl),
                    contentDescription = "Icono del clima",
                    modifier = Modifier.size(90.dp)




                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "${weather.main.temp.toInt()}°C",
                    style = MaterialTheme.typography.displaySmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF44318D)
                    )
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "🔻 ${weather.main.temp_min}°C     🔺 ${weather.main.temp_max}°C",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = Color(0xFF4C3A6B),
                    fontWeight = FontWeight.Medium
                )
            )

            Spacer(modifier = Modifier.height(24.dp))
            Divider(color = Color.White.copy(alpha = 0.3f), thickness = 1.dp)
            Spacer(modifier = Modifier.height(16.dp))

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                InfoRow(Icons.Default.Thermostat, "Sensación", "${weather.main.feels_like} °C")
                InfoRow(Icons.Default.WaterDrop, "Humedad", "${weather.main.humidity}%")
                InfoRow(Icons.Default.Speed, "Presión", "${weather.main.pressure} hPa")
                InfoRow(Icons.Default.Air, "Viento", "${weather.wind.speed} m/s, dir. ${weather.wind.deg}°")
                InfoRow(Icons.Default.Cloud, "Nubosidad", "${weather.clouds.all}%")
                InfoRow(Icons.Default.LocationOn, "Coordenadas", "${weather.coord.lat}, ${weather.coord.lon}")
                weather.rain?.`1h`?.let { InfoRow(Icons.Default.WaterDrop, "Lluvia (1h)", "$it mm") }
                weather.snow?.`1h`?.let { InfoRow(Icons.Default.Cloud, "Nieve (1h)", "$it mm") }
            }
        }
    }
}

@Composable
fun InfoRow(icon: ImageVector, label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = label, tint = Color(0xFF4A4E69), modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = label, color = Color(0xFF4A4E69))
        }
        Text(text = value, color = Color(0xFF4A4E69), fontWeight = FontWeight.Medium)
    }
}

