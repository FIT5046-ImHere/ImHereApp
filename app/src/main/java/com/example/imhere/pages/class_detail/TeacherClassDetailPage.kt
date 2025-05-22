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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.imhere.model.ClassSession
import com.example.imhere.navItems
import com.example.imhere.ui.theme.Blue1
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun TeacherClassDetailPage(navController: NavHostController, viewModel: TeacherClassDetailViewModel = hiltViewModel(),session: ClassSession)
{
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: ""
    val students by viewModel.students
    val formatter = remember { SimpleDateFormat("hh:mm a", Locale.getDefault()) }
    val dateFormatter = remember { SimpleDateFormat("EEE, MMM d", Locale.getDefault()) }
    val timeFormatter = remember { SimpleDateFormat("hh:mm a", Locale.getDefault()) }
    val dateFormatted = dateFormatter.format(session.startDateTime)
    val startTimeFormatted = timeFormatter.format(session.startDateTime)
    val endTimeFormatted = timeFormatter.format(session.endDateTime)

    LaunchedEffect(session.id){
        session.id?.let {
            viewModel.loadAttendance(it)
        }
    }
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
                text = " ${session.name}",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(15.dp))
            Text(
                text = "Class Location: ${session.location}",
                style = MaterialTheme.typography.titleMedium,
                color = Color.DarkGray
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "Class Time: $dateFormatted, $startTimeFormatted – $endTimeFormatted",
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

//preview
@Composable
fun TeacherClassDetailPreviewWrapper(
    navController: NavHostController,
    session: ClassSession,
    students: List<StudentAttendance>
) {
    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(20.dp)
        ) {
            // Header
            Text("Class Detail", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(20.dp))

            Text("Class Name:", style = MaterialTheme.typography.titleMedium, color = Color.DarkGray)
            Text(session.name, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)

            Spacer(modifier = Modifier.height(15.dp))
            Text("Class Location: ${session.location}", style = MaterialTheme.typography.titleMedium, color = Color.DarkGray)

            Spacer(modifier = Modifier.height(10.dp))
            Text("Class Time: ${session.startDateTime} - ${session.endDateTime}", style = MaterialTheme.typography.titleMedium, color = Color.DarkGray)

            Spacer(modifier = Modifier.height(15.dp))
            Divider()

            // Attendance
            Text("Attendance List", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(vertical = 12.dp))

            LazyColumn {
                items(students) { student ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(student.name, style = MaterialTheme.typography.bodyLarge)
                        Text(
                            text = student.status.replaceFirstChar { it.uppercase() },
                            color = when (student.status.lowercase()) {
                                "present" -> Color(0xFF2E7D32)
                                "late" -> Color(0xFFFF9800)
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
    val navController = rememberNavController()
    val mockSession = ClassSession(
        id = "session001",
        name = "Software Engineering",
        location = "Room B-204",
        unitCode = "FIT2001",
        teacherId = "teacher123",
        startDateTime = Date(),
        endDateTime = Date()
    )

    val fakeStudents = listOf(
        StudentAttendance("Alice Johnson", "present"),
        StudentAttendance("Bob Smith", "late"),
        StudentAttendance("Charlie Davis", "absent"),
        StudentAttendance("Diana Wong", "present")
    )

    TeacherClassDetailPreviewWrapper(
        navController = navController,
        session = mockSession,
        students = fakeStudents
    )
}

