package com.example.imhere.pages.class_detail

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.imhere.model.AttendanceStatus
import com.example.imhere.model.StudentAttendance
import com.example.imhere.ui.components.PageHeader

@Composable
fun TeacherClassDetailPage(
    navController: NavHostController,
    classSessionId: String = "",
    className: String,
    startTime: String,
    endTime: String,
    attendances: List<StudentAttendance> = emptyList(),
    viewModel: TeacherClassDetailViewModel = hiltViewModel(),
) {
    val studentAttendances by viewModel.getStudentAttendances(classSessionId).collectAsState(
        attendances
    )
    val isSaving by viewModel.isSaving.collectAsState()

    val context = LocalContext.current

    fun saveAttendances() {
        viewModel.saveAttendances(classSessionId) {
            Toast.makeText(context, "Successfully Saved Attendances", Toast.LENGTH_SHORT).show()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                16.dp
//                start = 20.dp,
//                end = 20.dp,
//                top = paddingValues.calculateTopPadding() + 30.dp,
//                bottom = paddingValues.calculateBottomPadding() + 30.dp
            )
    )
    {

        // --- Class Info Header ---
        PageHeader(navController, title = "Class Detail")
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

        Button(onClick = {
            saveAttendances()
        }, modifier = Modifier.fillMaxWidth(), enabled = !isSaving) {
            Text(if (isSaving) "Saving..." else "Save Attendances")
        }

        // --- List of Students and Statuses ---
        LazyColumn {
            items(studentAttendances) { studentAttendance ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = studentAttendance.studentName,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = studentAttendance.status.value.replaceFirstChar { it.uppercase() },
                        color = when (studentAttendance.status.value.lowercase()) {
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

@Preview(showBackground = true)
@Composable
fun PreviewTeacherClassDetailPage() {
    val sampleStudents = listOf(
        StudentAttendance("Alice Johnson", status = AttendanceStatus.PRESENT),
        StudentAttendance("Bob Smith", status = AttendanceStatus.LATE),
        StudentAttendance("Charlie Davis", status = AttendanceStatus.ABSENT),
        StudentAttendance("Diana Wong", status = AttendanceStatus.PRESENT)
    )

    TeacherClassDetailPage(
        navController = rememberNavController(),
        className = "FIT5046 - Mobile Computing",
        startTime = "09:00 AM",
        endTime = "10:00 AM",
        attendances = sampleStudents
    )
}
