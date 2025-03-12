package com.example.exchange_rate_client

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.exchange_rate_client.ui.theme.ExchangerateclientTheme
import android.graphics.Color
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ExchangerateclientTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    ExchangeRateClientApp()
                }
            }
        }

    }
}




@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExchangeRateClientApp() {
    val context = LocalContext.current
    var startDate by remember { mutableStateOf(1738368000000L) } // Cambiado a Long
    var endDate by remember { mutableStateOf(1743465600000L) } // Cambiado a Long
    var moneda by remember { mutableStateOf("") }
    var result by remember { mutableStateOf<List<Pair<Long, Double>>>(emptyList()) }

    // Función para formatear el timestamp a una fecha legible
    fun formatDate(timestamp: Long): String {
        val date = Date(timestamp)
        val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return formatter.format(date)
    }

    // Función para mostrar el DatePickerDialog
    fun showDatePickerDialog(initialDate: Long, onDateSelected: (Long) -> Unit) {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = initialDate
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            context,
            { _, selectedYear, selectedMonth, selectedDay ->
                val selectedCalendar = Calendar.getInstance()
                selectedCalendar.set(selectedYear, selectedMonth, selectedDay)
                onDateSelected(selectedCalendar.timeInMillis)
            },
            year,
            month,
            day
        )
        datePickerDialog.show()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Cliente de Tasas de Cambio") }
            )
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .padding(16.dp)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = moneda,
                    onValueChange = { moneda = it },
                    label = { Text("Tipo de Moneda (example USD)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    modifier = Modifier.fillMaxWidth()
                )

                // Botón para seleccionar la fecha de inicio
                Button(
                    onClick = {
                        showDatePickerDialog(startDate) { selectedDate ->
                            startDate = selectedDate
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Seleccionar fecha de inicio")
                }

                // TextField para mostrar la fecha de inicio seleccionada
                OutlinedTextField(
                    value = formatDate(startDate),
                    onValueChange = { }, // No permitir edición manual
                    label = { Text("Fecha de inicio seleccionada") },
                    modifier = Modifier.fillMaxWidth(),
                    readOnly = true // Hacer el campo de solo lectura
                )

                // Botón para seleccionar la fecha de fin
                Button(
                    onClick = {
                        showDatePickerDialog(endDate) { selectedDate ->
                            endDate = selectedDate
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Seleccionar fecha de fin")
                }

                // TextField para mostrar la fecha de fin seleccionada
                OutlinedTextField(
                    value = formatDate(endDate),
                    onValueChange = { }, // No permitir edición manual
                    label = { Text("Fecha de fin seleccionada") },
                    modifier = Modifier.fillMaxWidth(),
                    readOnly = true // Hacer el campo de solo lectura
                )

                // Botón para ejecutar la consulta
                Button(
                    onClick = {
                        result = queryExchangeRates(
                            context,
                            startDate,
                            endDate,
                            moneda.toString()
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Obtener tasas de cambio")
                }

                // Mostrar gráfico solo si hay datos
                if (result.isNotEmpty()) {
                    ExchangeRateChart(context, result, startDate, endDate)
                    for (res in result) {
                        Log.d("res", res.toString())
                    }
                } else {
                    Text("No se encontraron datos")
                }
            }
        }
    )
}

// Función para ejecutar la consulta y devolver una lista de pares (fecha, tasa)
@SuppressLint("Range")
private fun queryExchangeRates(
    context: Context,
    startDate: Long,
    endDate: Long,
    moneda: String
): List<Pair<Long, Double>> {
    val uri = Uri.parse("content://com.example.exchangeRate.providers/exchange_rates_by_date_range")
    val cursor = context.contentResolver.query(
        uri,
        null,
        null,
        arrayOf(startDate.toString(), endDate.toString()),
        null
    )

    val exchangeData = mutableListOf<Pair<Long, Double>>()

    cursor?.use {
        while (it.moveToNext()) {
            val rates = it.getString(it.getColumnIndex("rates"))
            val lastUpdateUnix = it.getLong(it.getColumnIndex("last_update_unix"))
            val nextUpdateUnix = it.getLong(it.getColumnIndex("next_update_unix"))

            if (rates.isNotBlank()) {
                val tasas = rates.split(", ") // Separar cada moneda
                for (tasa in tasas) {
                    val partes = tasa.split("=") // Separar moneda y valor
                    if (partes.size == 2) {
                        val nombreMoneda = partes[0].trim()
                        val valorCambio = partes[1].toDoubleOrNull() // Convertir a número

                        if (nombreMoneda == moneda && valorCambio != null) {
                            exchangeData.add(Pair(lastUpdateUnix, valorCambio))
                        }
                    }
                }
            }
        }
    }

    return exchangeData
}

@Composable
fun ExchangeRateChart(context: Context, exchangeData: List<Pair<Long, Double>>, startDate: Long, endDate: Long) {
    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { ctx ->
            LineChart(ctx).apply {
                xAxis.position = XAxis.XAxisPosition.BOTTOM
                axisRight.isEnabled = false
                description.isEnabled = false
                xAxis.valueFormatter = object : ValueFormatter() {
                    override fun getFormattedValue(value: Float): String {
                        // Convertir el valor de 'value' (que representa los días desde startDate) a fecha
                        val date = Date(startDate + (value.toLong() * 1000 * 60 * 60 * 24)) // Convertir a milisegundos
                        val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) // Ajusta el formato
                        return formatter.format(date)
                    }
                }
            }
        },
        update = { chart ->
            // Generar las entradas para cada par (timestamp, rate) en exchangeData
            val entries = exchangeData.map { (timestamp, rate) ->
                val time =timestamp * 1000L
                // Calcular la diferencia en días entre la fecha de exchangeData y startDate
                val daysSinceStart = (time - startDate) / (1000 * 60 * 60 * 24)
                Log.d("daysSinceStart", daysSinceStart.toString())
                Entry(daysSinceStart.toFloat(), rate.toFloat()) // Establecer el punto en el eje X correspondiente
            }

            val dataSet = LineDataSet(entries, "Exchange Rates").apply {
                color = Color.BLUE
                valueTextColor = Color.WHITE
                setDrawValues(false) // No mostrar los valores sobre los puntos
            }

            chart.data = LineData(dataSet)

            // Ajustar los valores del eje X para que cubran el rango de días entre startDate y endDate
            val totalDays = (endDate - startDate) / (1000 * 60 * 60 * 24).toFloat()
            chart.xAxis.axisMinimum = 0f
            chart.xAxis.axisMaximum = totalDays

            chart.invalidate() // Actualizar el gráfico
        }
    )
}



