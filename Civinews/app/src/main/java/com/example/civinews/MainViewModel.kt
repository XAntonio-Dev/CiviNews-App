package com.example.civinews

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.civinews.data.local.AuthPreferences
import com.example.civinews.utils.Routes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val authPreferences: AuthPreferences
) : ViewModel() {

    var startDestination by mutableStateOf(Routes.AUTH)
        private set

    var isLoading by mutableStateOf(true)
        private set

    init {
        viewModelScope.launch {
            // Leemos el primer valor del DataStore al arrancar
            val token = authPreferences.authToken.first()

            startDestination = if (token.isNullOrBlank()) Routes.AUTH else Routes.MAIN_APP

            isLoading = false
        }
    }
}