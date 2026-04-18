package com.example.civinews.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.civinews.R
import com.example.civinews.ui.base.components.EmailField
import com.example.civinews.ui.base.components.PasswordField
import com.example.civinews.ui.icons.GoogleIcon
import com.example.civinews.ui.theme.newsreaderFontFamily

@Composable
fun AuthScreen(
    modifier: Modifier = Modifier,
    onAuthSuccess: (isAdmin: Boolean) -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val state = viewModel.state

    LaunchedEffect(state.isSuccess) {
        if (state.isSuccess) {
            onAuthSuccess(state.userIsAdmin)
        }
    }

    val events = AuthEvents(
        onModeChange = viewModel::onModeChange,
        onUsernameChange = viewModel::onUsernameChange,
        onEmailChange = viewModel::onEmailChange,
        onPasswordChange = viewModel::onPasswordChange,
        onSubmitClick = viewModel::submit
    )

    AuthContent(
        modifier = modifier,
        state = state,
        events = events
    )
}

@Composable
fun AuthContent(
    modifier: Modifier = Modifier,
    state: AuthState,
    events: AuthEvents
) {
    Box(modifier = modifier.fillMaxSize()) {

        // Formulario normal de fondo
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Text(
                text = stringResource(id = R.string.app_name),
                fontFamily = newsreaderFontFamily,
                fontStyle = FontStyle.Italic,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 48.sp,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = if (state.isLoginMode) stringResource(id = R.string.login_subtitle) else stringResource(id = R.string.register_subtitle),
                fontFamily = newsreaderFontFamily,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(32.dp))

            Surface(
                shape = RoundedCornerShape(32.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(24.dp)) {

                    AuthToggleSlider(
                        isLoginMode = state.isLoginMode,
                        onModeChanged = events.onModeChange
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    if (!state.isLoginMode) {
                        OutlinedTextField(
                            value = state.username,
                            onValueChange = events.onUsernameChange,
                            label = { Text(stringResource(id = R.string.auth_name_label)) },
                            isError = state.usernameError != null,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )
                        if (state.usernameError != null) {
                            Text(
                                text = state.usernameError,
                                color = MaterialTheme.colorScheme.error,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    EmailField(value = state.email, onChange = events.onEmailChange)
                    if (state.emailError != null) {
                        Text(
                            text = state.emailError,
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    PasswordField(value = state.password, onChange = events.onPasswordChange)
                    if (state.passwordError != null) {
                        Text(
                            text = state.passwordError,
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                        )
                    }

                    if (state.errorMessage != null) {
                        Text(
                            text = state.errorMessage,
                            color = if (state.errorMessage.contains("creada")) Color(0xFF4CAF50) else MaterialTheme.colorScheme.error,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 16.dp).align(Alignment.CenterHorizontally)
                        )
                    }

                    if (state.isLoginMode) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            TextButton(onClick = { /* TODO: Recuperar Pass */ }) {
                                Text(
                                    text = stringResource(id = R.string.login_forgot_password),
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    } else {
                        Spacer(modifier = Modifier.height(24.dp))
                    }

                    Button(
                        onClick = events.onSubmitClick,
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        enabled = !state.isLoading,
                        shape = RoundedCornerShape(28.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text(
                            text = if (state.isLoading) stringResource(id = R.string.auth_btn_loading)
                            else if (state.isLoginMode) stringResource(id = R.string.auth_btn_login)
                            else stringResource(id = R.string.auth_btn_register),
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        HorizontalDivider(modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.outlineVariant)
                        Text(
                            text = if (state.isLoginMode) stringResource(id = R.string.auth_or_login) else stringResource(id = R.string.auth_or_register),
                            modifier = Modifier.padding(horizontal = 8.dp),
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Bold
                        )
                        HorizontalDivider(modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.outlineVariant)
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    OutlinedButton(
                        onClick = { /* TODO: Google Auth */ },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(28.dp)
                    ) {
                        Icon(
                            imageVector = GoogleIcon(),
                            contentDescription = stringResource(id = R.string.auth_google_cd),
                            tint = Color.Unspecified
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(id = R.string.auth_google_button),
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Capa oscura de carga superpuesta
        if (state.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.6f))
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) { /* Absorbe clics vacíos para bloquear el fondo */ },
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(64.dp),
                    strokeWidth = 6.dp
                )
            }
        }
    }
}

@Composable
fun AuthToggleSlider(
    isLoginMode: Boolean,
    onModeChanged: (Boolean) -> Unit
) {
    Surface(
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .background(
                        color = if (isLoginMode) MaterialTheme.colorScheme.primary else Color.Transparent,
                        shape = RoundedCornerShape(24.dp)
                    )
                    .clickable { onModeChanged(true) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(id = R.string.auth_tab_login),
                    color = if (isLoginMode) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .background(
                        color = if (!isLoginMode) MaterialTheme.colorScheme.primary else Color.Transparent,
                        shape = RoundedCornerShape(24.dp)
                    )
                    .clickable { onModeChanged(false) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(id = R.string.auth_tab_register),
                    color = if (!isLoginMode) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }
    }
}