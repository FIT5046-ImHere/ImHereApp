package com.example.imhere.pages.class_detail

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.imhere.navItems
import com.example.imhere.ui.theme.Blue1

// Data model
data class StudentAttendance(
    val name: String,
    val status: String // "present", "late", "absent"
)

@Composable
fun TeacherClassDetailPage(
    navController: NavHostController,
    className: String,
    startTime: String,
    endTime: String,
    students: List<StudentAttendance>
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: ""

    Scaffold(
        bottomBar = {
            NavigationBar {
                navItems.filterNot { it.route == "login" }.forEach { item ->
                    NavigationBarItem(
                        icon = { Icon(item.icon, contentDescription = item.label) },
                        label = { Text(item.label) },
                        selected = currentRoute == item.route,
                        onClick = {
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
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
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    start = 20.dp,
                    end = 20.dp,
                    top = paddingValues.calculateTopPadding() + 30.dp,
                    bottom = paddingValues.calculateBottomPadding() + 30.dp
                )
        )
        {
            // --- Class Info Header ---
            Text(
                text = "Class Detail",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Class Name:",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium,
                color = Color.DarkGray
            )
            // --- Class Name ---
            Text(
                text = " $className",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(15.dp))
            Text(
                text = "Class Time: $startTime - $endTime",
                style = MaterialTheme.typography.titleMedium,
                color = Color.DarkGray
            )

            Spacer(modifier = Modifier.height(15.dp))
            Divider()

            // --- Attendance List Header ---
            Text(
                text = "Attendance List",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(vertical = 12.dp)
            )

            // --- List of Students and Statuses ---
            LazyColumn {
                items(students) { student ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = student.name,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            text = student.status.replaceFirstChar { it.uppercase() },
                            color = when (student.status.lowercase()) {
                                "present" -> Color(0xFF2E7D32)  // Green
                                "late" -> Color(0xFFFF9800)     // Orange
                                "absent" -> Color.Red
                                else -> Color.Gray
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    Divider()
                }
            }
        }
    }
}

    @Preview(showBackground = true)
    @Composable
    fun PreviewTeacherClassDetailPage() {
        val sampleStudents = listOf(
            StudentAttendance("Alice Johnson", "present"),
            StudentAttendance("Bob Smith", "late"),
            StudentAttendance("Charlie Davis", "absent"),
            StudentAttendance("Diana Wong", "present")
        )

        TeacherClassDetailPage(
            navController = rememberNavController(),
            className = "FIT5046 - Mobile Computing",
            startTime = "09:00 AM",
            endTime = "10:00 AM",
            students = sampleStudents
        )
    }
