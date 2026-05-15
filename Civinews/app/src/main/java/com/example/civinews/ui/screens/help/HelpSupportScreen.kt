package com.example.civinews.ui.screens.help

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.civinews.ui.base.components.CiviNewsTopBar

@Composable
fun HelpSupportScreen(
    onNavigateBack: () -> Unit,
    isDarkTheme: Boolean,
    onThemeChange: (Boolean) -> Unit
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            CiviNewsTopBar(
                title = "Centro de Ayuda",
                isDarkTheme = isDarkTheme,
                onThemeChange = onThemeChange,
                onNavigateBack = onNavigateBack
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 16.dp)
        ) {
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text(
                        text = "Preguntas Frecuentes (FAQ)",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(24.dp))

                    FaqItem(
                        question = "¿Cómo reporto una nueva incidencia?",
                        answer = "Ve a la pestaña 'Inicio' y presiona el botón '+' flotante. Podrás añadir una fotografía de tu galería o cámara, una descripción detallada y la ubicación exacta del problema."
                    )

                    FaqItem(
                        question = "¿Quién puede ver la información que publico?",
                        answer = "Las incidencias que reportes serán gestionadas directamente por los administradores y moderadores del ayuntamiento. Tus credenciales son totalmente privadas."
                    )

                    FaqItem(
                        question = "¿Para qué necesita la aplicación mi ubicación?",
                        answer = "El GPS únicamente se activa en el momento en que decides crear un nuevo reporte para poder adjuntar las coordenadas exactas de la incidencia. No realizamos seguimiento en segundo plano."
                    )

                    FaqItem(
                        question = "¿Puedo cambiar mi nombre de usuario?",
                        answer = "Sí, puedes editar tu alias desde tu 'Perfil' pulsando el icono del lápiz. Por medidas de seguridad y para evitar spam, solo se permite un cambio de nombre cada 14 días."
                    )

                    FaqItem(
                        question = "¿Qué hago si olvido mi contraseña?",
                        answer = "En la pantalla de inicio de sesión tienes la opción 'Olvidé mi contraseña'. Te enviaremos un correo electrónico con un enlace seguro para restablecerla."
                    )

                    FaqItem(
                        question = "¿Qué ocurre si elimino mi cuenta?",
                        answer = "Es una acción definitiva e irreversible. Se borrarán permanentemente todos tus datos personales, así como las incidencias e imágenes que hayas subido, cumpliendo estrictamente con la normativa RGPD."
                    )
                }
            }
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun FaqItem(question: String, answer: String) {
    Column(modifier = Modifier.padding(bottom = 20.dp)) {
        Text(
            text = question,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = answer,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            lineHeight = 20.sp
        )
    }
}