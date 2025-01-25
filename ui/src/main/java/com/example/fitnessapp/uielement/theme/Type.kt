package com.example.fitnessapp.uielement.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val robotoLight = FontFamily.Default

val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = robotoLight,
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp
    ),

    displayMedium = TextStyle(
        fontFamily = robotoLight,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    ),
    labelSmall = TextStyle(
        fontFamily = robotoLight,
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp,
    ),
    displayLarge = TextStyle(
        fontFamily = robotoLight,
        fontWeight = FontWeight.Bold,
        fontSize = 30.sp
    ),
    displaySmall = TextStyle(
        fontFamily = robotoLight,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 12.sp,
        letterSpacing = 0.5.sp
    )
)
