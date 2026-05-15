package com.example.civinews.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.civinews.ui.screens.about.AboutUsScreen
import com.example.civinews.ui.screens.addReport.AddReportScreen
import com.example.civinews.ui.screens.auth.AuthScreen
import com.example.civinews.ui.screens.help.HelpSupportScreen
import com.example.civinews.ui.screens.main.AdminMainScreen
import com.example.civinews.ui.screens.main.UserMainScreen
import com.example.civinews.ui.screens.privacy.PrivacyPolicyScreen
import com.example.civinews.ui.screens.reportDetail.ReportDetailScreen
import com.example.civinews.ui.screens.terms.TermsScreen
import com.example.civinews.utils.Routes

@Composable
fun AppNavigation(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = Routes.AUTH,
    isDarkTheme: Boolean,
    onThemeChange: (Boolean) -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {

        composable(Routes.AUTH) {
            AuthScreen(
                onAuthSuccess = { isAdmin ->
                    val route = if (isAdmin) Routes.ADMIN else Routes.MAIN_APP
                    navController.navigate(route) {
                        popUpTo(Routes.AUTH) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.MAIN_APP) {
            UserMainScreen(
                isDarkTheme = isDarkTheme,
                onThemeChange = onThemeChange,
                onNavigateToAddReport = { navController.navigate(Routes.ADD_REPORT) },
                onNavigateToReportDetail = { reportId ->
                    navController.navigate(Routes.createReportDetailRoute(reportId))
                },
                onNavigateToAboutUs = { navController.navigate(Routes.ABOUT_US) },
                onNavigateToTerms = { navController.navigate(Routes.TERMS) },
                onNavigateToPrivacy = { navController.navigate(Routes.PRIVACY) },
                onNavigateToHelp = { navController.navigate(Routes.HELP) },
                onLogout = {
                    navController.navigate(Routes.AUTH) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.ADMIN) {
            AdminMainScreen(
                isDarkTheme = isDarkTheme,
                onThemeChange = onThemeChange,
                onNavigateToAboutUs = { navController.navigate(Routes.ABOUT_US) },
                onNavigateToTerms = { navController.navigate(Routes.TERMS) },
                onNavigateToPrivacy = { navController.navigate(Routes.PRIVACY) },
                onNavigateToHelp = { navController.navigate(Routes.HELP) },
                onLogout = {
                    navController.navigate(Routes.AUTH) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.ABOUT_US) {
            AboutUsScreen(
                onNavigateBack = { navController.popBackStack() },
                isDarkTheme = isDarkTheme,
                onThemeChange = onThemeChange
            )
        }

        composable(Routes.TERMS) {
            TermsScreen(
                onNavigateBack = { navController.popBackStack() },
                isDarkTheme = isDarkTheme,
                onThemeChange = onThemeChange
            )
        }

        composable(Routes.PRIVACY) {
            PrivacyPolicyScreen(
                onNavigateBack = { navController.popBackStack() },
                isDarkTheme = isDarkTheme,
                onThemeChange = onThemeChange
            )
        }

        composable(Routes.HELP) {
            HelpSupportScreen(
                onNavigateBack = { navController.popBackStack() },
                isDarkTheme = isDarkTheme,
                onThemeChange = onThemeChange
            )
        }

        composable(Routes.ADD_REPORT) {
            AddReportScreen(
                onNavigateBack = { navController.popBackStack() },
                isDarkTheme = isDarkTheme,
                onThemeChange = onThemeChange
            )
        }

        composable(
            route = Routes.REPORT_DETAIL,
            arguments = listOf(navArgument("reportId") { type = NavType.StringType })
        ) { backStackEntry ->
            val reportId = backStackEntry.arguments?.getString("reportId") ?: ""
            ReportDetailScreen(
                reportId = reportId,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}