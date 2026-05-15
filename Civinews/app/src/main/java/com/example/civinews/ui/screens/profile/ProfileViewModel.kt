package com.example.civinews.ui.screens.profile

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.civinews.data.repository.AuthRepository
import com.example.civinews.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    var state by mutableStateOf(ProfileState())
        private set

    init {
        loadUserProfile()
    }

    private fun loadUserProfile() {
        viewModelScope.launch {
            state = state.copy(isLoading = true)
            val result = userRepository.getUserProfile()
            result.onSuccess { userResponse ->
                state = state.copy(
                    currentName = userResponse.name,
                    currentEmail = userResponse.email,
                    isAdmin = userResponse.isAdmin,
                    isLoading = false
                )
            }.onFailure { error ->
                state = state.copy(isLoading = false, message = "Error al cargar perfil")
                println("Error al cargar perfil: ${error.localizedMessage}")
            }
        }
    }

    fun onEditNameClick() { state = state.copy(showEditNameDialog = true, newNameInput = state.currentName) }
    fun onLogoutClick() { state = state.copy(showLogoutDialog = true) }
    fun onNameInputChange(newName: String) { state = state.copy(newNameInput = newName) }

    fun onDismissDialogs() {
        state = state.copy(
            showEditNameDialog = false,
            showLogoutDialog = false,
            showPasswordDialog = false,
            showDeleteDialog = false,
            oldPasswordInput = "",
            newPasswordInput = "",
            message = null
        )
    }

    fun onSaveName() {
        val newName = state.newNameInput.trim()
        if (newName.isBlank() || newName == state.currentName) {
            state = state.copy(showEditNameDialog = false)
            return
        }
        state = state.copy(showEditNameDialog = false, isLoading = true)

        viewModelScope.launch {
            val result = userRepository.updateUserName(newName)
            result.onSuccess { updatedUser ->
                state = state.copy(currentName = updatedUser.name, isLoading = false, message = "Nombre actualizado")
            }.onFailure { error ->
                state = state.copy(isLoading = false, message = "Error al actualizar nombre")
                println("Error al actualizar nombre: ${error.localizedMessage}")
            }
        }
    }

    fun onLogoutConfirm() {
        viewModelScope.launch {
            state = state.copy(showLogoutDialog = false)
            authRepository.clearSession()
            state = state.copy(isLoggedOut = true)
        }
    }

    fun onShowPasswordDialog() { state = state.copy(showPasswordDialog = true, message = null) }
    fun onOldPasswordChange(v: String) { state = state.copy(oldPasswordInput = v) }
    fun onNewPasswordChange(v: String) { state = state.copy(newPasswordInput = v) }
    fun onShowDeleteDialog() { state = state.copy(showDeleteDialog = true, message = null) }

    fun onSavePassword() {
        if (state.newPasswordInput.length < 6) {
            state = state.copy(message = "La contraseña debe tener al menos 6 caracteres")
            return
        }
        viewModelScope.launch {
            state = state.copy(isLoading = true, showPasswordDialog = false)
            val result = userRepository.changePassword(state.oldPasswordInput, state.newPasswordInput)

            result.fold(
                onSuccess = {
                    state = state.copy(
                        isLoading = false,
                        oldPasswordInput = "",
                        newPasswordInput = "",
                        message = "Contraseña actualizada correctamente"
                    )
                },
                onFailure = {
                    state = state.copy(
                        isLoading = false,
                        showPasswordDialog = true, // Reabrimos para que intente de nuevo
                        message = "Contraseña actual incorrecta."
                    )
                }
            )
        }
    }

    fun onConfirmDelete() {
        viewModelScope.launch {
            state = state.copy(isLoading = true, showDeleteDialog = false)
            val result = userRepository.deleteAccount()

            result.fold(
                onSuccess = {
                    authRepository.clearSession()
                    state = state.copy(isLoading = false, isLoggedOut = true)
                },
                onFailure = {
                    state = state.copy(isLoading = false, message = "Error al eliminar la cuenta")
                }
            )
        }
    }
}