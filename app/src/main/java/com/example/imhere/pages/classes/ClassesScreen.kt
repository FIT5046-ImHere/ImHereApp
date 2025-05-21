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
                text = "Schedules",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 16.dp, bottom = 16.dp)
            )
            /*
            Text(
            "Your Report",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(bottom = 8.dp)
            )
            */
        }
        item {
            Text(
                text = "Your Classes:",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }
        val classes = listOf(
            ClassSession(
                id = "1",
                name = "Mathematics 101",
                location = "Room A-101",
                unitCode = "MAT101",
                teacherId = "teacher1",
                recurrence = ClassSessionRecurrence.WEEKLY,
                startDateTime = getFutureDate(hours = 2),
                endDateTime = getFutureDate(hours = 3)
            ),
            ClassSession(
                id = "3",
                name = "Chemistry 301",
                location = "Lab C-105",
                unitCode = "CHE301",
                teacherId = "teacher2",
                recurrence = ClassSessionRecurrence.ONCE,
                startDateTime = getFutureDate(days = 2, hours = 4),
                endDateTime = getFutureDate(days = 2, hours = 5)
            ),
            ClassSession(
                id = "4",
                name = "Computer Science 101",
                location = "Room D-201",
                unitCode = "CS101",
                teacherId = "teacher2",
                recurrence = ClassSessionRecurrence.WEEKLY,
                startDateTime = getFutureDate(days = 3, hours = 2),
                endDateTime = getFutureDate(days = 3, hours = 3)
            ),
            ClassSession(
                id = "5",
                name = "Biology 201",
                location = "Lab E-302",
                unitCode = "BIO201",
                teacherId = "teacher3",
                recurrence = ClassSessionRecurrence.BIWEEKLY,
                startDateTime = getFutureDate(days = 4, hours = 1),
                endDateTime = getFutureDate(days = 4, hours = 2)
            ),
            ClassSession(
                id = "6",
                name = "History 101",
                location = "Room F-101",
                unitCode = "HIS101",
                teacherId = "teacher3",
                recurrence = ClassSessionRecurrence.MONTHLY,
                startDateTime = getFutureDate(days = 5, hours = 3),
                endDateTime = getFutureDate(days = 5, hours = 4)
            ),
            ClassSession(
                id = "7",
                name = "Literature 202",
                location = "Room G-204",
                unitCode = "LIT202",
                teacherId = "teacher4",
                recurrence = ClassSessionRecurrence.WEEKLY,
                startDateTime = getFutureDate(days = 6, hours = 2),
                endDateTime = getFutureDate(days = 6, hours = 3)
            )
        )
        items(classes) { classItem ->
            ClassCard(classItem = classItem)
        }
    }
}

@Composable
fun ClassCard(classItem: ClassSession, modifier: Modifier = Modifier) {
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
                text = classItem.name,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
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

// Helper to create future dates
private fun getFutureDate(days: Int = 0, hours: Int = 0): Date {
    val calendar = Calendar.getInstance()
    calendar.add(Calendar.DAY_OF_MONTH, days)
    calendar.add(Calendar.HOUR_OF_DAY, hours)
    return calendar.time
}