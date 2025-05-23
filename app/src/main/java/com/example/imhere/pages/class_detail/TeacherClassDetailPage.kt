package com.example.imhere.pages.class_detail

import QrCode
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
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
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TeacherClassDetailPage(
    navController: NavHostController,
    classSessionId: String = "",
    attendances: List<StudentAttendance> = emptyList(),
    viewModel: TeacherClassDetailViewModel = hiltViewModel(),
) {
//    Collecting class session
    val isLoading by viewModel.isLoading.collectAsState()
    val classSession by viewModel.classSession.collectAsState()
    var password by remember { mutableStateOf<String?>(classSession?.attendancePassword) }
    var isTakingAttendance by remember { mutableStateOf(classSession?.attendancePassword != null) }

    LaunchedEffect(classSession) {
        if (classSession != null) {
            password = classSession!!.attendancePassword
            isTakingAttendance = classSession!!.attendancePassword != null
        }
    }

    val startDate: LocalDate? = classSession?.startDateTime?.toInstant()
        ?.atZone(ZoneId.systemDefault())
        ?.toLocalDate()

    val endDate: LocalDate? = classSession?.endDateTime?.toInstant()
        ?.atZone(ZoneId.systemDefault())
        ?.toLocalDate()

    val startTime: LocalTime? = classSession?.startDateTime?.toInstant()
        ?.atZone(ZoneId.systemDefault())
        ?.toLocalTime()

    val endTime: LocalTime? = classSession?.endDateTime?.toInstant()
        ?.atZone(ZoneId.systemDefault())
        ?.toLocalTime()

    LaunchedEffect(Unit) {
        viewModel.loadClassSession(classSessionId)
    }

    val studentAttendances by viewModel.getStudentAttendances(classSessionId).collectAsState(
        attendances
    )
    val isSaving by viewModel.isSaving.collectAsState()

    val context = LocalContext.current

    fun saveAttendances() {
        viewModel.saveAttendances(classSessionId) {
            isTakingAttendance = false
            password = null
            Toast.makeText(context, "Successfully Saved Attendances", Toast.LENGTH_SHORT).show()
        }
    }

    fun startTakingAttendances() {
        viewModel.startTakingAttendances(classSessionId) { pw ->
            isTakingAttendance = true
            password = pw
            Toast.makeText(context, "Please show the QR code to the students", Toast.LENGTH_LONG).show()
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
        if (classSession != null) {

            // --- Class Info Header ---
            PageHeader(navController, title = "Class Detail") {
                Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Class Detail",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Button(onClick = {
                        navController.navigate("enrollments/$classSessionId")
                    }) {
                        Text("Enroll Students")
                    }
                }
            }
            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Class Name:",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium,
                color = Color.DarkGray
            )

            // --- Class Name ---
            Text(
                text = classSession!!.name,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(15.dp))
            Text(
                text = "Class Time: ${startTime?.format(DateTimeFormatter.ofPattern("h:mm a"))} - ${endTime?.format(DateTimeFormatter.ofPattern("h:mm a "))}",
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

            if (!isTakingAttendance) {
                Button(onClick = {
                    startTakingAttendances()
                }, modifier = Modifier.fillMaxWidth(), enabled = !isSaving) {
                    Text("Start Taking Attendances")
                }
            } else {
                Button(onClick = {
                    saveAttendances()
                }, modifier = Modifier.fillMaxWidth(), enabled = !isSaving) {
                    Text(if (isSaving) "Saving..." else "Save Attendances")
                }
            }

            if (password != null) {
                Spacer(Modifier.height(16.dp))
                Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "Show this QR code to the students")
                    QrCode(data = password!!)
                }
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
}

@RequiresApi(Build.VERSION_CODES.O)
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
        classSessionId = "",
    )
}
