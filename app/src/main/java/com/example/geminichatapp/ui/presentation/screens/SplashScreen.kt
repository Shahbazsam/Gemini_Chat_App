package com.example.geminichatapp.ui.presentation.screens

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.Dp
import com.example.geminichatapp.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SplashScreen() {
    val xOffset = remember { Animatable(160f) }
    val yOffset = remember { Animatable(850f) }
    val size = remember { Animatable(75.dp, Dp.VectorConverter) }
    var currentImage by remember { mutableIntStateOf(R.drawable.rocket) }
    var flag by remember { mutableStateOf(true) }

    val gradientOffset = remember { Animatable(0f) }
    val particles = remember { mutableStateListOf<Pair<Float, Float>>() }

    // Gradiant animations
    LaunchedEffect(Unit) {
        launch {
            while (flag) {
                gradientOffset.animateTo(
                    targetValue = -24f,
                    animationSpec = tween(durationMillis = 3000, easing = LinearEasing)
                )
//                gradientOffset.snapTo(0f) // Reset for a looping effect
            }
        }

        // Create particles randomly
        launch {
            while (flag) {
                particles.add(Pair((0..1000).random().toFloat(), (0..1000).random().toFloat()))
                delay(50) // Add particles every 50ms
                if (particles.size > 100) particles.removeAt(0) // Limit particle count
            }
        }

        // Rocket animation sequence
        delay(500)
        yOffset.animateTo(
            targetValue = 50f,
            animationSpec = tween(durationMillis = 1500)
        )
        yOffset.animateTo(
            targetValue = 355f,
            animationSpec = tween(durationMillis = 600)
        )
        launch{
            xOffset.animateTo(
                targetValue = 140f,
                animationSpec = tween(durationMillis = 600)
            )
            size.animateTo(
                targetValue = 150.dp,
                animationSpec = tween(durationMillis = 500)
            )
        }

        currentImage = R.drawable.assistant
        // Next screen
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            // Draw animated gradient
            val gradient = Brush.linearGradient(
                colors = listOf(
                    Color.Black,
                    Color.Gray,
                    Color(0xFF023261)
                ),
                start = Offset(160f * gradientOffset.value, 0f),
                end = Offset(0f, 775f * (1 - gradientOffset.value))
            )
            drawRect(brush = gradient, size = this.size)

            // Draw particles
            particles.forEach { (x, y) ->
                drawCircle(
                    color = Color.White,
                    radius = 5f,
                    center = Offset(x, y * 2),
                    alpha = 0.8f
                )
            }
        }

        Crossfade(
            targetState = currentImage,
            label = "Assistant",
            animationSpec = tween(durationMillis = 250)
        ) { image ->
            Image(
                colorFilter = ColorFilter.tint(Color.White),
                painter = painterResource(id = image),
                contentDescription = if (image == R.drawable.rocket) "Rocket" else "Assistant",
                modifier = Modifier
                    .size(size.value)
                    .offset(xOffset.value.dp, yOffset.value.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun Preview() {
    SplashScreen()
}