package com.example.civinews.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.civinews.R

// 1. Declaramos las familias tipográficas
val newsreaderFontFamily = FontFamily(
    Font(R.font.newsreader_regular, FontWeight.Normal),
    Font(R.font.newsreader_italic, FontWeight.Normal, FontStyle.Italic),
    Font(R.font.newsreader_bold, FontWeight.Bold),
    Font(R.font.newsreader_bold_italic, FontWeight.Bold, FontStyle.Italic)
)

val workSansFontFamily = FontFamily(
    Font(R.font.work_sans_regular, FontWeight.Normal),
    Font(R.font.work_sans_medium, FontWeight.Medium),
    Font(R.font.work_sans_bold, FontWeight.Bold)
)

// 2. Sobrescribimos la tipografía por defecto de Material 3
val Typography = Typography(
    // Títulos grandes de la app (Home, Cabeceras)
    headlineLarge = TextStyle(
        fontFamily = newsreaderFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp,
        lineHeight = 40.sp,
        letterSpacing = 0.sp
    ),
    // Títulos de las tarjetas
    titleLarge = TextStyle(
        fontFamily = newsreaderFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    // Texto normal
    bodyLarge = TextStyle(
        fontFamily = workSansFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = workSansFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp
    ),
    // Botones y etiquetas
    labelLarge = TextStyle(
        fontFamily = workSansFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),
    labelMedium = TextStyle(
        fontFamily = workSansFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
)