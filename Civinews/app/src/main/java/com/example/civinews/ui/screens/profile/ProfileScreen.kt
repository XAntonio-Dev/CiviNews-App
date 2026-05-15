package com.example.civinews.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ContactSupport
import androidx.compose.material.icons.filled.ContactSupport
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.civinews.ui.base.components.AlertDialogOkCancel
import com.example.civinews.ui.base.components.PasswordField
import com.example.civinews.ui.base.components.ProfileActionButton

@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    onNavigateToAboutUs: () -> Unit,
    onNavigateToTerms: () -> Unit,
    onNavigateToPrivacy: () -> Unit,
    onNavigateToHelp: () -> Unit,
    onLogoutSuccess: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val state = viewModel.state

    LaunchedEffect(state.isLoggedOut) {
        if (state.isLoggedOut) {
            onLogoutSuccess()
        }
    }

    val events = ProfileEvents(
        onEditNameClick = viewModel::onEditNameClick,
        onLogoutClick = viewModel::onLogoutClick,
        onDismissDialogs = viewModel::onDismissDialogs,
        onNameInputChange = viewModel::onNameInputChange,
        onSaveName = viewModel::onSaveName,
        onNavigateToAboutUs = onNavigateToAboutUs,
        onNavigateToTerms = onNavigateToTerms,
        onNavigateToPrivacy = onNavigateToPrivacy,
        onNavigateToHelp = onNavigateToHelp,
        onLogoutConfirm = viewModel::onLogoutConfirm,
        onShowPasswordDialog = viewModel::onShowPasswordDialog,
        onOldPasswordChange = viewModel::onOldPasswordChange,
        onNewPasswordChange = viewModel::onNewPasswordChange,
        onSavePassword = viewModel::onSavePassword,
        onShowDeleteDialog = viewModel::onShowDeleteDialog,
        onConfirmDelete = viewModel::onConfirmDelete
    )

    ProfileContent(
        modifier = modifier,
        state = state,
        events = events
    )
}

@Composable
fun ProfileContent(
    modifier: Modifier = Modifier,
    state: ProfileState,
    events: ProfileEvents
) {
    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier.fillMaxWidth().height(200.dp),
                contentAlignment = Alignment.BottomCenter
            ) {
                Box(
                    modifier = Modifier.fillMaxWidth().height(150.dp).align(Alignment.TopCenter)
                        .background(MaterialTheme.colorScheme.primaryContainer)
                )

                Box(
                    modifier = Modifier.size(110.dp).clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surface)
                        .border(4.dp, MaterialTheme.colorScheme.surface, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize().padding(4.dp).clip(CircleShape)
                            .background(MaterialTheme.colorScheme.secondaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Person,
                            contentDescription = "Avatar de usuario",
                            modifier = Modifier.size(60.dp),
                            tint = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = state.currentName,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                IconButton(onClick = events.onEditNameClick) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Editar nombre",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            if (state.isAdmin) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.tertiaryContainer,
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Text(
                        text = "Moderador",
                        color = MaterialTheme.colorScheme.onTertiaryContainer,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier.padding(horizontal = 24.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Email,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = state.currentEmail,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            if (state.message != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = state.message,
                    color = if (state.message.contains("Error") || state.message.contains("incorrecta"))
                        MaterialTheme.colorScheme.error else Color(0xFF4CAF50),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // --- BLOQUE 1: INFO Y SOPORTE (Cuadrícula 2x2) ---
            Column(
                modifier = Modifier.fillMaxWidth(0.85f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    ProfileActionButton("Nosotros", Icons.Default.Info, events.onNavigateToAboutUs, Modifier.weight(1f))
                    ProfileActionButton("Ayuda", Icons.AutoMirrored.Filled.ContactSupport, events.onNavigateToHelp, Modifier.weight(1f))
                }
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    ProfileActionButton("Términos", Icons.Default.Description, events.onNavigateToTerms, Modifier.weight(1f))
                    ProfileActionButton("Privacidad", Icons.Default.PrivacyTip, events.onNavigateToPrivacy, Modifier.weight(1f))
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- BLOQUE 2: ZONA DE SEGURIDAD (Vertical) ---
            OutlinedButton(
                onClick = events.onShowPasswordDialog,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth(0.85f).height(54.dp).padding(bottom = 8.dp)
            ) {
                Icon(Icons.Default.Lock, null, Modifier.size(20.dp).padding(end = 8.dp))
                Text("Cambiar Contraseña", fontSize = 15.sp, fontWeight = FontWeight.Bold)
            }

            OutlinedButton(
                onClick = events.onShowDeleteDialog,
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                modifier = Modifier.fillMaxWidth(0.85f).height(54.dp).padding(bottom = 8.dp)
            ) {
                Icon(Icons.Default.Delete, null, Modifier.size(20.dp).padding(end = 8.dp))
                Text("Borrar Cuenta Permanentemente", fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }

            ElevatedButton(
                onClick = events.onLogoutClick,
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.elevatedButtonColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.onErrorContainer
                ),
                modifier = Modifier.fillMaxWidth(0.85f).height(60.dp).padding(bottom = 16.dp)
            ) {
                Text("Cerrar Sesión", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }

        if (state.showEditNameDialog) {
            AlertDialogOkCancel(
                title = "Editar Nombre",
                okText = "Guardar",
                onConfirm = events.onSaveName,
                onDismiss = events.onDismissDialogs,
                content = {
                    Column {
                        OutlinedTextField(
                            value = state.newNameInput,
                            onValueChange = events.onNameInputChange,
                            label = { Text("Nuevo alias") },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        )
                        Text(
                            text = "Nota: Solo puedes cambiar tu nombre una vez cada 14 días.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
            )
        }

        if (state.showPasswordDialog) {
            AlertDialogOkCancel(
                title = "Cambiar Contraseña",
                icon = Icons.Default.Lock,
                okText = "Actualizar",
                onConfirm = events.onSavePassword,
                onDismiss = events.onDismissDialogs,
                content = {
                    Column {
                        PasswordField(value = state.oldPasswordInput, onChange = events.onOldPasswordChange, label = "Contraseña actual")
                        Spacer(modifier = Modifier.height(8.dp))
                        PasswordField(value = state.newPasswordInput, onChange = events.onNewPasswordChange, label = "Nueva contraseña")
                    }
                }
            )
        }

        if (state.showDeleteDialog) {
            AlertDialogOkCancel(
                title = "¿Eliminar cuenta permanentemente?",
                text = "Esta acción es irreversible. Se borrarán todos tus datos personales y las noticias que hayas publicado.",
                icon = Icons.Default.Warning,
                okText = "Eliminar",
                onConfirm = events.onConfirmDelete,
                onDismiss = events.onDismissDialogs
            )
        }

        if (state.showLogoutDialog) {
            AlertDialogOkCancel(
                title = "Cerrar Sesión",
                text = "¿Estás seguro de que deseas salir de CiviNews?",
                okText = "Salir",
                icon = Icons.Default.Warning,
                onConfirm = { events.onDismissDialogs(); events.onLogoutConfirm() },
                onDismiss = events.onDismissDialogs
            )
        }

        if (state.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.5f))
                    .clickable(indication = null, interactionSource = remember { MutableInteractionSource() }) {},
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary, modifier = Modifier.size(50.dp), strokeWidth = 5.dp)
            }
        }
    }
}