package com.example.imhere.pages.class_detail

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.imhere.model.AttendanceStatus
import com.example.imhere.model.ClassSession
import com.example.imhere.model.service.AccountService
import com.example.imhere.model.service.AttendanceService
import com.example.imhere.model.service.ClassSessionService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject


@HiltViewModel
class StudentClassDetailViewModel @Inject constructor(
    private val accountService: AccountService,
    private val attendanceService: AttendanceService,
    private val classSessionService: ClassSessionService,
) : ViewModel() {
    private val currentUserId = accountService.currentUserId
    val attendanceStatus = MutableStateFlow<AttendanceStatus?>(null)
    val hasRecorded = MutableStateFlow(false)
    val isLoading = MutableStateFlow(true)
    val classSession = MutableStateFlow<ClassSession?>(null)


    fun loadClassSessionAndAttendance(classSessionId: String) {
        viewModelScope.launch {
            isLoading.value = true
            try {
                val session = classSessionService.getClassById(classSessionId)
                classSession.value = session

                val attendances = attendanceService.getCurrentAttendances(classSessionId)
                val record = attendances.find { it.studentId == accountService.currentUserId }

                attendanceStatus.value = record?.status
                hasRecorded.value = record != null
            } catch (e: Exception) {
                Log.e("StudentVM", "Error loading class or attendance", e)
            } finally {
                isLoading.value = false
            }
        }
    }

    fun markAttendance(
        classSessionId: String,
        password: String,
        onSuccess: (AttendanceStatus) -> Unit,
        onError: (String?) -> Unit
    ) {
        Log.d("MARKING ATT", "MARKING ATT")
        viewModelScope.launch {
            try {
                val passwordValid = attendanceService.checkAttendancePassword(classSessionId, password)
                if (!passwordValid) {
                    onError("Password is invalid!")
                    return@launch
                }

                val session = classSessionService.getClassById(classSessionId)
                if (session == null) {
                    onError("Class session not found.")
                    return@launch
                }

                val now = Date()
                val sessionTime = session.startDateTime

                // Extract only time from session.startDateTime and set to today
                val calendarNow = java.util.Calendar.getInstance()
                val calendarSession = java.util.Calendar.getInstance().apply {
                    time = sessionTime
                    set(java.util.Calendar.YEAR, calendarNow.get(java.util.Calendar.YEAR))
                    set(java.util.Calendar.MONTH, calendarNow.get(java.util.Calendar.MONTH))
                    set(java.util.Calendar.DAY_OF_MONTH, calendarNow.get(java.util.Calendar.DAY_OF_MONTH))
                }

                val diffMillis = now.time - calendarSession.time.time
                val diffMinutes = diffMillis / (60 * 1000)

                val status = if (diffMinutes >= 15) AttendanceStatus.LATE else AttendanceStatus.PRESENT

                attendanceService.createAttendance(
                    studentId = accountService.currentUserId,
                    classSessionId = classSessionId,
                    dateTime = now,
                    status = status,
                    teacherId = currentUserId
                )

                onSuccess(status)
            } catch (e: Exception) {
                onError(e.message)
            }
        }
    }
}