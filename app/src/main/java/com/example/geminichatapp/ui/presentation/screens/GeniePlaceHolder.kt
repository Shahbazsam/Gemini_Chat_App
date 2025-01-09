package com.example.geminichatapp.ui.presentation.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp


@Composable
fun GeniePlaceholder(isTextFieldFocused: Boolean) {

    val textAnim = remember { Animatable(0f) }
    LaunchedEffect(isTextFieldFocused) {
        textAnim.animateTo(
            targetValue = if (isTextFieldFocused) -150f else 0f,  // Move up when focused
            animationSpec = tween(
                durationMillis = 300,  // Duration for smooth animation
                easing = FastOutSlowInEasing
            )
        )
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Ask me anything!",
            style = MaterialTheme.typography.titleLarge.copy(
                color = Color(0xFFE0E0E0),
                fontWeight = FontWeight.Bold
            ),
            modifier = Modifier

                .graphicsLayer {
                    translationY = textAnim.value // Apply the animation to the text's visibility
            }
        )
    }
}
