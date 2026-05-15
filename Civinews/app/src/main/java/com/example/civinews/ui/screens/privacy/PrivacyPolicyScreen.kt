package com.example.civinews.ui.screens.privacy

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Article
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Update
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.civinews.ui.base.components.CiviNewsTopBar
import com.example.civinews.ui.base.components.LegalSection

@Composable
fun PrivacyPolicyScreen(
    onNavigateBack: () -> Unit,
    isDarkTheme: Boolean,
    onThemeChange: (Boolean) -> Unit
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            CiviNewsTopBar(
                title = "Privacidad",
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
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            Text(
                text = "Política de Privacidad y RGPD",
                fontSize = 24.sp,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 24.dp, top = 8.dp)
            )

            LegalSection(
                title = "1. Responsable del Tratamiento",
                content = "Los datos personales proporcionados a través de la aplicación móvil CiviNews serán tratados por los administradores de la plataforma y cedidos exclusivamente a la administración pública local competente para la gestión y resolución de las incidencias reportadas.",
                icon = Icons.Default.AccountBalance
            )

            LegalSection(
                title = "2. Datos Recopilados",
                content = "CiviNews opera bajo el estricto principio de minimización de datos:\n• Cuenta: Correo electrónico, alias y contraseña (encriptada de forma irreversible mediante hashing).\n• Ubicación: Se recaba exclusivamente mediante la introducción manual de la dirección o calle por parte del usuario. CiviNews no solicita permisos de ubicación ni accede en ningún momento al GPS del dispositivo.\n• Multimedia: Fotografías aportadas voluntariamente para documentar la incidencia.",
                icon = Icons.Default.Article
            )

            LegalSection(
                title = "3. Legitimación y Cesión",
                content = "La base legal para el tratamiento de estos datos es el consentimiento explícito otorgado por el Usuario al registrarse y al enviar cada reporte. CiviNews garantiza que los datos personales no serán vendidos, alquilados ni cedidos a entidades privadas o anunciantes.",
                icon = Icons.Default.Share
            )

            LegalSection(
                title = "4. Plazos de Conservación",
                content = "Los datos personales de la cuenta se conservarán mientras el Usuario mantenga su perfil activo. Los datos recopilados en los avisos se emplearán durante el tiempo necesario para la resolución del problema. Si el usuario decide darse de baja, la totalidad de sus datos serán eliminados.",
                icon = Icons.Default.Update
            )

            LegalSection(
                title = "5. Seguridad de la Información",
                content = "Aplicamos medidas técnicas avanzadas para proteger la confidencialidad de los datos, incluyendo conexiones seguras (HTTPS/TLS) entre la aplicación y nuestros servidores, y encriptación de credenciales para evitar tratamientos no autorizados.",
                icon = Icons.Default.Security
            )

            LegalSection(
                title = "6. Derechos ARCO/RGPD",
                content = "El Usuario tiene pleno control sobre sus datos. Desde la sección 'Perfil', puede modificar su alias o ejecutar el 'Borrado de Cuenta'. Esta acción es irreversible y aplica un protocolo de eliminación en cascada: destruye permanentemente la cuenta, las credenciales, y borra de forma definitiva todos los reportes, imágenes e incidencias publicadas en la plataforma.",
                icon = Icons.Default.DeleteForever
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}