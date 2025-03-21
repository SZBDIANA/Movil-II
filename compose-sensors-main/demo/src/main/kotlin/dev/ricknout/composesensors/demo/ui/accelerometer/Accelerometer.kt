package dev.ricknout.composesensors.demo.ui.accelerometer

import android.content.res.Configuration
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.ricknout.composesensors.accelerometer.isAccelerometerSensorAvailable
import dev.ricknout.composesensors.accelerometer.rememberAccelerometerSensorValueAsState
import dev.ricknout.composesensors.demo.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SoccerField() {
    if (isAccelerometerSensorAvailable()) {
        val sensorValue by rememberAccelerometerSensorValueAsState()
        val (x, y, z) = sensorValue.value

        val leftLimit = 60.dp // Derecha
        val rightLimit = 1020.dp
        val topLimit = 60.dp // Arriba
        val bottomLimit = 1920.dp

        // Izquierda + / Derecha -
        // Abajo + / Arriba -
        // Ancho + / Delgado -
        // Largo + / Corto -

        val obstacles = listOf(
            // PORTERIA ARRIBA
            RectObstacle(153.dp, 18.dp, 5.dp, 30.dp),
            RectObstacle(253.dp, 18.dp, 5.dp, 30.dp),
            // VERTICALES PARTE ARRIBA
            RectObstacle(40.dp, 145.dp, 10.dp, 50.dp),
            RectObstacle(360.dp, 145.dp, 10.dp, 50.dp),
            // 2 PRIMEROS
            RectObstacle(40.dp, 220.dp, 10.dp, 50.dp),
            RectObstacle(70.dp, 200.dp, 10.dp, 50.dp),
            // 2 ULTIMOS
            RectObstacle(360.dp, 220.dp, 10.dp, 50.dp),
            RectObstacle(320.dp, 200.dp, 10.dp, 50.dp),
            // 1 VERTICAL 5 FILA
            RectObstacle(40.dp, 326.dp, 10.dp, 50.dp),
            // 2 VERTICAL 5 FILA
            RectObstacle(360.dp, 326.dp, 10.dp, 50.dp),
            // FILA 0
            RectObstacle(40.dp, 90.dp, 40.dp, 10.dp),
            RectObstacle(110.dp, 90.dp, 10.dp, 10.dp),
            RectObstacle(110.dp, 115.dp, 10.dp, 10.dp),
            RectObstacle(295.dp, 90.dp, 10.dp, 10.dp),
            RectObstacle(330.dp, 90.dp, 40.dp, 10.dp),
            RectObstacle(295.dp, 115.dp, 10.dp, 10.dp),

            // HORIZONTALES PARTE ARRIBA 1 FILA
            RectObstacle(50.dp, 145.dp, 10.dp, 10.dp),
            RectObstacle(85.dp, 145.dp, 10.dp, 10.dp),
            RectObstacle(110.dp, 145.dp, 10.dp, 10.dp),
            RectObstacle(145.dp, 145.dp, 10.dp, 10.dp),
            RectObstacle(180.dp, 145.dp, 10.dp, 10.dp),
            RectObstacle(215.dp, 145.dp, 10.dp, 10.dp),
            RectObstacle(250.dp, 145.dp, 10.dp, 10.dp),
            RectObstacle(295.dp, 145.dp, 10.dp, 10.dp),
            RectObstacle(330.dp, 145.dp, 10.dp, 10.dp),
            // HORIZONTALES PARTE ARRIBA 2 FILA
            RectObstacle(70.dp, 200.dp, 40.dp, 10.dp),
            RectObstacle(140.dp, 200.dp, 40.dp, 10.dp),
            RectObstacle(210.dp, 200.dp, 40.dp, 10.dp),
            RectObstacle(280.dp, 200.dp, 40.dp, 10.dp),
            // HORIZONTALES 3 FILA
            RectObstacle(180.dp, 250.dp, 40.dp, 10.dp),
            RectObstacle(250.dp, 250.dp, 40.dp, 10.dp),
            RectObstacle(100.dp, 250.dp, 40.dp, 10.dp),
            // HORIZONTALES 4 FILA
            RectObstacle(50.dp, 295.dp, 20.dp, 10.dp),
            RectObstacle(120.dp, 295.dp, 15.dp, 10.dp),
            RectObstacle(190.dp, 295.dp, 20.dp, 10.dp),
            RectObstacle(260.dp, 295.dp, 15.dp, 10.dp),
            RectObstacle(330.dp, 295.dp, 15.dp, 10.dp),
            // HORIZONTALES 5 FILA
            RectObstacle(90.dp, 345.dp, 50.dp, 10.dp),
            RectObstacle(260.dp, 345.dp, 50.dp, 10.dp),
            RectObstacle(330.dp, 345.dp, 10.dp, 10.dp),
            RectObstacle(60.dp, 345.dp, 10.dp, 10.dp),

            // PORTERIA ABAJO
            RectObstacle(153.dp, 711.dp, 5.dp, 30.dp),
            RectObstacle(253.dp, 711.dp, 5.dp, 30.dp),
            // VERTICALES PARTE ARRIBA
            RectObstacle(40.dp, 570.dp, 10.dp, 50.dp),
            RectObstacle(360.dp, 570.dp, 10.dp, 50.dp),
            // 2 PRIMEROS
            RectObstacle(40.dp, 495.dp, 10.dp, 50.dp),
            RectObstacle(70.dp, 515.dp, 10.dp, 50.dp),
            // 2 ULTIMOS
            RectObstacle(360.dp, 495.dp, 10.dp, 50.dp),
            RectObstacle(320.dp, 520.dp, 10.dp, 50.dp),
            // 1 VERTICAL 5 FILA
            RectObstacle(40.dp, 385.dp, 10.dp, 50.dp),
            // 2 VERTICAL 5 FILA
            RectObstacle(360.dp, 385.dp, 10.dp, 50.dp),
            // FILA 0
            RectObstacle(40.dp, 665.dp, 40.dp, 10.dp),
            RectObstacle(110.dp, 665.dp, 10.dp, 10.dp),
            RectObstacle(110.dp, 635.dp, 10.dp, 10.dp),
            RectObstacle(295.dp, 665.dp, 10.dp, 10.dp),
            RectObstacle(330.dp, 665.dp, 40.dp, 10.dp),
            RectObstacle(295.dp, 635.dp, 10.dp, 10.dp),
            // HORIZONTALES PARTE ARRIBA 1 FILA
            RectObstacle(50.dp, 610.dp, 10.dp, 10.dp),
            RectObstacle(85.dp, 610.dp, 10.dp, 10.dp),
            RectObstacle(110.dp, 610.dp, 10.dp, 10.dp),
            RectObstacle(145.dp, 610.dp, 10.dp, 10.dp),
            RectObstacle(180.dp, 610.dp, 10.dp, 10.dp),
            RectObstacle(215.dp, 610.dp, 10.dp, 10.dp),
            RectObstacle(250.dp, 610.dp, 10.dp, 10.dp),
            RectObstacle(295.dp, 610.dp, 10.dp, 10.dp),
            RectObstacle(330.dp, 610.dp, 10.dp, 10.dp),
            // HORIZONTALES PARTE ARRIBA 2 FILA
            RectObstacle(70.dp, 560.dp, 40.dp, 10.dp),
            RectObstacle(140.dp, 560.dp, 40.dp, 10.dp),
            RectObstacle(210.dp, 560.dp, 40.dp, 10.dp),
            RectObstacle(280.dp, 560.dp, 40.dp, 10.dp),
            // HORIZONTALES 3 FILA
            RectObstacle(180.dp, 510.dp, 40.dp, 10.dp),
            RectObstacle(250.dp, 510.dp, 40.dp, 10.dp),
            RectObstacle(100.dp, 510.dp, 40.dp, 10.dp),
            // HORIZONTALES 4 FILA
            RectObstacle(50.dp, 460.dp, 20.dp, 10.dp),
            RectObstacle(120.dp, 460.dp, 15.dp, 10.dp),
            RectObstacle(190.dp, 460.dp, 20.dp, 10.dp),
            RectObstacle(260.dp, 460.dp, 15.dp, 10.dp),
            RectObstacle(330.dp, 460.dp, 15.dp, 10.dp),
            // HORIZONTALES 5 FILA
            RectObstacle(90.dp, 410.dp, 50.dp, 10.dp),
            RectObstacle(260.dp, 410.dp, 50.dp, 10.dp),
            RectObstacle(330.dp, 410.dp, 10.dp, 10.dp),
            RectObstacle(60.dp, 410.dp, 10.dp, 10.dp),
        )

        val trampolines = listOf(
            Trampoline(23.dp, 23.dp),
            Trampoline(377.dp, 23.dp),
            Trampoline(23.dp, 726.dp),
            Trampoline(377.dp, 726.dp)
        )

        var ArribaGols by remember { mutableStateOf(0) }
        var AbajoGols by remember { mutableStateOf(0) }
        var center by remember { mutableStateOf(Offset(rightLimit.value / 2, bottomLimit.value / 2)) }

        val topGoalArea = with(LocalDensity.current) {
            androidx.compose.ui.geometry.Rect(
                left = 145.dp.toPx(),
                top = 0.dp.toPx(),
                right = 245.dp.toPx(),
                bottom = 60.dp.toPx()
            )
        }

        val bottomGoalArea = with(LocalDensity.current) {
            androidx.compose.ui.geometry.Rect(
                left = 145.dp.toPx(),
                top = 665.dp.toPx(),
                right = 245.dp.toPx(),
                bottom = 725.dp.toPx()
            )
        }

        val orientation = LocalConfiguration.current.orientation
        val contentColor = LocalContentColor.current
        val radius = with(LocalDensity.current) { 10.dp.toPx() }

        val speedFactor = 2.5f // Puedes ajustar el valor a lo que quieras, más alto = más rápido

        center = if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            Offset(
                x = (center.x - x * speedFactor).coerceIn(leftLimit.value + radius, rightLimit.value - radius),
                y = (center.y + y * speedFactor).coerceIn(topLimit.value + radius, bottomLimit.value - radius),
            )
        } else {
            Offset(
                x = (center.x + y * speedFactor).coerceIn(leftLimit.value + radius, rightLimit.value - radius),
                y = (center.y + x * speedFactor).coerceIn(topLimit.value + radius, bottomLimit.value - radius),
            )
        }


        // Verificar la colisión entre los trampolines
        obstacles.forEach { obstacle ->
            center = checkCollision(center, obstacle, radius)
        }

        trampolines.forEach { trampoline ->
            center = checkTrampolineCollision(center, trampoline, radius)
        }

        // Verificar los goles
        if (center.x in topGoalArea.left..topGoalArea.right && center.y in topGoalArea.top..topGoalArea.bottom) {
            ArribaGols++
            center = Offset(rightLimit.value / 2, bottomLimit.value / 2)
        }

        if (center.x in bottomGoalArea.left..bottomGoalArea.right && center.y in bottomGoalArea.top..bottomGoalArea.bottom) {
            AbajoGols++
            center = Offset(rightLimit.value / 2, bottomLimit.value / 2)
        }

        Scaffold(
            topBar = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 28.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    ScoreCard(title = "Jugador 1", score = ArribaGols, color = Color(0xFF2196F3)) // Azul
                    ScoreCard(title = "Jugador 2", score = AbajoGols, color = Color(0xFFF44336)) // Rojo
                }
            }
        ) { paddingValues ->
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Green)
                ) {
                    Box(
                        modifier = Modifier
                            .size(rightLimit, bottomLimit)
                            .padding(20.dp)
                            .background(Color.Transparent)
                            .border(4.dp, Color.White)
                            .align(Alignment.Center)
                    ) {
                        Divider(
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.Center)
                                .height(4.dp),
                            color = Color.White
                        )

                        Canvas(modifier = Modifier.fillMaxSize()) {
                            drawCircle(
                                color = Color.White,
                                radius = 50.dp.toPx(),
                                center = Offset(size.width / 2, size.height / 2),
                                style = Stroke(width = 4.dp.toPx())
                            )
                        }

                        val penaltyAreaWidth = 200.dp
                        val penaltyAreaHeight = 100.dp
                        val goalWidth = 100.dp
                        val goalHeight = 60.dp

                        Box(
                            modifier = Modifier
                                .size(penaltyAreaWidth, penaltyAreaHeight)
                                .align(Alignment.TopCenter)
                                .offset(y = 20.dp)
                                .background(Color.Transparent)
                                .border(4.dp, Color.White)
                        )

                        Box(
                            modifier = Modifier
                                .size(penaltyAreaWidth, penaltyAreaHeight)
                                .align(Alignment.BottomCenter)
                                .offset(y = (-20).dp)
                                .background(Color.Transparent)
                                .border(4.dp, Color.White)
                        )

                        Box(
                            modifier = Modifier
                                .size(goalWidth, goalHeight)
                                .align(Alignment.TopCenter)
                                .offset(y = (-goalHeight / 2))
                                .background(Color.Gray)
                                .border(2.dp, Color.White)
                        )

                        Box(
                            modifier = Modifier
                                .size(goalWidth, goalHeight)
                                .align(Alignment.BottomCenter)
                                .offset(y = (goalHeight / 2))
                                .background(Color.Gray)
                                .border(2.dp, Color.White)
                        )
                    }


                    obstacles.forEach { obstacle ->
                        Box(
                            modifier = Modifier
                                .offset(obstacle.left, obstacle.top)
                                .size(obstacle.width, obstacle.height)
                                .background(Color.Gray)
                        )
                    }

                    trampolines.forEach { trampoline ->
                        Box(
                            modifier = Modifier
                                .offset(trampoline.left, trampoline.top)
                                .size(10.dp, 10.dp)
                                .background(Color.White)
                        )
                    }

                    Image(
                        painter = painterResource(id = R.drawable.balon), // o Coil
                        contentDescription = "Balón",
                        modifier = Modifier
                            .size(radius .dp)
                            .offset {
                                IntOffset(
                                    (center.x - radius).toInt(),
                                    (center.y - radius).toInt()
                                )
                            }
                    )
                }
                }
            }
        }
    }


