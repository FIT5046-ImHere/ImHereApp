package com.example.imhere.pages.report

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.imhere.mock_data.AttendanceMockData
import com.example.imhere.mock_data.ClassSessionMockData
import com.example.imhere.mock_data.SelfAttendanceMockData
import com.example.imhere.model.Attendance
import com.example.imhere.model.AttendanceStatus
import com.example.imhere.model.UserProfile
import com.example.imhere.model.UserProfileType
import com.example.imhere.model.service.AccountService
import com.example.imhere.model.service.AttendanceService
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import javax.inject.Inject


enum class ChartType { PIE, BAR }


/**
 * ViewModel for the Report screen.
 * - Loads the current user profile to scope data (teacher vs. student).
 * - Maintains date‐range and session‐ID filters.
 * - Exposes reactive chart data (pie & line) based on filtered attendances.
 */
@RequiresApi(Build.VERSION_CODES.O)
@HiltViewModel
class ReportViewModel @Inject constructor(
    private val accountService: AccountService,
    private val attendanceService: AttendanceService,

) : ViewModel() {

    // region — Profile & Permissions
    /** Current user profile; determines which attendance list to use. */
    var profile by mutableStateOf<UserProfile?>(null)

    init {
        viewModelScope.launch {
            accountService.currentUserProfile.collect {
                profile = it
            }
        }
    }
    // endregion

    // region — Raw Attendance Sources
    private val selfAttendances = SelfAttendanceMockData.attendanceList
    private val allAttendances = AttendanceMockData.attendanceList

    /**
     * Attendance records scoped by role:
     * Teachers see all; students see only their own.
     */


    var attendances by mutableStateOf<List<Attendance>>(emptyList())
        private set
//    val attendances: List<Attendance>
//        get() = if (profile?.type == UserProfileType.TEACHER) allAttendances else selfAttendances


    init {
        Log.d("ReportViewModel", attendances.toString())
    }


    // endregion

    // region — Filters

    /** Currently selected session ID to filter by, or `null` to disable session‐filtering. */
    var selectedSessionId by mutableStateOf<String?>(null)

    // Date range state (default: last 1 month)
    /** Start of date range filter (inclusive). */
    var startDate by mutableStateOf(LocalDate.now().minusMonths(1))

    /** End of date range filter (inclusive). */
    var endDate by mutableStateOf(LocalDate.now())

    private val dateFormatter = DateTimeFormatter.ofPattern("d/M/yyyy")

    // endregion

    fun applyFilters() {
        Log.d("ASSHOLE","ASSHOLE")
        viewModelScope.launch{
            Log.d("ASSHOLE","ASSHOLE")

            val studentId = if (profile?.type == UserProfileType.STUDENT) profile!!.uid else null
            val teacherId = if (profile?.type == UserProfileType.TEACHER) profile!!.uid else null
            Log.d("BITCH","BITCH")

            val hey = attendanceService.getAttendances(
                studentId = studentId,
                teacherId = teacherId,
//                classSessionId = TODO(),
//                startDate = TODO(),
//                endDate = TODO(),
            )
            attendances = hey
            Log.d("FETCHHH", hey.toString())
        }
    }

    // region — Class Sessions for Dropdown
    /** Distinct session IDs available, based on user role. */
    val classIds by derivedStateOf {
        if (profile?.type == UserProfileType.TEACHER) {
            ClassSessionMockData.classSessions.mapNotNull { it.id }
        } else {
            selfAttendances.map { it.classSessionId }
        }.distinct()
    }

    /** Full ClassSession objects matching [classIds]. */
    val classSessions by derivedStateOf {
        ClassSessionMockData.classSessions.filter { it.id in classIds }
    }
    // endregion

    // region — Filtered & Aggregated Data
    /**
     * Attendances filtered by:
     *  - date between [startDate] and [endDate], and
     *  - matching [selectedSessionId] if non‐null.
     */
    val filteredAttendances by derivedStateOf {
        attendances.filter { record ->
            val date = record.dateTime
                .toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate()

            val inDateRange = !date.isBefore(startDate) && !date.isAfter(endDate)
            val matchesSession = selectedSessionId?.let { record.classSessionId == it } ?: true
            inDateRange && matchesSession
        }
    }

    /**
     * Counts of each AttendanceStatus after filtering.
     * E.g. { PRESENT → 5, LATE → 2, ABSENT → 0 }.
     */
    val aggregatedAttendances by derivedStateOf {
        filteredAttendances.groupingBy { it.status }.eachCount()
    }
    // endregion

    // region — Pie Chart Data
    /** Generates PieEntry list from [aggregatedAttendances]. */
    val pieEntries by derivedStateOf {
        aggregatedAttendances.map { (status, count) ->
            PieEntry(
                count.toFloat(),
                status.name.lowercase().replaceFirstChar { it.uppercase() }
            )
        }
    }
    // endregion

    // region — Line Chart Data

    /** X‐axis labels: distinct dates in [filteredAttendances], formatted. */
    val dateLabels: List<String> by derivedStateOf {
        // get all record dates, dedupe, sort
        filteredAttendances
            .map { it.dateTime.toInstant().atZone(ZoneId.systemDefault()).toLocalDate() }
            .distinct()
            .sorted()
            .map { it.format(dateFormatter) }
    }

    /**
     * Builds one LineDataSet per AttendanceStatus.
     * Each dataset contains (x = day index, y = count for that status on that date).
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
            LineDataSet(
                entries,
                status.name.lowercase().replaceFirstChar { it.uppercase() }).apply {
                color = ColorTemplate.MATERIAL_COLORS[index % ColorTemplate.MATERIAL_COLORS.size]
                setCircleColor(color)
                lineWidth = 2f
                circleRadius = 4f
                valueTextSize = 10f
                mode = LineDataSet.Mode.CUBIC_BEZIER
            }
        }
    }
    // endregion

}