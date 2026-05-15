package com.example.civinews.ui.screens.main

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.automirrored.outlined.List
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.List
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.civinews.ui.base.components.CiviNewsTopBar
import com.example.civinews.ui.screens.home.HomeScreen
import com.example.civinews.ui.screens.myReports.MyReportsScreen
import com.example.civinews.ui.screens.profile.ProfileScreen
import com.example.civinews.utils.Routes
import com.example.civinews.ui.theme.workSansFontFamily

data class BottomNavItem(
    val route: String,
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

@Composable
fun UserMainScreen(
    isDarkTheme: Boolean,
    onThemeChange: (Boolean) -> Unit,
    onNavigateToAddReport: () -> Unit,
    onNavigateToAboutUs: () -> Unit,
    onNavigateToReportDetail: (String) -> Unit,
    onNavigateToTerms: () -> Unit,
    onNavigateToPrivacy: () -> Unit,
    onNavigateToHelp: () -> Unit,
    onLogout: () -> Unit
) {
    val bottomNavController = rememberNavController()
    val navBackStackEntry by bottomNavController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: Routes.HOME

    val tabs = listOf(
        BottomNavItem(Routes.HOME, "Inicio", Icons.Filled.Home, Icons.Outlined.Home),
        BottomNavItem(Routes.MY_REPORTS, "Mis Avisos", Icons.AutoMirrored.Filled.List,
            Icons.AutoMirrored.Outlined.List
        ),
        BottomNavItem(Routes.PROFILE, "Perfil", Icons.Filled.AccountCircle, Icons.Outlined.AccountCircle)
    )

    Scaffold(
        topBar = {
            CiviNewsTopBar(
                title = when (currentRoute) {
                    Routes.PROFILE -> "Mi Perfil"
                    Routes.MY_REPORTS -> "Mis Avisos"
                    else -> "CiviNews"
                },
                isDarkTheme = isDarkTheme,
                onThemeChange = onThemeChange
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                tabs.forEach { tab ->
                    val isSelected = currentRoute == tab.route
                    NavigationBarItem(
                        icon = {
                            Icon(
                                imageVector = if (isSelected) tab.selectedIcon else tab.unselectedIcon,
                                contentDescription = tab.title
                            )
                        },
                        label = {
                            Text(
                                text = tab.title,
                                fontFamily = workSansFontFamily,
                                fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium,
                                fontSize = 12.sp
                            )
                        },
                        selected = isSelected,
                        onClick = {
                            if (currentRoute != tab.route) {
                                bottomNavController.navigate(tab.route) {
                                    popUpTo(bottomNavController.graph.startDestinationId) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = bottomNavController,
            startDestination = Routes.HOME,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Routes.HOME) {
                HomeScreen(
                    onNavigateToAddReport = onNavigateToAddReport,
                    onReportClick = onNavigateToReportDetail
                )
            }

            composable(Routes.MY_REPORTS) {
                MyReportsScreen(
                    onReportClick = onNavigateToReportDetail
                )
            }

            composable(Routes.PROFILE) {
                ProfileScreen(
                    onNavigateToAboutUs = onNavigateToAboutUs,
                    onNavigateToTerms = onNavigateToTerms,
                    onNavigateToPrivacy = onNavigateToPrivacy,
                    onNavigateToHelp = onNavigateToHelp,
                    onLogoutSuccess = onLogout
                )
            }
        }
    }
}