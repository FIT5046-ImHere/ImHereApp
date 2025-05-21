package com.example.imhere.pages.report

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.imhere.mock_data.AttendanceMockData
import com.example.imhere.model.Attendance
import com.example.imhere.model.AttendanceStatus
import com.example.imhere.model.service.AccountService
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import javax.inject.Inject


enum class ChartType { PIE, BAR }

@RequiresApi(Build.VERSION_CODES.O)
@HiltViewModel
class ReportViewModel @Inject constructor(
//    private val accountService: AccountService
//    private val _records: MutableStateFlow<List<Attendance>> = MutableStateFlow<List<Attendance>>(AttendanceMockData.attendanceList),
//    val records: StateFlow<List<Attendance>> = _records.asStateFlow()
) : ViewModel() {
    val attendances = AttendanceMockData.attendanceList

    init {
        Log.d("ReportViewModel",attendances.toString())
    }

    // Date range state (default: last 1 month)
    var startDate by mutableStateOf(LocalDate.now().minusMonths(1))
    var endDate by mutableStateOf(LocalDate.now())

    // 3. Filtered attendances recomputed when startDate or endDate changes
    val filteredAttendances by derivedStateOf {
        attendances.filter { attendance ->
            val recordDate = attendance.dateTime.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
            !recordDate.isBefore(startDate) && !recordDate.isAfter(endDate)
        }
    }

    // 4. Aggregated counts by status, updates when filteredAttendances changes
    val aggregatedAttendances by derivedStateOf {
        filteredAttendances
            .groupingBy { it.status }
            .eachCount()
    }

    // 5. Pie entries for chart, updates when aggregatedAttendances changes
    val pieEntries by derivedStateOf {
        aggregatedAttendances.map { (status, count) ->
            PieEntry(
                count.toFloat(),
                status.name.lowercase().replaceFirstChar { it.uppercase() }
            )
        }
    }
    private val dateFormatter = DateTimeFormatter.ofPattern("d/M/yyyy")

    //Line chart data
    /**
     * A sorted list of all dates (as strings) in the current range.
     * We’ll use these as our X‐axis labels.
     */
    val dateLabels: List<String> by derivedStateOf {
        // get all record dates, dedupe, sort
        filteredAttendances
            .map { it.dateTime.toInstant().atZone(ZoneId.systemDefault()).toLocalDate() }
            .distinct()
            .sorted()
            .map { it.format(dateFormatter) }
    }
    /**
     * Builds a LineDataSet per AttendanceStatus,
     * mapping each date label index to the count on that day.
     */
    fun makeLineDataSets(): List<LineDataSet> {
        // Group records by LocalDate
        val groupedByDate = filteredAttendances.groupBy { attendance ->
            attendance.dateTime.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
        }

        return AttendanceStatus.entries.toTypedArray().mapIndexed { index, status ->
            // Create chart entries: (x=index of dateLabels, y=count)
            val entries = dateLabels.mapIndexed { idx, label ->
                val date = LocalDate.parse(label, dateFormatter)
                val count = groupedByDate[date]?.count { it.status == status } ?: 0
                Entry(idx.toFloat(), count.toFloat())
            }
            LineDataSet(entries, status.name.lowercase().replaceFirstChar { it.uppercase() }).apply {
                color = ColorTemplate.MATERIAL_COLORS[index % ColorTemplate.MATERIAL_COLORS.size]
                setCircleColor(color)
                lineWidth = 2f
                circleRadius = 4f
                valueTextSize = 10f
                mode = LineDataSet.Mode.CUBIC_BEZIER
            }
        }
    }


}