package com.example.civinews.data.local

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Inicializamos DataStore a nivel de Context
private val Context.dataStore by preferencesDataStore(name = "auth_prefs")

class AuthPreferences(private val context: Context) {

    // Claves exactas para guardar y leer
    companion object {
        val TOKEN_KEY = stringPreferencesKey("access_token")
        val IS_ADMIN_KEY = booleanPreferencesKey("is_admin")
    }

    // Lectura reactiva (Flow)
    val authToken: Flow<String?> = context.dataStore.data.map { prefs ->
        prefs[TOKEN_KEY]
    }

    val isAdmin: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[IS_ADMIN_KEY] ?: false
    }

    // Escritura asíncrona
    suspend fun saveAuthInfo(token: String, isAdmin: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[TOKEN_KEY] = token
            prefs[IS_ADMIN_KEY] = isAdmin
        }
    }
}