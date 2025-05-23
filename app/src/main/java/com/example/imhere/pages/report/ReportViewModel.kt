package com.example.imhere.pages.report

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.imhere.model.Attendance
import com.example.imhere.model.AttendanceStatus
import com.example.imhere.model.ClassSession
import com.example.imhere.model.UserProfile
import com.example.imhere.model.UserProfileType
import com.example.imhere.model.service.AccountService
import com.example.imhere.model.service.AttendanceService
import com.example.imhere.model.service.ClassSessionService
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date
import javax.inject.Inject


@RequiresApi(Build.VERSION_CODES.O)
@HiltViewModel
class ReportViewModel @Inject constructor(
    private val accountService: AccountService,
    private val attendanceService: AttendanceService,
    private val classSessionService: ClassSessionService
) : ViewModel() {

    var profile by mutableStateOf<UserProfile?>(null)

    init {
        viewModelScope.launch {
            accountService.currentUserProfile.collect {
                profile = it
            }
        }
    }

    var classSessions by mutableStateOf<List<ClassSession>>(emptyList())

    init {
        viewModelScope.launch {
            accountService.currentUserProfile.collect { user ->
                if (user != null) {
                    val sessions = when (user.type) {
                        UserProfileType.TEACHER -> {
                            classSessionService.getAllClassSessions(teacherId = user.uid)
                        }

                        UserProfileType.STUDENT -> {
                            classSessionService.getAllClassSessions(studentId = user.uid)
                        }

                        else -> emptyList()
                    }

                    classSessions = sessions
                }
            }
        }
    }

    private var attendances by mutableStateOf<List<Attendance>>(emptyList())
        private set

    var selectedSessionId by mutableStateOf<String?>(null)
    var startDate by mutableStateOf<LocalDate>(LocalDate.now().minusMonths(1))
    var endDate by mutableStateOf<LocalDate>(LocalDate.now())

    private val dateFormatter = DateTimeFormatter.ofPattern("d/M/yyyy")

    fun applyFilters() {
        viewModelScope.launch {
            val studentId = if (profile?.type == UserProfileType.STUDENT) profile!!.uid else null
            val teacherId = if (profile?.type == UserProfileType.TEACHER) profile!!.uid else null

            attendances = attendanceService.getAttendances(
                studentId = studentId,
                teacherId = teacherId,
                classSessionId = selectedSessionId,
                startDate = Date.from(startDate.atStartOfDay(ZoneId.systemDefault()).toInstant()),
                endDate = Date.from(
                    endDate.atTime(LocalTime.MAX).atZone(ZoneId.systemDefault()).toInstant()
                ),
            )
        }
    }

    private val aggregatedAttendances by derivedStateOf {
        attendances.sortedBy { it.status }.groupingBy { it.status }.eachCount()
    }

    val pieEntries by derivedStateOf {
        listOf(
            PieEntry(
                aggregatedAttendances[AttendanceStatus.PRESENT]?.toFloat() ?: 0f,
                "Present"
            ),
            PieEntry(
                aggregatedAttendances[AttendanceStatus.LATE]?.toFloat() ?: 0f,
                "Late"
            ),
            PieEntry(
                aggregatedAttendances[AttendanceStatus.ABSENT]?.toFloat() ?: 0f,
                "Absent"
            ),
        )
    }

    val dateLabels: List<String> by derivedStateOf {
        attendances
            .map { it.dateTime.toInstant().atZone(ZoneId.systemDefault()).toLocalDate() }
            .distinct()
            .sorted()
            .map { it.format(dateFormatter) }
    }

    fun makeLineDataSets(): List<LineDataSet> {
        val groupedByDate = attendances.groupBy { attendance ->
            attendance.dateTime.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
        }

        return AttendanceStatus.entries.toTypedArray().mapIndexed { index, status ->
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
}


