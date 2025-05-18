package com.example.appnotificaciones

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.google.firebase.messaging.FirebaseMessaging
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                FCMTokenScreen()
            }
        }
    }
}
@Composable
fun FCMTokenScreen() {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    var token by remember { mutableStateOf<String?>(null) }
    var error by remember { mutableStateOf<String?>(null) }
    var timestamp by remember { mutableStateOf<String?>(null) }

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            Log.d("FCM", "Permiso concedido")
            getToken(
                onTokenReceived = {
                    token = it
                    timestamp = getCurrentTimestamp()
                },
                onError = {
                    error = it
                }
            )
        } else {
            error = "Permiso denegado"
        }
    }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            )) {
                PackageManager.PERMISSION_GRANTED -> {
                    getToken(
                        onTokenReceived = {
                            token = it
                            timestamp = getCurrentTimestamp()
                        },
                        onError = {
                            error = it
                        }
                    )
                }

                else -> {
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        } else {
            getToken(
                onTokenReceived = {
                    token = it
                    timestamp = getCurrentTimestamp()
                },
                onError = {
                    error = it
                }
            )
        }
    }

    // UI
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF4CAF50), Color(0xFF81C784))
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Card(
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(10.dp),
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Token FCM",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = Color(0xFF2E7D32)
                )

                Spacer(modifier = Modifier.height(16.dp))

                token?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Fecha: $timestamp",
                        style = MaterialTheme.typography.bodySmall
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            clipboardManager.setText(AnnotatedString(it))
                            Toast.makeText(context, "Token copiado al portapapeles", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF388E3C),
                            contentColor = Color.White
                        )
                    ) {
                        Text("Copiar token")
                    }
                }

                error?.let {
                    Text(
                        text = "Error: $it",
                        color = Color.Red,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        }
    }
}
fun getToken(onTokenReceived: (String) -> Unit, onError: (String) -> Unit) {
    FirebaseMessaging.getInstance().token
        .addOnSuccessListener { token -> onTokenReceived(token) }
        .addOnFailureListener { exception -> onError(exception.message ?: "Error desconocido") }
}

fun getCurrentTimestamp(): String {
    val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
    return dateFormat.format(Date())
}
