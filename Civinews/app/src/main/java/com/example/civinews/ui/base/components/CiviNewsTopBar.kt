package com.example.civinews.ui.base.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.civinews.ui.theme.newsreaderFontFamily

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CiviNewsTopBar(
    title: String,
    isDarkTheme: Boolean? = null,
    onThemeChange: ((Boolean) -> Unit)? = null,
    onNavigateBack: (() -> Unit)? = null,
    isTransparent: Boolean = false
) {
    val contentColor = if (isTransparent) Color.White else MaterialTheme.colorScheme.primary

    TopAppBar(
        title = {
            Text(
                text = title,
                fontFamily = newsreaderFontFamily,
                fontStyle = FontStyle.Italic,
                fontWeight = FontWeight.ExtraBold,
                color = contentColor,
                fontSize = 28.sp
            )
        },
        navigationIcon = {
            if (onNavigateBack != null) {
                val iconModifier = if (isTransparent) {
                    Modifier
                        .padding(start = 8.dp)
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.3f))
                } else {
                    Modifier
                }

                IconButton(onClick = onNavigateBack, modifier = iconModifier) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Volver",
                        tint = contentColor
                    )
                }
            }
        },
        actions = {
            if (isDarkTheme != null && onThemeChange != null) {
                IconButton(onClick = { onThemeChange(!isDarkTheme) }) {
                    Icon(
                        imageVector = if (isDarkTheme) Icons.Filled.LightMode else Icons.Filled.DarkMode,
                        contentDescription = "Alternar Modo Oscuro",
                        tint = contentColor
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = if (isTransparent) Color.Transparent else MaterialTheme.colorScheme.background,
            scrolledContainerColor = MaterialTheme.colorScheme.surface
        )
    )
}