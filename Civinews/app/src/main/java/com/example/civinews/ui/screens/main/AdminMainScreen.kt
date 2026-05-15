package com.example.civinews.ui.screens.main

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.civinews.ui.base.components.CiviNewsTopBar
import com.example.civinews.ui.screens.admin.AdminScreen
import com.example.civinews.ui.screens.profile.ProfileScreen
import com.example.civinews.utils.Routes
import com.example.civinews.ui.theme.workSansFontFamily

@Composable
fun AdminMainScreen(
    isDarkTheme: Boolean,
    onThemeChange: (Boolean) -> Unit,
    onNavigateToAboutUs: () -> Unit,
    onNavigateToTerms: () -> Unit,
    onNavigateToPrivacy: () -> Unit,
    onNavigateToHelp: () -> Unit,
    onLogout: () -> Unit
) {
    val adminNavController = rememberNavController()
    val navBackStackEntry by adminNavController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: Routes.ADMIN_PENDING

    val tabs = listOf(
        BottomNavItem(Routes.ADMIN_PENDING, "Moderación", Icons.Filled.Security, Icons.Outlined.Security),
        BottomNavItem(Routes.ADMIN_PROFILE, "Perfil", Icons.Filled.AccountCircle, Icons.Outlined.AccountCircle)
    )

    Scaffold(
        topBar = {
            CiviNewsTopBar(
                title = if (currentRoute == Routes.ADMIN_PROFILE) "Mi Perfil" else "CiviNews Admin",
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
                                adminNavController.navigate(tab.route) {
                                    popUpTo(adminNavController.graph.startDestinationId) { saveState = true }
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
            navController = adminNavController,
            startDestination = Routes.ADMIN_PENDING,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Routes.ADMIN_PENDING) {
                AdminScreen()
            }

            composable(Routes.ADMIN_PROFILE) {
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