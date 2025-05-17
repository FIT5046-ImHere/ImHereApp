package com.example.imhere.pages.create_class

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.imhere.model.ClassSession
import com.example.imhere.model.service.ClassSessionService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class ClassCreationViewModel @Inject constructor(
    private val classSessionService: ClassSessionService
) : ViewModel() {

    var isSubmitting = false
        private set

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
        onSuccess: () -> Unit,
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
                    recurrence = recurrence,
                    startDateTime = startDateTime,
                    endDateTime = endDateTime
                )
                classSessionService.createClassSession(classSession)
                onSuccess()
            } catch (e: Exception) {
                onError(e.localizedMessage ?: "Failed to create class")
            } finally {
                isSubmitting = false
            }
        }
    }
}
