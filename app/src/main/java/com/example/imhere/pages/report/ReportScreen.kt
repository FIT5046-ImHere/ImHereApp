package com.example.imhere.pages.report

import SimplePieChart
import android.app.DatePickerDialog
import android.os.Build
import android.util.Log
import android.widget.DatePicker
import androidx.annotation.RequiresApi
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
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.imhere.pages.login.LoginViewModel
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.utils.ColorTemplate
import com.github.mikephil.charting.formatter.PercentFormatter
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

//@Preview(showBackground = true)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ReportPage(modifier: Modifier = Modifier, viewModel: ReportViewModel = hiltViewModel(), navController: NavHostController) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)
    val attendances = viewModel.attendances
//    var fromDate by remember { mutableStateOf("") }
//    var toDate by remember { mutableStateOf("") }

    val fromDatePickerDialog = DatePickerDialog(context, { _: DatePicker, y: Int, m: Int, d: Int ->
        val text = "$d/${m + 1}/$y"
        val formatter = DateTimeFormatter.ofPattern("d/M/yyyy")
        viewModel.startDate = LocalDate.parse(text, formatter)
    }, year, month, day)

    val toDatePickerDialog = DatePickerDialog(context, { _: DatePicker, y: Int, m: Int, d: Int ->
        val text = "$d/${m + 1}/$y"
        val formatter = DateTimeFormatter.ofPattern("d/M/yyyy")
        viewModel.endDate = LocalDate.parse(text, formatter)
    }, year, month, day)

//    val data = listOf(50f, 30f, 20f)
//    val colors = listOf(Color.Green, Color.Blue, Color.Red)
//    val labels = listOf("Present", "Late", "Absent")

    LaunchedEffect(viewModel.pieEntries) {
        Log.d("LaunchedEffectCunt", "CHAAANGEGEEEEs")
    }

    val pieDataSet = PieDataSet(viewModel.pieEntries, "Pie Data Set")
    pieDataSet.colors = ColorTemplate.COLORFUL_COLORS.toList()
    val pieData = PieData(pieDataSet)
    pieDataSet.xValuePosition = PieDataSet.ValuePosition.INSIDE_SLICE;
    pieDataSet.yValuePosition = PieDataSet.ValuePosition.INSIDE_SLICE;
    //we created a class for adding "%" sign using
    pieDataSet.valueFormatter = PercentFormatter()
    pieDataSet.valueTextSize= 40f

    // Date formatter for display: day/month/year
    val dateFormatter = remember { DateTimeFormatter.ofPattern("d/M/yyyy") }

    // Format dates for text fields
    val formattedStartDate = viewModel.startDate.format(dateFormatter)
    val formattedEndDate = viewModel.endDate.format(dateFormatter)

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
                    value = formattedStartDate,
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
                    value = formattedEndDate,
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
//            TODO: create chart
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { context ->
                    PieChart(context).apply {
                        data = pieData
                        description.isEnabled = false
                        centerText = "Expenses"
                        setDrawCenterText(true)
                        setEntryLabelTextSize(14f)
                        animateY(4000)
                    }
                },
                update = { chart ->
                    // Called on every recomposition!  Here you can give it fresh data:
                    chart.data = PieData(
                        PieDataSet(viewModel.pieEntries, "Status")
                            .apply {
                                colors = ColorTemplate.COLORFUL_COLORS.toList()
                                valueFormatter = PercentFormatter()
                                valueTextSize = 12f
                            }
                    )
                    chart.invalidate()  // redraw with new data
                }
            )

        }
    }
}
