package com.example.civinews.ui.screens.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.civinews.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {
    var state by mutableStateOf(LoginState())
        private set

    fun onEmailChange(v: String) {
        state = state.copy(email = v, emailError = null, errorMessage = null)
    }

    fun onPasswordChange(v: String) {
        state = state.copy(password = v, errorMessage = null)
    }

    // Lógica de validación y llamada al backend
    fun login() {
        if (state.email.isBlank()) {
            state = state.copy(emailError = "El email es obligatorio")
            return
        }

        viewModelScope.launch {
            state = state.copy(isLoading = true)
            try {
                // Delegamos la petición al repositorio
                val response = repository.login(state.email, state.password)
                state = state.copy(isLoading = false, isSuccess = true)
            } catch (e: Exception) {
                state = state.copy(isLoading = false, errorMessage = "Error: ${e.message}")
            }
        }
    }
}