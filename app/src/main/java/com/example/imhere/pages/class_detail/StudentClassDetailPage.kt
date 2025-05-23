package com.example.imhere.pages.class_detail

import android.app.Activity
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.imhere.model.AttendanceStatus
import com.example.imhere.ui.components.PageHeader
import com.google.zxing.integration.android.IntentIntegrator
import kotlinx.coroutines.delay
import java.time.*
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun StudentClassDetailPage(
    viewModel: StudentClassDetailViewModel = hiltViewModel(),
    classSessionId: String,
    navController: NavHostController
) {
    val now = remember { mutableStateOf(LocalDateTime.now()) }

    val classSession by viewModel.classSession.collectAsState()
    val attendanceStatus by viewModel.attendanceStatus.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadClassSessionAndAttendance(classSessionId)
    }

    LaunchedEffect(Unit) {
        while (true) {
            now.value = LocalDateTime.now()
            delay(1000)
        }
    }

    if (isLoading || classSession == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    val zoneId = ZoneId.systemDefault()
    val startDateTime = classSession!!.startDateTime.toInstant().atZone(zoneId).toLocalDateTime()
    val endDateTime = classSession!!.endDateTime.toInstant().atZone(zoneId).toLocalDateTime()

    val context = LocalContext.current
    val activity = remember(context) { context as Activity }
    var scannedResult by remember { mutableStateOf<String?>(null) }

    val qrLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val contents = result.data?.getStringExtra("SCAN_RESULT")
            contents?.let { scannedClassId ->
                viewModel.markAttendance(
                    classSessionId = classSessionId,
                    password = scannedClassId,
                    onSuccess = {
                        scannedResult = scannedClassId
                        Toast.makeText(context, "Attendance marked successfully!", Toast.LENGTH_SHORT).show()
                    },
                    onError = { errorMsg ->
                        Toast.makeText(context, "Failed: $errorMsg", Toast.LENGTH_LONG).show()
                    }
                )
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        PageHeader(navController = navController, title = "Class Detail")
        Spacer(modifier = Modifier.height(30.dp))

        Text("Class Name:", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Medium, color = Color.DarkGray)
        Text(classSession!!.name, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(20.dp))

        Text("Location:", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Medium, color = Color.DarkGray)
        Text(classSession!!.location, style = MaterialTheme.typography.titleMedium.copy(fontSize = 18.sp))
        Spacer(modifier = Modifier.height(20.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Column {
                Text("Start Time:", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Medium, color = Color.DarkGray)
                Text(startDateTime.format(DateTimeFormatter.ofPattern("hh:mm a")), style = MaterialTheme.typography.titleMedium.copy(fontSize = 18.sp))
            }
            Column {
                Text("End Time:", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Medium, color = Color.DarkGray)
                Text(endDateTime.format(DateTimeFormatter.ofPattern("hh:mm a")), style = MaterialTheme.typography.titleMedium.copy(fontSize = 18.sp))
            }
        }
        Spacer(modifier = Modifier.height(20.dp))

        Text("Recurrence:", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Medium, color = Color.DarkGray)
        Text(classSession!!.recurrence.name.lowercase().replaceFirstChar { it.titlecase() }, style = MaterialTheme.typography.titleMedium.copy(fontSize = 18.sp))
        Spacer(modifier = Modifier.height(20.dp))

        Text("Attendance Status:", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Medium, color = Color.DarkGray)
        Text(
            text = attendanceStatus?.name?.lowercase()?.replaceFirstChar { it.titlecase() } ?: "Not submitted",
            color = when (attendanceStatus) {
                AttendanceStatus.PRESENT -> Color(0xFF2E7D32)
                AttendanceStatus.LATE -> Color(0xFFFF9800)
                AttendanceStatus.ABSENT -> Color.Red
                else -> Color.Gray
            },
            style = MaterialTheme.typography.titleMedium.copy(fontSize = 18.sp)
        )

        Spacer(modifier = Modifier.height(40.dp))

        Button(
            onClick = {
                val integrator = IntentIntegrator(activity).apply {
                    setOrientationLocked(false)
                    setPrompt("Scan QR Code")
                    setBeepEnabled(true)
                    setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
                    captureActivity = CustomScannerActivity::class.java
                }
                qrLauncher.launch(integrator.createScanIntent())
            },
            enabled = attendanceStatus == null,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Text("Scan QR Code")
        }

        Spacer(modifier = Modifier.height(8.dp))

        when {
            attendanceStatus != null -> Text("Attendance submitted: ✅ ${attendanceStatus!!.name.lowercase().replaceFirstChar { it.titlecase() }}", style = MaterialTheme.typography.bodySmall, color = Color(0xFF2E7D32))
            else -> Text(
                text = "Please scan the QR code",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

        }

        scannedResult?.let {
            Spacer(modifier = Modifier.height(8.dp))
            Text("Scanned QR Code: $it", color = Color.Blue)
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun PreviewStudentClassDetailPage() {
    StudentClassDetailPage(
        classSessionId = "class001",
        navController = rememberNavController()
    )
}
