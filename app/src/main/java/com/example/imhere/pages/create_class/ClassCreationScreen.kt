package com.example.imhere.pages.create_class

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.util.Log
import android.widget.DatePicker
import android.widget.TimePicker
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ClassDetailsForm(
    modifier: Modifier = Modifier,
    viewModel: ClassCreationViewModel = hiltViewModel()
) {
    val classSessions by viewModel.classSessions.collectAsState(emptyList())

    LaunchedEffect(classSessions) {
        Log.d("ClassScreen", "Fetched sessions: $classSessions")
    }

    val context = LocalContext.current
    val navController = rememberNavController()

    var className by remember { mutableStateOf("") }
    var unitCode by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var recurrence by remember { mutableStateOf("") }

    var fromDate by remember { mutableStateOf("") }
    var toDate by remember { mutableStateOf("") }
    var startTime by remember { mutableStateOf("") }
    var endTime by remember { mutableStateOf("") }

    val calendar = Calendar.getInstance()
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)

    val fromDatePickerDialog = DatePickerDialog(context, { _: DatePicker, y: Int, m: Int, d: Int ->
        fromDate = "$d/${m + 1}/$y"
    }, year, month, day)

    val toDatePickerDialog = DatePickerDialog(context, { _: DatePicker, y: Int, m: Int, d: Int ->
        toDate = "$d/${m + 1}/$y"
    }, year, month, day)

    val startTimePickerDialog = TimePickerDialog(context, { _: TimePicker, hour: Int, minute: Int ->
        startTime = String.format("%02d:%02d", hour, minute)
    }, 0, 0, true)

    val endTimePickerDialog = TimePickerDialog(context, { _: TimePicker, hour: Int, minute: Int ->
        endTime = String.format("%02d:%02d", hour, minute)
    }, 0, 0, true)

    fun onSubmit() {
        if (className.isBlank() || unitCode.isBlank() || location.isBlank() ||
            recurrence.isBlank() || fromDate.isBlank() || toDate.isBlank() ||
            startTime.isBlank() || endTime.isBlank()
        ) {
            Toast.makeText(context, "Please fill out all fields", Toast.LENGTH_SHORT).show()
            return
        }

        val sDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(fromDate)
        val eDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(toDate)

        if (sDate != null && eDate != null) {
            viewModel.createClass(
                name = className,
                unitCode = unitCode,
                location = location,
                recurrence = recurrence,
                startDate = sDate,
                endDate = eDate,
                startTime = startTime,
                endTime = endTime,
                onSuccess = {
                    Toast.makeText(context, "Class created successfully", Toast.LENGTH_SHORT).show()
                    navController.popBackStack()
                },
                onError = {
                    Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                }
            )
        }
    }

    Column(modifier = modifier.padding(16.dp)) {
        Text("Create a Class", style = MaterialTheme.typography.headlineLarge)
        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = className,
            onValueChange = { className = it },
            label = { Text("Class Name*") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))
        OutlinedTextField(
            value = unitCode,
            onValueChange = { unitCode = it },
            label = { Text("Unit Code*") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))
        OutlinedTextField(
            value = location,
            onValueChange = { location = it },
            label = { Text("Location*") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))
        OutlinedTextField(
            value = recurrence,
            onValueChange = { recurrence = it },
            label = { Text("Recurrence*") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))
        Row(Modifier.fillMaxWidth()) {
            Box(modifier = Modifier.weight(1f)) {
                OutlinedTextField(
                    value = fromDate,
                    onValueChange = {},
                    label = { Text("Start Date*") },
                    readOnly = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Box(Modifier.matchParentSize().clickable { fromDatePickerDialog.show() })
            }
            Spacer(modifier = Modifier.width(10.dp))
            Box(modifier = Modifier.weight(1f)) {
                OutlinedTextField(
                    value = toDate,
                    onValueChange = {},
                    label = { Text("End Date*") },
                    readOnly = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Box(Modifier.matchParentSize().clickable { toDatePickerDialog.show() })
            }
        }

        Spacer(modifier = Modifier.height(10.dp))
        Row(Modifier.fillMaxWidth()) {
            Box(modifier = Modifier.weight(1f)) {
                OutlinedTextField(
                    value = startTime,
                    onValueChange = {},
                    label = { Text("Start Time*") },
                    readOnly = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Box(Modifier.matchParentSize().clickable { startTimePickerDialog.show() })
            }
            Spacer(modifier = Modifier.width(10.dp))
            Box(modifier = Modifier.weight(1f)) {
                OutlinedTextField(
                    value = endTime,
                    onValueChange = {},
                    label = { Text("End Time*") },
                    readOnly = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Box(Modifier.matchParentSize().clickable { endTimePickerDialog.show() })
            }
        }

        Spacer(modifier = Modifier.height(15.dp))
        Button(
            onClick = { onSubmit() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Submit")
        }
    }
}