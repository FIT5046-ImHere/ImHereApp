package com.example.imhere.pages.classes

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.imhere.model.ClassSession
import com.example.imhere.model.ClassSessionRecurrence
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ClassesScreen(navController: NavHostController) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Text(
                "Schedules",
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            /*
            Text(
            "Your Classes:",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(bottom = 8.dp)
            )
            */

        }
        item {
            Text(
                "Your Classes:",
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
        val classes = listOf(
            ClassSession(
                id = "1",
                name = "Mathematics 101",
                location = "Room A-101",
                unitCode = "MAT101",
                teacherId = "teacher1", // Prof. Smith
                recurrence = ClassSessionRecurrence.WEEKLY,
                startDateTime = createDate(2025, Calendar.MAY, 23, 12, 0),
                endDateTime = createDate(2025, Calendar.MAY, 23, 14, 0)
            ),
            ClassSession(
                id = "2",
                name = "Chemistry 301",
                location = "Lab C-105",
                unitCode = "CHE301",
                teacherId = "teacher2", // Prof. Jones
                recurrence = ClassSessionRecurrence.ONCE,
                startDateTime = createDate(2025, Calendar.MAY, 23, 14, 0),
                endDateTime = createDate(2025, Calendar.MAY, 23, 16, 0)
            ),
            ClassSession(
                id = "3",
                name = "Computer Science 101",
                location = "Room D-201",
                unitCode = "CS101",
                teacherId = "teacher2", // Prof. Jones
                recurrence = ClassSessionRecurrence.WEEKLY,
                startDateTime = createDate(2025, Calendar.MAY, 24, 12, 0),
                endDateTime = createDate(2025, Calendar.MAY, 24, 14, 0)
            ),
            ClassSession(
                id = "4",
                name = "Biology 201",
                location = "Lab E-302",
                unitCode = "BIO201",
                teacherId = "teacher3", // Prof. Brown
                recurrence = ClassSessionRecurrence.BIWEEKLY,
                startDateTime = createDate(2025, Calendar.MAY, 24, 14, 0),
                endDateTime = createDate(2025, Calendar.MAY, 24, 16, 0)
            ),
            ClassSession(
                id = "5",
                name = "History 101",
                location = "Room F-101",
                unitCode = "HIS101",
                teacherId = "teacher3", // Prof. Brown
                recurrence = ClassSessionRecurrence.MONTHLY,
                startDateTime = createDate(2025, Calendar.MAY, 25, 12, 0),
                endDateTime = createDate(2025, Calendar.MAY, 25, 14, 0)
            ),
            ClassSession(
                id = "6",
                name = "Literature 202",
                location = "Room G-204",
                unitCode = "LIT202",
                teacherId = "teacher1", // Prof. Smith
                recurrence = ClassSessionRecurrence.WEEKLY,
                startDateTime = createDate(2025, Calendar.MAY, 25, 14, 0),
                endDateTime = createDate(2025, Calendar.MAY, 25, 16, 0)
            )
        )
        items(classes) { classItem ->
            ClassCard(classItem = classItem)
        }
    }
}

@Composable
fun ClassCard(classItem: ClassSession, modifier: Modifier = Modifier) {
    // Mock professor names based on teacherId
    val professorName = when (classItem.teacherId) {
        "teacher1" -> "Prof. Smith"
        "teacher2" -> "Prof. Jones"
        "teacher3" -> "Prof. Brown"
        else -> "Unknown"
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "${classItem.name} - ${classItem.unitCode}",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Professor: $professorName",
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Time: ${
                    SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault()).format(classItem.startDateTime)
                } - ${
                    SimpleDateFormat("HH:mm", Locale.getDefault()).format(classItem.endDateTime)
                }",
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Location: ${classItem.location}",
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Recurrence: ${classItem.recurrence.name.lowercase().replaceFirstChar { it.titlecase() }}",
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

// Helper to create specific dates
private fun createDate(year: Int, month: Int, day: Int, hour: Int, minute: Int): Date {
    val calendar = Calendar.getInstance()
    calendar.set(year, month, day, hour, minute, 0)
    calendar.set(Calendar.MILLISECOND, 0)
    return calendar.time
}