package com.example.civinews.ui.screens.audit

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.civinews.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserAuditViewModel @Inject constructor(
    private val repository: UserRepository
) : ViewModel() {

    var state: UserAuditState by mutableStateOf(UserAuditState.Loading)
        private set

    // Centralizamos la búsqueda aquí (Única Fuente de Verdad)
    var searchQuery: String by mutableStateOf("")
        private set

    private var searchJob: Job? = null

    init {
        loadUsers()
    }

    fun loadUsers() {
        viewModelScope.launch {
            state = UserAuditState.Loading
            val result = repository.getAllUsers()

            state = if (result.isSuccess) {
                val users = result.getOrDefault(emptyList())
                if (users.isEmpty()) {
                    UserAuditState.NoData
                } else {
                    UserAuditState.Success(dataset = users)
                }
            } else {
                UserAuditState.Error(message = "Error de conexión: ${result.exceptionOrNull()?.localizedMessage}")
            }
        }
    }

    // Actualiza el texto y lanza la petición con debounce
    fun updateSearchQuery(query: String) {
        searchQuery = query
        searchJob?.cancel()

        if (query.isBlank()) {
            loadUsers()
            return
        }

        searchJob = viewModelScope.launch {
            delay(1500)
            state = UserAuditState.Loading
            val result = repository.searchUsers(query)

            state = if (result.isSuccess) {
                val users = result.getOrDefault(emptyList())
                if (users.isEmpty()) {
                    UserAuditState.NoData
                } else {
                    UserAuditState.Success(dataset = users)
                }
            } else {
                UserAuditState.Error(message = "Error en búsqueda: ${result.exceptionOrNull()?.localizedMessage}")
            }
        }
    }

    fun toggleRole(userId: String) {
        viewModelScope.launch {
            state = UserAuditState.Loading
            val result = repository.toggleUserRole(userId)

            if (result.isSuccess) {
                refreshCurrentView()
            } else {
                state = UserAuditState.Error("No se pudo modificar el rol")
            }
        }
    }

    fun banUser(userId: String) {
        viewModelScope.launch {
            state = UserAuditState.Loading
            val result = repository.banUser(userId)

            if (result.isSuccess) {
                refreshCurrentView()
            } else {
                state = UserAuditState.Error("No se pudo eliminar el usuario")
            }
        }
    }

    // Mantiene el filtro de búsqueda si recargamos la lista tras una acción
    private fun refreshCurrentView() {
        if (searchQuery.isNotBlank()) {
            updateSearchQuery(searchQuery)
        } else {
            loadUsers()
        }
    }
}