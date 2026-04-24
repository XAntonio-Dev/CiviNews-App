package com.example.civinews.utils

object Constants {
    // Esta IP apunta directamente al localhost de tu ordenador desde el emulador
    const val BASE_URL = "http://10.0.2.2:8000/"
}

object Routes {
    // Rutas Raíz (Pantalla completa)
    const val AUTH = "auth"
    const val MAIN_APP = "main_app"
    const val ADD_REPORT = "add_report"
    const val ADMIN = "admin"

    // Rutas Internas (Pestañas de la BottomBar)
    const val HOME = "home"
    const val MY_REPORTS = "my_reports"
    const val PROFILE = "profile"

    const val ADMIN_PENDING = "admin_pending"
    const val ADMIN_PROFILE = "admin_profile"
}