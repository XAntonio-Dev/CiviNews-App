package com.example.civinews.ui.screens.audit

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.civinews.data.models.user.UserResponse

@Composable
fun UserAuditScreen(
    modifier: Modifier = Modifier,
    viewModel: UserAuditViewModel = hiltViewModel()
) {
    val state = viewModel.state
    val searchQuery = viewModel.searchQuery

    val events = UserAuditEvents(
        onToggleRoleClick = viewModel::toggleRole,
        onBanClick = viewModel::banUser,
        onSearch = viewModel::updateSearchQuery,
        onRefresh = viewModel::loadUsers
    )

    LaunchedEffect(Unit) {
        if (viewModel.searchQuery.isBlank()) {
            viewModel.loadUsers()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
    ) {
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "AUDITORÍA",
            color = MaterialTheme.colorScheme.primary,
            fontSize = 18.sp,
            fontWeight = FontWeight.ExtraBold,
        )
        Text(
            text = "Gestión de Usuarios",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Administra los accesos y sanciona comportamientos.",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
        )

        OutlinedTextField(
            value = searchQuery,
            onValueChange = events.onSearch,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Buscar por alias o email...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Buscar") },
            shape = RoundedCornerShape(12.dp),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
            ),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Box(modifier = Modifier.fillMaxSize()) {
            when (state) {
                is UserAuditState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.TopCenter).padding(top = 32.dp)
                    )
                }
                is UserAuditState.Error -> {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = state.message,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            modifier = Modifier.padding(16.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                }
                is UserAuditState.NoData -> {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "No hay resultados para esta búsqueda.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(24.dp).fillMaxWidth(),
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
                is UserAuditState.Success -> {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(state.dataset) { user ->
                            UserCard(
                                user = user,
                                onToggleRole = { events.onToggleRoleClick(user.id) },
                                onBan = { events.onBanClick(user.id) }
                            )
                        }
                        item { Spacer(modifier = Modifier.height(80.dp)) }
                    }
                }
            }
        }
    }
}

@Composable
fun UserCard(
    user: UserResponse,
    onToggleRole: () -> Unit,
    onBan: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.secondaryContainer,
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSecondaryContainer,
                    modifier = Modifier.padding(12.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = user.name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = user.email,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(6.dp))
                Surface(
                    color = if (user.isAdmin) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = if (user.isAdmin) "ADMIN" else "CIUDADANO",
                        color = if (user.isAdmin) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.errorContainer,
                    modifier = Modifier.size(40.dp).clickable { onBan() }
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteForever,
                        contentDescription = "Banear",
                        tint = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.padding(10.dp)
                    )
                }
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.surface,
                    modifier = Modifier.size(40.dp).clickable { onToggleRole() },
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                ) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = "Rol",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(10.dp)
                    )
                }
            }
        }
    }
}