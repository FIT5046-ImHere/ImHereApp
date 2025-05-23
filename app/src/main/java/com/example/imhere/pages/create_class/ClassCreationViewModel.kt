package com.example.imhere.pages.create_class

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.imhere.model.ClassSession
import com.example.imhere.model.ClassSessionRecurrence
import com.example.imhere.model.service.AccountService
import com.example.imhere.model.service.ClassSessionService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class ClassCreationViewModel @Inject constructor(
    private val classSessionService: ClassSessionService,
    private val accountService: AccountService,
) : ViewModel() {
    val classSessions = MutableStateFlow<List<ClassSession>>(emptyList())
    init {
        viewModelScope.launch {
            try {
                classSessions.value = classSessionService.getAllClassSessions()
            } finally {

            }
        }
    }

    var isSubmitting = false

    private fun combineDateAndTime(date: Date, time: String): Date {
        val (hour, minute) = time.split(":").map { it.toInt() }

        val calendar = Calendar.getInstance()
        calendar.time = date

        calendar.set(Calendar.HOUR_OF_DAY, hour)
        calendar.set(Calendar.MINUTE, minute)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)

        return calendar.time
    }

    fun createClass(
        name: String,
        location: String,
        unitCode: String,
        recurrence: String,
        startDate: Date,
        endDate: Date,
        startTime: String,
        endTime: String,
        onSuccess: (ClassSession?) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            isSubmitting = true
            try {
                val startDateTime = combineDateAndTime(startDate, startTime)
                val endDateTime = combineDateAndTime(endDate, endTime)

                val classSession = ClassSession(
                    name = name,
                    location = location,
                    unitCode = unitCode,
                    teacherId = accountService.currentUserId,
                    recurrence = ClassSessionRecurrence.valueOf(recurrence),
                    startDateTime = startDateTime,
                    endDateTime = endDateTime
                )
                val classSesh = classSessionService.createClassSession(classSession)
                onSuccess(classSesh)
            } catch (e: Exception) {
                onError(e.localizedMessage ?: "Failed to create class")
            } finally {
                isSubmitting = false
            }
        }
    }
}
