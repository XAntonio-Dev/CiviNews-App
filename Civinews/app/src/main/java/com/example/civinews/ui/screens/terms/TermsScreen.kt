package com.example.civinews.ui.screens.terms

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Warning
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
fun TermsScreen(
    onNavigateBack: () -> Unit,
    isDarkTheme: Boolean,
    onThemeChange: (Boolean) -> Unit
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            CiviNewsTopBar(
                title = "Términos de Uso",
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
                text = "Términos y Condiciones de Uso",
                fontSize = 24.sp,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 24.dp, top = 8.dp)
            )

            LegalSection(
                title = "1. Aceptación y Objeto",
                content = "El acceso y uso de la aplicación móvil CiviNews atribuye la condición de Usuario e implica la aceptación íntegra, plena y sin reservas de los presentes Términos y Condiciones. CiviNews es una plataforma de participación ciudadana cuyo único fin es facilitar la comunicación de incidencias urbanas a la administración pública competente.",
                icon = Icons.Default.CheckCircle
            )

            LegalSection(
                title = "2. Normas de Conducta y Civismo",
                content = "El Usuario se compromete a utilizar la plataforma de buena fe, respetando la legalidad vigente, la moral y el orden público. Queda terminantemente prohibido:\n• Publicar avisos falsos, engañosos o que no correspondan a incidencias urbanas reales.\n• Subir material fotográfico que contenga información personal de terceros, rostros identificables, matrículas de vehículos no relacionadas con la incidencia, o contenido obsceno, ofensivo o ilegal.\n• Emplear la plataforma para fines comerciales, publicitarios o de acoso.",
                icon = Icons.Default.Person
            )

            LegalSection(
                title = "3. Multimedia y Ubicación",
                content = "Al reportar una incidencia, el Usuario otorga a CiviNews y a la administración correspondiente una licencia gratuita, no exclusiva y permanente para utilizar, reproducir y procesar las fotografías adjuntas con el fin de gestionar y resolver el reporte. La plataforma no accede en ningún momento al GPS ni a la ubicación del dispositivo; la localización de la incidencia se realiza de forma estrictamente manual mediante la introducción de la calle o dirección por parte del Usuario.",
                icon = Icons.Default.LocationOn
            )

            LegalSection(
                title = "4. Moderación y Sanciones",
                content = "CiviNews se reserva el derecho de admisión y permanencia. El equipo de administración y moderación tiene potestad absoluta para:\n• Rechazar, ocultar o eliminar cualquier aviso que incumpla las presentes normas.\n• Suspender temporalmente o banear de forma permanente y sin previo aviso a cualquier Usuario que realice un uso fraudulento, abusivo o malintencionado del sistema.",
                icon = Icons.Default.Security
            )

            LegalSection(
                title = "5. Responsabilidad y Garantías",
                content = "CiviNews es un canal de comunicación y no garantiza la resolución inmediata de las incidencias reportadas. La plataforma no se hace responsable de las opiniones o contenidos vertidos por los Usuarios, recayendo la responsabilidad civil y legal de los reportes exclusivamente sobre el perfil emisor.",
                icon = Icons.Default.Warning
            )

            LegalSection(
                title = "6. Cancelación y Cese de Uso",
                content = "El Usuario puede cesar el uso de la aplicación en cualquier momento. A través de la sección 'Perfil', el Usuario dispone de una herramienta de 'Borrado de Cuenta', la cual ejecutará una eliminación en cascada, destruyendo de forma irreversible sus datos de acceso y desvinculando o eliminando sus reportes del sistema.",
                icon = Icons.Default.Delete
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}