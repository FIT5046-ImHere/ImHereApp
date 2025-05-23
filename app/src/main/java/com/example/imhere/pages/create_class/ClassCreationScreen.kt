package com.example.imhere.pages.create_class

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.util.Log
import android.widget.DatePicker
import android.widget.TimePicker
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.imhere.model.ClassSessionRecurrence
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClassDetailsForm(
    modifier: Modifier = Modifier,
    viewModel: ClassCreationViewModel = hiltViewModel(),
    navController: NavHostController = rememberNavController()
) {
    val classSessions by viewModel.classSessions.collectAsState(emptyList())

    LaunchedEffect(classSessions) {
        Log.d("ClassScreen", "Fetched sessions: $classSessions")
    }

    val context = LocalContext.current

    var className by remember { mutableStateOf("") }
    var unitCode by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var selectedRecurrence by remember { mutableStateOf(ClassSessionRecurrence.ONCE) }
    var recurrenceExpanded by remember { mutableStateOf(false) }

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
            fromDate.isBlank() || toDate.isBlank() ||
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
                recurrence = selectedRecurrence.name.uppercase(),
                startDate = sDate,
                endDate = eDate,
                startTime = startTime,
                endTime = endTime,
                onSuccess = { classSession ->
                    Toast.makeText(context, "Class created successfully", Toast.LENGTH_SHORT).show()
                    navController.navigate("enrollments/${classSession?.id}")
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
        ExposedDropdownMenuBox(
            expanded = recurrenceExpanded,
            onExpandedChange = { recurrenceExpanded = !recurrenceExpanded }
        ) {
            OutlinedTextField(
                readOnly = true,
                value = selectedRecurrence.name.lowercase().replaceFirstChar { it.uppercase() },
                onValueChange = {},
                label = { Text("Recurrence*") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = recurrenceExpanded) },
                modifier = Modifier.menuAnchor().fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = recurrenceExpanded,
                onDismissRequest = { recurrenceExpanded = false }
            ) {
                ClassSessionRecurrence.entries.forEach { option ->
                    DropdownMenuItem(
                        text = {
                            Text(option.name.lowercase().replaceFirstChar { it.uppercase() })
                        },
                        onClick = {
                            selectedRecurrence = option
                            recurrenceExpanded = false
                        }
                    )
                }
            }
        }

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
