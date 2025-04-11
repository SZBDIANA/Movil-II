import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.location.Location
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import net.ivanvega.milocationymapascompose.R
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

data class RouteResponse(
    val features: List<RouteFeature>
)

data class RouteFeature(
    val geometry: RouteGeometry,
    val properties: RouteProperties
)

data class RouteGeometry(
    val coordinates: List<List<Double>>
)

data class RouteProperties(
    val summary: RouteSummary
)

data class RouteSummary(
    val distance: Double,
    val duration: Double
)

interface RouteService {
    @GET("/v2/directions/driving-car")
    suspend fun getRoute(
        @Query("api_key") apiKey: String,
        @Query("start") start: String,
        @Query("end") end: String
    ): RouteResponse
}

val retrofit = Retrofit.Builder()
    .baseUrl("https://api.openrouteservice.org")
    .addConverterFactory(MoshiConverterFactory.create())
    .build()

val routeService = retrofit.create(RouteService::class.java)

// Permisos de ubicación
private val permissions = arrayOf(
    Manifest.permission.ACCESS_FINE_LOCATION,
    Manifest.permission.ACCESS_COARSE_LOCATION
)

// Permisos de ubicación
private fun hasLocationPermissions(activity: ComponentActivity): Boolean {
    return permissions.all {
        ActivityCompat.checkSelfPermission(activity, it) == PackageManager.PERMISSION_GRANTED
    }
}

// Solicitar permisos de ubicación
private fun requestLocationPermissions(activity: ComponentActivity) {
    ActivityCompat.requestPermissions(activity, permissions, LOCATION_PERMISSION_REQUEST_CODE)
}

private const val LOCATION_PERMISSION_REQUEST_CODE = 1
private const val PREF_KEY_ORIGIN = "origin_coordinates"

@Composable
fun MiMapa(activity: ComponentActivity) {
    var origen by remember { mutableStateOf("") }
    var destino by remember { mutableStateOf("") }
    var ruta: RouteResponse? by remember { mutableStateOf(null) }
    var manualDestinationMode by remember { mutableStateOf(false) }
    var manualDestinationCoordinate by remember { mutableStateOf(LatLng(0.0, 0.0)) }
    var markerDestino: LatLng? by remember { mutableStateOf(null) }
    val scope = rememberCoroutineScope()
    val singapore = LatLng(20.126275317533462, -101.18905377998448)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(singapore, 10f)
    }

    val sharedPreferences = activity.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

    LaunchedEffect(Unit) {
        val casaCoordinate = sharedPreferences.getString(PREF_KEY_ORIGIN, null)
        if (casaCoordinate == null) {
            obtenerUbicacionActual(activity)?.let { location ->
                val currentLatLng = LatLng(location.latitude, location.longitude)
                val casaCoords = "${currentLatLng.longitude},${currentLatLng.latitude}"
                sharedPreferences.edit().putString(PREF_KEY_ORIGIN, casaCoords).apply()
                showToast(activity, "Coordenadas de Casa establecidas: $casaCoords")
            }
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            Card(
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Selecciona el destino",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            onClick = {
                                scope.launch {
                                    obtenerUbicacionActual(activity)?.let { location ->
                                        val currentLatLng = LatLng(location.latitude, location.longitude)
                                        destino = "${currentLatLng.longitude},${currentLatLng.latitude}"
                                        markerDestino = currentLatLng
                                    }
                                }
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E88E5))
                        ) {
                            Icon(Icons.Default.LocationOn, contentDescription = null)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Ubicación actual")
                        }

                        Button(
                            onClick = { manualDestinationMode = true },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFA000))
                        ) {
                            Icon(Icons.Default.Search, contentDescription = null)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Elegir en el mapa")
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            scope.launch {
                                val origen = sharedPreferences.getString(PREF_KEY_ORIGIN, null)
                                if (origen != null && destino.isNotEmpty()) {
                                    ruta = obtenerRuta(origen, destino)
                                } else {
                                    showToast(activity, "Completa los campos de origen y destino")
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF43A047))
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.route),
                            contentDescription = "Trazar ruta",
                            tint = Color.White
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Trazar Ruta")
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            GoogleMap(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp)),
                cameraPositionState = cameraPositionState,
                onMapClick = { latLng ->
                    if (manualDestinationMode) {
                        manualDestinationCoordinate = latLng
                        destino = "${latLng.longitude},${latLng.latitude}"
                        markerDestino = latLng
                        manualDestinationMode = false
                    }
                }
            ) {
                sharedPreferences.getString(PREF_KEY_ORIGIN, null)?.let {
                    val casaLatLng = LatLng(it.split(",")[1].toDouble(), it.split(",")[0].toDouble())
                    Marker(
                        state = MarkerState(position = casaLatLng),
                        title = "Origen"
                    )
                }

                markerDestino?.let {
                    Marker(
                        state = MarkerState(position = it),
                        title = "Destino(casa)",
                        icon = bitmapDescriptorFromVector(activity, R.drawable.home, 100,100)
                    )
                }


                ruta?.let { route ->
                    val coordenadasRuta = route.features.first().geometry.coordinates.map { LatLng(it[1], it[0]) }
                    Polyline(points = coordenadasRuta, color = Color.Red)
                }

                if (manualDestinationMode) {
                    Marker(
                        state = MarkerState(position = manualDestinationCoordinate),
                        title = "Destino(casa)"
                    )
                }
            }
        }
    }
}


// Obtener la ubicación actual
@SuppressLint("MissingPermission")
suspend fun obtenerUbicacionActual(activity: ComponentActivity): Location? {
    return withContext(Dispatchers.Main) {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity)
        if (hasLocationPermissions(activity)) {
            fusedLocationClient.lastLocation.await()
        } else {
            requestLocationPermissions(activity)
            null
        }
    }
}

// Función para obtener la ruta utilizando Retrofit
suspend fun obtenerRuta(origen: String, destino: String): RouteResponse {
    return withContext(Dispatchers.IO) {
        routeService.getRoute(
            apiKey = "5b3ce3597851110001cf6248de3c8109d6c44bacb398824a41ccf1df",
            start = origen,
            end = destino
        )
    }
}

fun showToast(context: Context, message: String) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}
fun bitmapDescriptorFromVector(context: Context, vectorResId: Int, width: Int, height: Int): BitmapDescriptor {
    val vectorDrawable = ContextCompat.getDrawable(context, vectorResId)!!
    vectorDrawable.setBounds(0, 0, width, height) // Ajusta el tamaño aquí
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    vectorDrawable.draw(canvas)
    return BitmapDescriptorFactory.fromBitmap(bitmap)
}


