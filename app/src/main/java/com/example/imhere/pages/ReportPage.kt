package com.example.imhere.pages

import android.app.DatePickerDialog
import android.widget.DatePicker
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import java.util.*

@Composable
fun ReportPage(modifier: Modifier = Modifier) {
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

    Column(modifier = modifier.padding(16.dp)) {
        Text(text = "Select Report Date Range", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(16.dp))

        Box(modifier = Modifier
            .fillMaxWidth()
            .clickable { fromDatePickerDialog.show() }) {
            OutlinedTextField(
                value = fromDate,
                onValueChange = {},
                label = { Text("From") },
                readOnly = true,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Box(modifier = Modifier
            .fillMaxWidth()
            .clickable { toDatePickerDialog.show() }) {
            OutlinedTextField(
                value = toDate,
                onValueChange = {},
                label = { Text("To") },
                readOnly = true,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
