package com.example.civinews.data.network

import com.example.civinews.data.local.AuthPreferences
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    private val authPreferences: AuthPreferences
) : Interceptor {
    //Función para inyectar token en las peticiones de Retrofit
    override fun intercept(chain: Interceptor.Chain): Response {
        // Leemos el token al instante bloqueando solo este hilo secundario
        val token = runBlocking { authPreferences.authToken.firstOrNull() }

        val requestBuilder = chain.request().newBuilder()

        if (!token.isNullOrEmpty()) {
            requestBuilder.addHeader("Authorization", "Bearer $token")
        }

        return chain.proceed(requestBuilder.build())
    }
}