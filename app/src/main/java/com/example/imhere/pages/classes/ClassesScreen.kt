package com.example.imhere.pages.classes

import androidx.compose.foundation.clickable
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.imhere.model.ClassSession
import com.example.imhere.model.ClassSessionRecurrence
import com.example.imhere.model.UserProfileType
import com.example.imhere.ui.components.PageHeader
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ClassesScreen(
    navController: NavHostController,
    viewModel:ClassesViewModel = hiltViewModel()
) {
    val classes = viewModel.classSessions
    val isLoading = viewModel.isLoading

    if (isLoading) {
        CircularProgressIndicator()
    } else {
        // Render LazyColumn with classes
    }

    val classInstances = listOf(
        ClassSession(
            id = "0",
            name = "Physics 202",
            location = "Lab B-204",
            unitCode = "PHY202",
            teacherId = "teacher1",
            recurrence = ClassSessionRecurrence.ONCE,
            startDateTime = createDate(2025, Calendar.MAY, 27, 14, 0),
            endDateTime = createDate(2025, Calendar.MAY, 27, 16, 0)
        ),
        ClassSession(
            id = "2_wk0",
            name = "Mathematics 101",
            location = "Room A-101",
            unitCode = "MAT101",
            teacherId = "teacher3",
            recurrence = ClassSessionRecurrence.WEEKLY,
            startDateTime = createDate(2025, Calendar.MAY, 28, 12, 0),
            endDateTime = createDate(2025, Calendar.MAY, 28, 14, 0)
        ),
        ClassSession(
            id = "3",
            name = "Computer Science 101",
            location = "Room D-201",
            unitCode = "CS101",
            teacherId = "teacher4",
            recurrence = ClassSessionRecurrence.MONTHLY,
            startDateTime = createDate(2025, Calendar.MAY, 30, 10, 0),
            endDateTime = createDate(2025, Calendar.MAY, 30, 12, 0)
        ),
        ClassSession(
            id = "2_wk1",
            name = "Mathematics 101",
            location = "Room A-101",
            unitCode = "MAT101",
            teacherId = "teacher3",
            recurrence = ClassSessionRecurrence.WEEKLY,
            startDateTime = createDate(2025, Calendar.JULY, 4, 12, 0),
            endDateTime = createDate(2025, Calendar.JULY, 4, 14, 0)
        ),
        ClassSession(
            id = "1",
            name = "History 101",
            location = "Room F-101",
            unitCode = "HIS101",
            teacherId = "teacher2",
            recurrence = ClassSessionRecurrence.ONCE,
            startDateTime = createDate(2025, Calendar.JULY, 5, 12, 0),
            endDateTime = createDate(2025, Calendar.JULY, 5, 14, 0)
        ),
        ClassSession(
            id = "4_bi1",
            name = "Chemistry 301",
            location = "Lab C-105",
            unitCode = "CHE301",
            teacherId = "teacher5",
            recurrence = ClassSessionRecurrence.BIWEEKLY,
            startDateTime = createDate(2025, Calendar.JULY, 6, 14, 0),
            endDateTime = createDate(2025, Calendar.JULY, 6, 16, 0)
        )
    ).sortedBy { it.startDateTime }

    val calendarNow = Calendar.getInstance()
    val calendarToday = calendarNow.clone() as Calendar
    val calendarTomorrow = (calendarNow.clone() as Calendar).apply { add(Calendar.DAY_OF_YEAR, 1) }

    val endOfWeek = (calendarNow.clone() as Calendar).apply {
        set(Calendar.DAY_OF_WEEK, firstDayOfWeek)
        add(Calendar.DAY_OF_WEEK, 7)
    }

    val endOfNextWeek = (calendarNow.clone() as Calendar).apply {
        set(Calendar.DAY_OF_WEEK, firstDayOfWeek)
        add(Calendar.DAY_OF_WEEK, 14)
    }

    val grouped = classes.groupBy {
        val classCal = Calendar.getInstance().apply { time = it.startDateTime }
        when {
            isSameDay(classCal, calendarToday) -> "Today"
            isSameDay(classCal, calendarTomorrow) -> "Tomorrow"
            classCal.after(calendarTomorrow) && classCal.before(endOfWeek) -> "This Week"
            classCal.after(endOfWeek) && classCal.before(endOfNextWeek) -> "Next Week"
            else -> "Later"
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Your Classes",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                if(
                    viewModel.profile?.type == UserProfileType.TEACHER
                ){
                    Button(onClick = {
                        navController.navigate("createClass")
                    }) {
                        Text("+ Create")
                    }
                }

            }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
            grouped.forEach { (header, classes) ->
                item {
                    Text(
                        text = header,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(vertical = 12.dp)
                    )
                }
                items(classes) { classItem ->
                    ClassCard(
                        classItem = classItem,
                        onClick = {
                            navController.navigate("classes/${classItem.id}")
                        }
                        )
                }
            }

            if (classInstances.isEmpty()) {
                item {
                    Text(
                        text = "No upcoming classes",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 16.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun ClassCard(
    classItem: ClassSession,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit) = {}
) {
    val professorName = when (classItem.teacherId) {
        "teacher1" -> "Venessa Fring"
        "teacher2" -> "Michael Carter"
        "teacher3" -> "Samantha Lee"
        "teacher4" -> "Robert Hayes"
        "teacher5" -> "Emily Watson"
        else -> "Unknown"
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable {
                if (onClick != null) {
                    onClick()
                }
            }
        ,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "${classItem.name}",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "${classItem.unitCode}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Text(
                text = "Prof $professorName",
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = SimpleDateFormat(
                    "MMM dd, yyyy",
                    Locale.getDefault()
                ).format(classItem.startDateTime),
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "${
                        SimpleDateFormat("HH:mm", Locale.getDefault()).format(classItem.startDateTime)
                    } - ${
                        SimpleDateFormat("HH:mm", Locale.getDefault()).format(classItem.endDateTime)
                    }",
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = classItem.location,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

private fun createDate(year: Int, month: Int, day: Int, hour: Int, minute: Int): Date {
    val calendar = Calendar.getInstance()
    calendar.set(year, month, day, hour, minute, 0)
    calendar.set(Calendar.MILLISECOND, 0)
    return calendar.time
}

private fun isSameDay(c1: Calendar, c2: Calendar): Boolean {
    return c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR) &&
            c1.get(Calendar.DAY_OF_YEAR) == c2.get(Calendar.DAY_OF_YEAR)
}
