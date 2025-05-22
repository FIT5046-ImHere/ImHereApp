package com.example.imhere.pages.class_detail


import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.imhere.model.service.AccountService
import com.example.imhere.model.service.AttendanceService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import androidx.compose.runtime.State
import com.example.imhere.model.service.StudentService


data class StudentAttendance(
    val name: String,
    val status: String
)


@HiltViewModel
open class TeacherClassDetailViewModel @Inject constructor(
    private val attendanceService: AttendanceService,
    private val accountService: AccountService,
    private val studentService: StudentService

) : ViewModel() {

    private val _students = mutableStateOf<List<StudentAttendance>>(emptyList())
    val students: State<List<StudentAttendance>> = _students

    fun loadAttendance(classSessionId: String) {
        viewModelScope.launch {
            try {
                // 🔍 Query only by classSessionId (others null)
                val attendances = attendanceService.getAttendances(
                    studentId = null,
                    teacherId = null,
                    classSessionId = classSessionId,
                    startDate = null,
                    endDate = null
                )
                val studentAttendanceList = attendances.map { attendance ->
                    val profile = try {
                        accountService.fetchUserProfile(attendance.studentId)
                    } catch (e: Exception) {
                        null
                    }

                    StudentAttendance(
                        name = profile?.name ?: "Unknown",
                        status = attendance.status.name.lowercase()
                    )
                }

                _students.value = studentAttendanceList
            } catch (e: Exception) {
                _students.value = emptyList()
            }
        }
    }
}
