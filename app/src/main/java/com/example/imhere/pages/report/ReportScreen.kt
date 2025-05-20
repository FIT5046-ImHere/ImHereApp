package com.example.imhere.pages.report

import SimplePieChart
import android.app.DatePickerDialog
import android.widget.DatePicker
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.imhere.pages.login.LoginViewModel
import java.util.*

//@Preview(showBackground = true)
@Composable
fun ReportPage(modifier: Modifier = Modifier, viewModel: ReportViewModel = hiltViewModel(), navController: NavHostController) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)
    val attendances = viewModel.attendances
    var fromDate by remember { mutableStateOf("") }
    var toDate by remember { mutableStateOf("") }

    val fromDatePickerDialog = DatePickerDialog(context, { _: DatePicker, y: Int, m: Int, d: Int ->
        fromDate = "$d/${m + 1}/$y"
    }, year, month, day)

    val toDatePickerDialog = DatePickerDialog(context, { _: DatePicker, y: Int, m: Int, d: Int ->
        toDate = "$d/${m + 1}/$y"
    }, year, month, day)

    val data = listOf(50f, 30f, 20f)
    val colors = listOf(Color.Green, Color.Blue, Color.Red)
    val labels = listOf("Present", "Late", "Absent")

    Column(modifier = modifier.padding(16.dp)) {
        Text(
            "Your Report",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(text = "Select Report Date Range", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(modifier = Modifier.weight(1f)) {
                OutlinedTextField(
                    value = fromDate,
                    onValueChange = {},
                    label = { Text("From") },
                    readOnly = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .clickable { fromDatePickerDialog.show() }
                )
            }

            Box(modifier = Modifier.weight(1f)) {
                OutlinedTextField(
                    value = toDate,
                    onValueChange = {},
                    label = { Text("To") },
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
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {}, modifier = Modifier.fillMaxWidth()) {
            Text("Filter")
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            SimplePieChart(
                data = data,
                colors = colors,
                modifier = Modifier.size(220.dp),
                labels = labels
            )
        }
    }
}
