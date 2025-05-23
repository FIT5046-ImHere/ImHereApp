package com.example.imhere.pages.report

import android.app.DatePickerDialog
import android.os.Build
import android.widget.DatePicker
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.PercentFormatter
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import androidx.core.graphics.toColorInt

enum class ChartToggleType { PIE, LINE }

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ReportPage(
    modifier: Modifier = Modifier,
    viewModel: ReportViewModel = hiltViewModel(),
) {
    LaunchedEffect(
        viewModel.startDate,
        viewModel.endDate,
        viewModel.selectedSessionId
    ) {
        viewModel.applyFilters()
    }

    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)

    var chartType by remember { mutableStateOf(ChartToggleType.PIE) }

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

    var isExpanded by remember { mutableStateOf(false) }
    val selectedSession = viewModel.classSessions.find{ it.id == viewModel.selectedSessionId }

    val pieDataSet = PieDataSet(viewModel.pieEntries, "Pie Data Set").apply {
        colors = listOf(
            "#4CAF50".toColorInt(), // Green
            "#FFEB3B".toColorInt(), // Yellow
            "#F44336".toColorInt() // Red
        )
    }
    val pieData = PieData(pieDataSet)
    pieDataSet.xValuePosition = PieDataSet.ValuePosition.INSIDE_SLICE;
    pieDataSet.yValuePosition = PieDataSet.ValuePosition.INSIDE_SLICE;
    pieDataSet.valueFormatter = PercentFormatter()
    pieDataSet.valueTextSize = 40f

    val dateFormatter = remember { DateTimeFormatter.ofPattern("d/M/yyyy") }

    val formattedStartDate = viewModel.startDate.format(dateFormatter)
    val formattedEndDate = viewModel.endDate.format(dateFormatter)

    val lineDataSets by remember { derivedStateOf { viewModel.makeLineDataSets() } }

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
                    onValueChange = { },
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
        ExposedDropdownMenuBox(
            expanded = isExpanded,
            modifier = Modifier.padding(10.dp),
            onExpandedChange = { isExpanded = it }
        ) {
            TextField(
                value = selectedSession?.name ?: "All Classes",
                onValueChange = {},
                readOnly = true,
                label = { Text("Class Session") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(isExpanded) },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor()
            )
            ExposedDropdownMenu(
                expanded = isExpanded,
                onDismissRequest = { isExpanded = false }
            ) {
                DropdownMenuItem(
                    text = { Text("All Classes") },
                    onClick = {
                        viewModel.selectedSessionId = null
                        isExpanded = false
                    }
                )
                viewModel.classSessions.forEach { session ->
                    DropdownMenuItem(
                        text = { Text(session.name) },
                        onClick = {
                            viewModel.selectedSessionId = session.id
                            isExpanded = false
                        }
                    )
                }
            }
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            ChartToggleType.entries.forEach { type ->
                Button(
                    onClick = { chartType = type },
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 4.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (chartType == type)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Text(type.name)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp),
            contentAlignment = Alignment.Center
        ) {
            when (chartType) {
                ChartToggleType.PIE -> {
                    AndroidView(
                        modifier = Modifier.fillMaxSize(),
                        factory = { context ->
                            PieChart(context).apply {
                                data = pieData
                                description.isEnabled = false
                                centerText = "Attendance Status"
                                setDrawCenterText(true)
                                setEntryLabelTextSize(14f)
                                animateY(4000)
                            }
                        },
                        update = { chart ->
                            chart.data = PieData(
                                PieDataSet(viewModel.pieEntries, "Status")
                                    .apply {
                                        valueFormatter = PercentFormatter()
                                        colors = listOf(
                                            "#4CAF50".toColorInt(), // Green
                                            "#FFEB3B".toColorInt(), // Yellow
                                            "#F44336".toColorInt() // Red
                                        )
                                        valueTextSize = 12f
                                    }
                            )
                            chart.invalidate()
                        }

                    )
                }

                ChartToggleType.LINE -> {
                    AndroidView(
                        modifier = modifier.fillMaxSize(),
                        factory = { ctx ->
                            LineChart(ctx).apply {
                                description.isEnabled = false
                                axisRight.isEnabled = false
                                animateX(1000)
                                xAxis.granularity = 1f
                                xAxis.setDrawLabels(true)
                                xAxis.valueFormatter =
                                    IndexAxisValueFormatter(viewModel.dateLabels)
                            }
                        },
                        update = { chart ->

                            chart.data = LineData(lineDataSets)
                            chart.xAxis.valueFormatter =
                                IndexAxisValueFormatter(viewModel.dateLabels)

                            chart.invalidate()
                        }
                    )
                }
            }
        }

    }
}
