package com.example.imhere.pages.class_detail

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.imhere.navItems
import com.example.imhere.ui.theme.Blue1
import java.time.*
import java.time.format.DateTimeFormatter
import android.app.Activity
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.LocalContext
import com.google.zxing.integration.android.IntentIntegrator
import com.journeyapps.barcodescanner.CaptureActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

enum class Recurrence {
    NONE, DAILY, WEEKLY, MONTHLY
}

data class ClassData(
    val id: String,
    val name: String,
    val startDateTime: LocalDateTime,
    val endDateTime: LocalDateTime,
    val recurrence: Recurrence,
    val location: String
)



@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun StudentClassDetailPage(
    classInfo: ClassData,
    attendanceStatus: String?,
    onScanClick: () -> Unit
) {
    val now = remember { mutableStateOf(LocalDateTime.now()) }

    LaunchedEffect(Unit) {
        while (true) {
            now.value = LocalDateTime.now()
            kotlinx.coroutines.delay(1000)
        }
    }
    val scanWindowStart = classInfo.startDateTime.minusMinutes(10)
    val scanWindowEnd = classInfo.endDateTime
    val canScan = now.value.isAfter(scanWindowStart) && now.value.isBefore(scanWindowEnd)

    val navController = rememberNavController()
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
                .padding(paddingValues)
                .padding(horizontal = 20.dp, vertical = 30.dp),
        ) {

            // --- Header ---
            Text(
                text = "Class Detail",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(30.dp))

            Text(
                text = "Class Name:",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium,
                color = Color.DarkGray
            )
            // --- Class Name ---
            Text(
                text = classInfo.name,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(30.dp))
            // --- Location ---
            Text(
                text = "Location:",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium,
                color = Color.DarkGray

            )
            Text(
                text = classInfo.location,
                style = MaterialTheme.typography.titleMedium.copy(fontSize = 18.sp)
            )
            Spacer(modifier = Modifier.height(30.dp))
            // --- Time Row ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Start Time:",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium,
                        color = Color.DarkGray

                    )
                    Text(
                        text = classInfo.startDateTime.format(DateTimeFormatter.ofPattern("hh:mm a")),
                        style = MaterialTheme.typography.titleMedium.copy(fontSize = 18.sp)
                    )
                }
                Column {
                    Text(
                        text = "End Time:",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium,
                        color = Color.DarkGray

                    )
                    Text(
                        text = classInfo.endDateTime.format(DateTimeFormatter.ofPattern("hh:mm a")),
                        style = MaterialTheme.typography.titleMedium.copy(fontSize = 18.sp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(30.dp))
            // --- Recurrence ---
            Text(
                text = "Recurrence:",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium,
                color = Color.DarkGray

            )
            Text(
                text = classInfo.recurrence.name.lowercase().replaceFirstChar { it.titlecase() },
                style = MaterialTheme.typography.titleMedium.copy(fontSize = 18.sp)
            )
            Spacer(modifier = Modifier.height(30.dp))
            // --- Attendance Info ---
            Text(
                text = "Attendance Status:",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium,
                color = Color.DarkGray

            )
            Text(
                text = attendanceStatus ?: "Not submitted",
                style = MaterialTheme.typography.titleMedium.copy(fontSize = 18.sp),
                color = when (attendanceStatus?.lowercase()) {
                    "present" -> Color(0xFF2E7D32)
                    "late" -> Color(0xFFFF9800)
                    "absent" -> Color.Red
                    else -> Color.Gray
                }
            )
            Spacer(modifier = Modifier.height(50.dp))

            // --- QR Scan Section with Countdown + ZXing Scanner ---
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val context = LocalContext.current
                val activity = remember(context) { context as Activity }

                var scannedResult by remember { mutableStateOf<String?>(null) }

                var attendanceStatus by remember { mutableStateOf<String?>(null) }

                val qrLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.StartActivityForResult()
                ) { result ->
                    if (result.resultCode == Activity.RESULT_OK) {
                        val contents = result.data?.getStringExtra("SCAN_RESULT")
                        contents?.let { scannedClassId ->
                            markAttendance(
                                classId = scannedClassId,
                                onSuccess = {
                                    scannedResult = scannedClassId
                                    attendanceStatus = "Present"
                                },
                                onError = { errorMsg ->
                                    Toast.makeText(context, "Failed: $errorMsg", Toast.LENGTH_LONG).show()
                                }
                            )
                        }
                    }
                }


                val canScan = now.value.isAfter(scanWindowStart) && now.value.isBefore(scanWindowEnd)
                val isTooEarly = now.value.isBefore(scanWindowStart)
                val isTooLate = now.value.isAfter(scanWindowEnd)

                val remainingTimeText = when {
                    now.value.isBefore(scanWindowStart) -> {
                        val duration = Duration.between(now.value, scanWindowStart)
                        val totalSeconds = duration.seconds
                        val minutes = totalSeconds / 60
                        val seconds = totalSeconds % 60
                        "Scan opens in: ${minutes} min ${seconds} sec"
                    }
                    canScan -> {
                        val duration = Duration.between(now.value, scanWindowEnd)
                        val totalSeconds = duration.seconds
                        val minutes = totalSeconds / 60
                        val seconds = totalSeconds % 60
                        "Scan closes in: ${minutes} min ${seconds} sec"
                    }
                    else -> null
                }

                // --- Scan Button ---
                Button(
                    onClick = {
                        val integrator = IntentIntegrator(activity).apply {
                            setOrientationLocked(false)
                            setPrompt("Scan QR Code")
                            setBeepEnabled(true)
                            setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
                            captureActivity = CaptureActivity::class.java
                        }
                        qrLauncher.launch(integrator.createScanIntent())
                    },
                    enabled = canScan && attendanceStatus == null,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (canScan) MaterialTheme.colorScheme.primary else Color.LightGray
                    )
                ) {
                    Text("Scan QR Code")
                }

                Spacer(modifier = Modifier.height(8.dp))

                // --- Status Text ---
                when {
                    attendanceStatus != null -> Text(
                        "Attendance submitted: ✅ $attendanceStatus",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF2E7D32)
                    )
                    remainingTimeText != null -> Text(
                        remainingTimeText,
                        style = MaterialTheme.typography.bodySmall,
                        color = if (canScan) Color.DarkGray else Color.Gray
                    )
                    isTooLate -> Text(
                        "The scan window has expired.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Red
                    )
                    else -> Text(
                        "You can scan 10 minutes before the class.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }

                // --- Debug Output ---
                scannedResult?.let {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Scanned QR Code: $it", color = Color.Blue)
                }
            }



        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun markAttendance(classId: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
    val user = FirebaseAuth.getInstance().currentUser
    val db = FirebaseFirestore.getInstance()

    if (user != null) {
        val userId = user.uid
        val today = LocalDate.now().format(DateTimeFormatter.ISO_DATE)

        val attendance = hashMapOf(
            "userId" to userId,
            "classId" to classId,
            "date" to today,
            "status" to "present"
        )

        db.collection("attendances")
            .add(attendance)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onError(e.localizedMessage ?: "Error saving attendance") }
    } else {
        onError("User not logged in.")
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun PreviewStudentClassDetailPage() {
    val futureStartTime = LocalDateTime.now().minusMinutes(30) // test
    val futureEndTime = futureStartTime.plusHours(1)

    val sampleClass = ClassData(
        id = "class001",
        name = "Mathematics 101",
        startDateTime = futureStartTime,
        endDateTime = futureEndTime,
        recurrence = Recurrence.WEEKLY,
        location = "Room A-101"
    )

    StudentClassDetailPage(
        classInfo = sampleClass,
        attendanceStatus = null,
        onScanClick = { println("Scan clicked!") }
    )
}
