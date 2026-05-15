package com.example.civinews.utils

object Constants {
    const val BASE_URL = "http://10.0.2.2:8000/"
}

object Routes {
    // --- RUTAS RAÍZ (Controladas por AppNavigation, ocupan toda la pantalla) ---
    const val AUTH = "auth"
    const val MAIN_APP = "main_app" // UserMainScreen
    const val ADMIN = "admin"       // AdminMainScreen

    // Pantallas secundarias sobrepuestas
    const val ADD_REPORT = "add_report"
    const val ABOUT_US = "about_us"
    const val TERMS = "terms"
    const val REPORT_DETAIL = "report_detail/{reportId}" // Preparado para NavArgs

    // Helper para construir la ruta del detalle
    fun createReportDetailRoute(reportId: String) = "report_detail/$reportId"

    // --- RUTAS INTERNAS USUARIO (Pestañas de BottomBar en UserMainScreen) ---
    const val HOME = "home"
    const val MY_REPORTS = "my_reports"
    const val PROFILE = "profile"

    // --- RUTAS INTERNAS ADMIN (Pestañas de BottomBar en AdminMainScreen) ---
    const val ADMIN_PENDING = "admin_pending"
    const val ADMIN_PROFILE = "admin_profile"
    const val PRIVACY = "privacy"
    const val HELP = "help"
}