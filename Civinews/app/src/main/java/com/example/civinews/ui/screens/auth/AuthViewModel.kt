package com.example.civinews.ui.screens.auth

import android.util.Patterns
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.civinews.data.local.AuthPreferences
import com.example.civinews.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: AuthRepository,
    private val authPreferences: AuthPreferences
) : ViewModel() {

    var state by mutableStateOf(AuthState())
        private set

    fun onModeChange(isLogin: Boolean) { state = AuthState(isLoginMode = isLogin) }
    fun onUsernameChange(v: String) { state = state.copy(username = v, usernameError = null, errorMessage = null) }
    fun onEmailChange(v: String) { state = state.copy(email = v, emailError = null, errorMessage = null) }
    fun onPasswordChange(v: String) { state = state.copy(password = v, passwordError = null, errorMessage = null) }

    fun submit() {
        if (state.email.isBlank() || !Patterns.EMAIL_ADDRESS.matcher(state.email).matches()) {
            state = state.copy(emailError = "Introduce un email válido")
            return
        }
        if (state.password.length < 6) {
            state = state.copy(passwordError = "Mínimo 6 caracteres")
            return
        }

        if (state.isLoginMode) login() else register()
    }

    private fun login() {
        viewModelScope.launch {
            state = state.copy(isLoading = true, errorMessage = null)
            val result = repository.login(state.email, state.password)

            result.fold(
                onSuccess = { response ->
                    authPreferences.saveAuthInfo(response.accessToken, response.user.isAdmin)
                    state = state.copy(isLoading = false, isSuccess = true, userIsAdmin = response.user.isAdmin)
                },
                onFailure = {
                    state = state.copy(isLoading = false, errorMessage = "Error: Credenciales incorrectas")
                }
            )
        }
    }

    private fun register() {
        if (state.username.isBlank()) {
            state = state.copy(usernameError = "El alias es obligatorio")
            return
        }
        viewModelScope.launch {
            state = state.copy(isLoading = true, errorMessage = null)
            val result = repository.register(state.email, state.username, state.password)

            result.fold(
                onSuccess = {
                    state = state.copy(
                        isLoading = false, isLoginMode = true, password = "",
                        errorMessage = "¡Cuenta creada! Inicia sesión."
                    )
                },
                onFailure = {
                    state = state.copy(isLoading = false, errorMessage = "Error al registrar")
                }
            )
        }
    }

    fun resetSuccessState() { state = state.copy(isSuccess = false) }

    // --- Lógica de Recuperar Contraseña ---
    fun showForgotPassDialog() {
        state = state.copy(showForgotPassDialog = true, forgotPassEmail = state.email, errorMessage = null)
    }

    fun dismissForgotPassDialog() {
        state = state.copy(showForgotPassDialog = false)
    }

    fun onForgotPassEmailChange(v: String) {
        state = state.copy(forgotPassEmail = v)
    }

    fun submitForgotPassword() {
        val email = state.forgotPassEmail.trim()

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            state = state.copy(errorMessage = "Introduce un email válido para la recuperación.")
            return
        }

        viewModelScope.launch {
            // Activamos loading y cerramos el diálogo para que el usuario vea el progreso
            state = state.copy(isLoading = true, showForgotPassDialog = false, errorMessage = null)

            val result = repository.resetPassword(email)

            result.fold(
                onSuccess = {
                    state = state.copy(
                        isLoading = false,
                        errorMessage = "Si el correo existe, recibirás un enlace en unos minutos."
                    )
                },
                onFailure = { error ->
                    state = state.copy(
                        isLoading = false,
                        errorMessage = "No se pudo procesar la solicitud. Revisa tu conexión."
                    )
                }
            )
        }
    }
}