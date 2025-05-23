package com.example.imhere

// creds for google calendar, the api key and the oauth client key?
// writing the code for all google cal shit

/* Using app authentication
The permissions or "authority" the principal has to access data or perform operations.
The act of authorization is carried out through code you write in your app.
This code informs the user that the app wishes to act on their behalf and, if allowed, uses
your app's unique credentials to obtain an access token from Google used to access data
or perform operations.
*/

// credential type used : OAuth 2 client ID

//client ID: 482684834489-p50fskjgsii7jjpbgn8hvac68h254shv.apps.googleusercontent.com
// API Key (unrestricted): AIzaSyAeJCybbzR-qRLF6x3nCCMv-2pyFcVhEQM

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.imhere.di.AccountServiceEntryPoint
import com.example.imhere.pages.classes.ClassesScreen
import com.example.imhere.pages.enrollment.EnrollmentScreen
import com.example.imhere.pages.home.HomePage
import com.example.imhere.pages.report.ReportPage
import com.example.imhere.pages.profile.ProfileScreen
import com.example.imhere.pages.class_detail.ClassDetailScreen
import com.example.imhere.pages.create_class.ClassDetailsForm

import com.example.imhere.pages.login.LoginScreen
import com.example.imhere.pages.register.RegisterScreen
import com.example.imhere.ui.theme.Blue1
import dagger.hilt.android.EntryPointAccessors

data class NavItem(val label: String, val icon: ImageVector, val route: String)

val navItems = listOf(
    NavItem("Home", Icons.Default.Home, "home"),
    NavItem("Schedules", Icons.Default.DateRange, "schedules"),
    NavItem("Report", Icons.Default.Build, "report"),
    NavItem("Profile", Icons.Default.Person, "profile"),
)

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MainScreen(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val context = LocalContext.current.applicationContext
    val bottomNavRoutes = navItems
        .map { it.route }

    val accountService = remember {
        EntryPointAccessors.fromApplication(
            context,
            AccountServiceEntryPoint::class.java
        ).accountService()
    }

    val isLoggedIn = accountService.hasUser
    val startDestination = if (isLoggedIn) "home" else "login"

    Scaffold(
        modifier = modifier,
        bottomBar = {
            if (isLoggedIn && currentRoute in bottomNavRoutes) {
                NavigationBar {
                    navItems.filterNot { it.route == "login" }.forEach { item ->
                        NavigationBarItem(
                            icon = { Icon(item.icon, contentDescription = item.label) },
                            label = { Text(item.label) },
                            selected = currentRoute == item.route,
                            onClick = {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.startDestinationId) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.primary,
                                indicatorColor = Blue1.copy(alpha = 0.2f)
                            )
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(paddingValues)
        ) {
            // Logged-in screens
            composable("home") { HomePage(navController = navController) }
            composable("schedules") { ClassesScreen(navController = navController) }
            composable("report") { ReportPage() }
            composable("profile") { ProfileScreen(navController = navController) }
            composable("createClass") { ClassDetailsForm(navController = navController) }

            composable("enrollments/{classSessionId}") { backStackEntry ->
                val classSessionId = backStackEntry.arguments?.getString("classSessionId") ?: ""
                EnrollmentScreen(navController = navController, classSessionId = classSessionId)
            }

            composable("classes/{classSessionId}") { backStackEntry ->
                val classSessionId = backStackEntry.arguments?.getString("classSessionId") ?: ""
                ClassDetailScreen(
                    navController = navController,
                    classSessionId = classSessionId
                )
            }

            // Auth screens
            composable("login") { LoginScreen(navController = navController) }
            composable("register") { RegisterScreen(navController = navController) }
        }
    }
}