data class RectObstacle(val left: Dp, val top: Dp, val width: Dp, val height: Dp)
data class Trampoline(val left: Dp, val top: Dp)

@Composable
fun checkCollision(center: Offset, obstacle: RectObstacle, radius: Float): Offset {
    val obstacleRect = with(LocalDensity.current) {
        androidx.compose.ui.geometry.Rect(
            left = obstacle.left.toPx(),
            top = obstacle.top.toPx(),
            right = obstacle.left.toPx() + obstacle.width.toPx(),
            bottom = obstacle.top.toPx() + obstacle.height.toPx()
        )
    }

    val closestX = center.x.coerceIn(obstacleRect.left, obstacleRect.right)
    val closestY = center.y.coerceIn(obstacleRect.top, obstacleRect.bottom)
    val distanceX = center.x - closestX
    val distanceY = center.y - closestY

    if ((distanceX * distanceX + distanceY * distanceY) < (radius * radius)) {
        val overlapX = radius - kotlin.math.abs(distanceX)
        val overlapY = radius - kotlin.math.abs(distanceY)
        return if (overlapX < overlapY) {
            Offset(
                x = if (distanceX < 0) center.x - overlapX else center.x + overlapX,
                y = center.y
            )
        } else {
            Offset(
                x = center.x,
                y = if (distanceY < 0) center.y - overlapY else center.y + overlapY
            )
        }
    }
    return center
}

@Composable
fun checkTrampolineCollision(center: Offset, trampoline: Trampoline, radius: Float): Offset {
    val trampolineRect = with(LocalDensity.current) {
        androidx.compose.ui.geometry.Rect(
            left = trampoline.left.toPx(),
            top = trampoline.top.toPx(),
            right = trampoline.left.toPx() + 40.dp.toPx(),
            bottom = trampoline.top.toPx() + 40.dp.toPx()
        )
    }

    val closestX = center.x.coerceIn(trampolineRect.left, trampolineRect.right)
    val closestY = center.y.coerceIn(trampolineRect.top, trampolineRect.bottom)
    val distanceX = center.x - closestX
    val distanceY = center.y - closestY

    if ((distanceX * distanceX + distanceY * distanceY) < (radius * radius)) {
        return Offset(
            x = center.x + (radius * 4) * kotlin.math.sign(distanceX),
            y = center.y + (radius * 30) * kotlin.math.sign(distanceY)
        )
    }
    return center
}
@Composable
fun ScoreCard(title: String, score: Int, color: Color) {
    Card(
        modifier = Modifier
            .size(width = 120.dp, height = 50.dp),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = title,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Box(
                modifier = Modifier
                    .size(30.dp)
                    .background(color, shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "$score",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}







