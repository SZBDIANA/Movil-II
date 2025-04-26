package com.example.wifi_scanner

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.compose.foundation.clickable
import android.net.wifi.ScanResult
import android.net.wifi.WifiManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SignalWifi4Bar
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat


class MainActivity : ComponentActivity() {

    private lateinit var wifiManager: WifiManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        wifiManager = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager

        setContent {
            WifiScannerScreen(wifiManager)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WifiScannerScreen(wifiManager: WifiManager) {
    var wifiList by remember { mutableStateOf<List<ScanResult>>(emptyList()) }
    var permissionGranted by remember { mutableStateOf(false) }

    val context = LocalContext.current // <-- lo movemos aquí, fuera del LaunchedEffect

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        permissionGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true &&
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (permissionGranted) {
            wifiList = wifiManager.scanResults
        }
    }

    LaunchedEffect(Unit) {
        permissionGranted = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(
                    context, Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED

        if (!permissionGranted) {
            launcher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        } else {
            wifiList = wifiManager.scanResults
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("WiFi Scanner") },
                actions = {
                    IconButton(onClick = {
                        // Refrescar escaneo
                        wifiList = wifiManager.scanResults
                    }) {
                        Icon(Icons.Filled.Refresh, contentDescription = "Refresh WiFi List")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            if (permissionGranted) {
                if (wifiList.isNotEmpty()) {
                    LazyColumn {
                        items(wifiList) { scanResult ->
                            WifiCard(scanResult)
                        }
                    }
                } else {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No hay redes disponibles.")
                    }
                }
            } else {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Permisos no otorgados. No se pueden escanear redes WiFi.")
                }
            }
        }
    }
}

@Composable
fun WifiCard(scanResult: ScanResult) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { expanded = !expanded }, // <-- Ahora responde al click
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = getWifiIcon(),
                    contentDescription = "WiFi Signal",
                    tint = getWifiColor(scanResult.level),
                    modifier = Modifier.size(40.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(text = scanResult.SSID.ifEmpty { "(Red oculta)" }, style = MaterialTheme.typography.titleMedium)
                    Text(text = "BSSID: ${scanResult.BSSID}", style = MaterialTheme.typography.bodySmall)
                }
            }
            if (expanded) {
                Spacer(modifier = Modifier.height(12.dp))
                Divider()
                Spacer(modifier = Modifier.height(12.dp))
                Column {
                    Text("Nivel de señal: ${scanResult.level} dBm", style = MaterialTheme.typography.bodyMedium)
                    Text("Frecuencia: ${scanResult.frequency} MHz", style = MaterialTheme.typography.bodyMedium)
                    Text("Seguridad: ${scanResult.capabilities}", style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}


fun getWifiIcon(): ImageVector {
    return Icons.Filled.SignalWifi4Bar
}

fun getWifiColor(level: Int): Color {
    return when {
        level >= -50 -> Color(0xFF4CAF50)// Excelente
        level >= -60 -> Color(0xFF8BC34A) // Bueno
        level >= -70 -> Color(0xFFFFEB3B) // Regular
        level >= -80 -> Color(0xFFFF9800) // Pobre
        else -> Color(0xFFF44336)         // Muy débil
    }
}