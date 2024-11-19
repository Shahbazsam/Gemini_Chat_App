package com.example.geminichatapp.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.geminichatapp.R

val Actor = FontFamily(
    Font(R.font.actor_regular, FontWeight.Normal)
)

val Alata = FontFamily(
    Font(R.font.alata_regular, FontWeight.Normal)
)

val OldStandart = FontFamily(
    Font(R.font.old_standard_regular, FontWeight.Normal),
    Font(R.font.old_standard_bold, FontWeight.Bold)
)

val Yatra = FontFamily(
    Font(R.font.yatra_regular, FontWeight.Normal)
)

val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    titleLarge = TextStyle(
        fontFamily = Yatra,
        fontWeight = FontWeight.Normal,
        fontSize = 20.sp
    ),
    titleMedium = TextStyle(
        fontFamily = OldStandart,
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp,
    ),
    bodyMedium = TextStyle(
        fontFamily = OldStandart,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp
    )
)