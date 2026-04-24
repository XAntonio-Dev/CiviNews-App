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

    fun onModeChange(isLogin: Boolean) {
        state = AuthState(isLoginMode = isLogin)
    }

    fun onUsernameChange(v: String) {
        state = state.copy(username = v, usernameError = null, errorMessage = null)
    }

    fun onEmailChange(v: String) {
        state = state.copy(email = v, emailError = null, errorMessage = null)
    }

    fun onPasswordChange(v: String) {
        state = state.copy(password = v, passwordError = null, errorMessage = null)
    }

    fun submit() {
        if (state.email.isBlank()) {
            state = state.copy(emailError = "El email es obligatorio")
            return
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(state.email).matches()) {
            state = state.copy(emailError = "Introduce un email válido (ej: hola@correo.com)")
            return
        }

        if (state.password.isBlank()) {
            state = state.copy(passwordError = "La contraseña es obligatoria")
            return
        }
        if (state.password.length < 6) {
            state = state.copy(passwordError = "La contraseña debe tener al menos 6 caracteres")
            return
        }

        if (state.isLoginMode) {
            login()
        } else {
            if (state.username.isBlank()) {
                state = state.copy(usernameError = "El alias es obligatorio")
                return
            }
            register()
        }
    }

    private fun login() {
        viewModelScope.launch {
            state = state.copy(isLoading = true, errorMessage = null)

            val result = repository.login(state.email, state.password)

            result.fold(
                onSuccess = { response ->
                    authPreferences.saveAuthInfo(
                        token = response.accessToken,
                        isAdmin = response.user.isAdmin
                    )

                    state = state.copy(isLoading = false, isSuccess = true, userIsAdmin = response.user.isAdmin)
                },
                onFailure = {
                    state = state.copy(
                        isLoading = false,
                        errorMessage = "Error: Credenciales incorrectas o servidor caído."
                    )
                }
            )
        }
    }

    private fun register() {
        viewModelScope.launch {
            state = state.copy(isLoading = true, errorMessage = null)

            val result = repository.register(state.email, state.username, state.password)

            result.fold(
                onSuccess = {
                    // Éxito: Limpiamos la password, pasamos a modo Login y avisamos al usuario
                    state = state.copy(
                        isLoading = false,
                        isLoginMode = true,
                        password = "",
                        errorMessage = "¡Cuenta creada! Por favor, inicia sesión."
                    )
                },
                onFailure = {
                    state = state.copy(
                        isLoading = false,
                        errorMessage = "Error: El email ya existe o hay un fallo de red."
                    )
                }
            )
        }
    }

    fun resetSuccessState() {
        state = state.copy(isSuccess = false)
    }
}