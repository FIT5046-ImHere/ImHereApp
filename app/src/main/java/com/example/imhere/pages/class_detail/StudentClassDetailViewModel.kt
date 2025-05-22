package com.example.imhere.pages.class_detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.imhere.model.AttendanceStatus
import com.example.imhere.model.service.AccountService
import com.example.imhere.model.service.AttendanceService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject


@HiltViewModel
class StudentClassDetailViewModel @Inject constructor(
    private val accountService: AccountService,
    private val attendanceService: AttendanceService
) : ViewModel() {
    private val currentUserId = accountService.currentUserId
    fun markAttendance(classSessionId: String,password: String, onSuccess: () -> Unit, onError: (String?) -> Unit) {
        viewModelScope.launch {
            try {
                val passwordValid = attendanceService.checkAttendancePassword(classSessionId,password)
                if (passwordValid) {
                    attendanceService.createAttendance(
                        studentId = accountService.currentUserId,
                        classSessionId = classSessionId,
                        dateTime = Date(),
                        status = AttendanceStatus.PRESENT,
                        teacherId = currentUserId
                    )
                }
                onSuccess()
            } catch (e: Exception) {
                onError(e.message)
            } finally {
            }
        }
    }
}