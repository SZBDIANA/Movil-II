package com.example.respuestaautomatica_broadcastr

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.respuestaautomatica_broadcastr.ui.theme.RespuestaAutomatica_BroadcastRTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Solicitar permisos antes de iniciar la app
        requestPermissions()

        setContent {
            RespuestaAutomatica_BroadcastRTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MainScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }

    private fun requestPermissions() {
        val permissions = arrayOf(Manifest.permission.READ_PHONE_STATE, Manifest.permission.SEND_SMS)
        val permissionsNotGranted = permissions.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }

        if (permissionsNotGranted.isNotEmpty()) {
            requestPermissions(permissionsNotGranted.toTypedArray(), 1)
        }
    }
}

@Composable
fun MainScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("AutoReplyPrefs", Context.MODE_PRIVATE)

    var phoneNumber by rememberSaveable { mutableStateOf(sharedPreferences.getString("phoneNumber", "") ?: "") }
    var message by rememberSaveable { mutableStateOf(sharedPreferences.getString("message", "") ?: "") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(text = "Número de teléfono:")

        OutlinedTextField(
            value = phoneNumber,
            onValueChange = { phoneNumber = it },
            label = { Text("Ingresa el número") },
            modifier = Modifier.fillMaxWidth()
        )

        Text(text = "Mensaje de respuesta automática:")

        OutlinedTextField(
            value = message,
            onValueChange = { message = it },
            label = { Text("Ingresa el mensaje") },
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {
                sharedPreferences.edit().apply {
                    putString("phoneNumber", phoneNumber)
                    putString("message", message)
                    apply()
                }
                Toast.makeText(context, "Datos guardados correctamente", Toast.LENGTH_SHORT).show()
                phoneNumber = ""
                message = ""
                // Imprimir en el Log para verificar que el número y mensaje fueron guardados
                Log.d("MainActivity", "Número guardado: $phoneNumber")
                Log.d("MainActivity", "Mensaje guardado: $message")
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Guardar")
        }

    }
}

@Preview(showBackground = true)
@Composable
fun PreviewMainScreen() {
    RespuestaAutomatica_BroadcastRTheme {
        MainScreen()
    }
}