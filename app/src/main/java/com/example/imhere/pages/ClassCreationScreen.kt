package com.example.imhere.pages

// class creation code from here on

import android.R
import android.R.attr.enabled
import android.R.attr.required
import android.R.attr.type
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import android.widget.DatePicker
import android.widget.TimePicker
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog

import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults

import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TimeInput
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.pointer.motionEventSpy
import androidx.compose.ui.layout.ModifierLocalBeyondBoundsLayout
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.math.exp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClassDetailsForm( modifier: Modifier = Modifier ) {
    // a function with the ui elements for the class inputs
    // creating a column layout

    // variables for date picker
    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)

    var fromDate by remember { mutableStateOf("") }
    var toDate by remember { mutableStateOf("") }

    val fromDatePickerDialog = DatePickerDialog(context, { _: DatePicker, y: Int, m: Int, d: Int ->
        fromDate = "$d/${m + 1}/$y"
    }, year, month, day)

    val toDatePickerDialog = DatePickerDialog(context, { _: DatePicker, y: Int, m: Int, d: Int ->
        toDate = "$d/${m + 1}/$y"
    }, year, month, day)

    //variables for time picker
    var startTime by remember { mutableStateOf("") }
    var endTime by remember { mutableStateOf("") }

    val currentTime = Calendar.getInstance()

    val timePickerState = rememberTimePickerState(
        initialHour = currentTime.get(Calendar.HOUR_OF_DAY),
        initialMinute = currentTime.get(Calendar.MINUTE),
        is24Hour = true,
    )

    val startTimePickerDialog = TimePickerDialog(context, { _: TimePicker, hourOfDay: Int, minute: Int ->
        startTime = "$hourOfDay:$minute"
    }, 0, 0, false)

    val endTimePickerDialog = TimePickerDialog(context, { _: TimePicker, hourOfDay: Int, minute: Int ->
        endTime = "$hourOfDay:$minute"
    }, 0, 0, false)


    Column(modifier.padding(16.dp)) {
//        Column(
//            modifier = Modifier
//                // add padding
//                .fillMaxWidth(1f)
//                .height(150.dp)
//                .background(color = MaterialTheme.colorScheme.primary)
//        ) {
//
//
//        }
        Text(
            text = "Create a Class",
            style = MaterialTheme.typography.headlineLarge,
//            color = Color.White,
//            modifier = Modifier.padding(start= 30.dp, end = 30.dp, top = 30.dp, bottom = 10.dp)
        )
        Column(
            modifier = Modifier
                // add padding
//                .padding(horizontal = 30.dp, vertical = 20.dp)
//                .fillMaxWidth(1f)

        ) {
            // headline

            // adding a placeholder text input
            TextFieldInput("Class Name*",)

            // creating vertical space
            Spacer(modifier = Modifier.height(10.dp))

            // Unit Name
            // should this be a drop down?
            // should certain teachers only be allowed certain units?
            DropDown(unitsDropdown.dropDownName, unitsDropdown.dropDownOptions)


            Spacer(modifier = Modifier.height(10.dp))

            // location of class
            DropDown(locationDropdown.dropDownName, locationDropdown.dropDownOptions)

            Spacer(modifier = Modifier.height(10.dp))


            Row(
                modifier = Modifier.fillMaxWidth(1f)
            ) { // date picker inputs
                Box(modifier = Modifier.weight(1f)) {
                    OutlinedTextField(
                        value = fromDate,
                        onValueChange = {},
                        label = { Text("Start Date*") },
                        readOnly = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .clickable { fromDatePickerDialog.show() }
                    )
                }

                Spacer(modifier = Modifier.fillMaxWidth(0.05f))

                Box(modifier = Modifier.weight(1f)) {
                    OutlinedTextField(
                        value = toDate,
                        onValueChange = {},
                        label = { Text("End Date*") },
                        readOnly = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .clickable { toDatePickerDialog.show() }
                    )
                }
            }

            Spacer(modifier = Modifier.height(15.dp))


            // timings of class
            Row() {
                Box(modifier = Modifier.weight(1f)) {
                    OutlinedTextField(
                        value = startTime,
                        onValueChange = {},
                        label = { Text("Start Time*") },
                        readOnly = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .clickable { startTimePickerDialog.show() }
                    )
                }

                Spacer(modifier = Modifier.fillMaxWidth(0.05f))


                Box(modifier = Modifier.weight(1f)) {
                    OutlinedTextField(
                        value = endTime,
                        onValueChange = {},
                        label = { Text("End Time*") },
                        readOnly = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .clickable { endTimePickerDialog.show() }
                    )
                }
            }

            Spacer(modifier = Modifier.height(15.dp))

            // teaching period
            DropDown(teachingPeriodDropdown.dropDownName, teachingPeriodDropdown.dropDownOptions)

            Spacer(modifier = Modifier.height((15.dp)))

            Button(
                onClick = {},
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Submit")
            }

        }


    }
}

// dummy data for drop downs
data class DropDownOptions (val dropDownName: String, val dropDownOptions: List<String>)
val locationDropdown = DropDownOptions("Location", listOf("LTB", "Learning Village", "WoodSide"))
val unitsDropdown = DropDownOptions("Unit*", listOf("FIT5147", "FIT5225", "FIT5046", "FIT9132"))
val teachingPeriodDropdown = DropDownOptions("Recurrence*", listOf("Daily", "Weekly", "Monthly"))


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropDown(labelName: String, availableOptions: List<String>) {
    var expanded by remember { mutableStateOf(false) }
    var selectedOptionText by remember { mutableStateOf("") }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange =  {
            expanded = !expanded
        }
    ) {
        OutlinedTextField(
            readOnly = true,
            value = selectedOptionText,
            onValueChange = { },
            label = { Text(labelName) },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(
                    expanded = expanded
                )
            },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth(1f)
            // the modifier allows the the control of the drop dowm
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = {
                expanded = false
            },
            modifier = Modifier.exposedDropdownSize(true)
        ) {
            availableOptions.forEach { selectionOption ->
                DropdownMenuItem(
                    text = {
                        Text(
                            selectionOption,
                            color = Color.Black
                        )},
                    onClick = {
                        selectedOptionText = selectionOption
                        expanded = false
                    }
                )
            }
        }
    }
}


// input for text fields in the form
@Composable
fun TextFieldInput(labelName: String) {
    var text by remember { mutableStateOf("") }

    OutlinedTextField(
        value = text,
        onValueChange = { text = it },
        label = { Text(labelName)},
        maxLines = 1,
        textStyle = TextStyle(color = MaterialTheme.colorScheme.secondary),
        modifier = Modifier.fillMaxWidth(1f)
    )
}
