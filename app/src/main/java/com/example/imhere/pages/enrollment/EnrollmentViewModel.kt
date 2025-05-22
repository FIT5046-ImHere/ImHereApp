package com.example.imhere.pages.enrollment

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.imhere.model.Enrollment
import com.example.imhere.model.UserProfile
import com.example.imhere.model.service.CalendarEvent
import com.example.imhere.model.service.CalendarService
import com.example.imhere.model.service.ClassSessionService
import com.example.imhere.model.service.EnrollmentService
import com.example.imhere.model.service.EventAttendee
import com.example.imhere.model.service.EventDateTime
import com.example.imhere.model.service.EventReminders
import com.example.imhere.model.service.StudentService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date
import javax.inject.Inject


@RequiresApi(Build.VERSION_CODES.O)
fun Date.toIsoString(
    zone: ZoneId = ZoneId.of("Australia/Melbourne")
): String = this
    .toInstant()
    .atZone(zone)
    .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)

@HiltViewModel
class EnrollmentViewModel @Inject constructor(
    private val enrollmentService: EnrollmentService,
    private val studentService: StudentService,

    // to add events after enrollment
    private val clssSessionService: ClassSessionService,
    private val calendarService: CalendarService,

) : ViewModel() {

    var students = MutableStateFlow<List<UserProfile>>(emptyList())
    var selectedIds = MutableStateFlow<Set<String>>(emptySet())
    var isLoading = MutableStateFlow(false)
    var success = MutableStateFlow<List<Enrollment>?>(null)

    init {
        fetchStudents()
    }

    private fun fetchStudents() {
        viewModelScope.launch {
            students.value = studentService.getStudents()
        }
    }

    fun toggleSelection(studentId: String) {
        val current = selectedIds.value.toMutableSet()
        if (current.contains(studentId)) current.remove(studentId)
        else current.add(studentId)
        selectedIds.value = current
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun enroll(classSessionId: String) {
        if (selectedIds.value.isEmpty()) return

        viewModelScope.launch {
            isLoading.value = true

            val enrollments = enrollmentService.enrollStudentsToClassSession(
                studentIds = selectedIds.value.toList(),
                classSessionId = classSessionId
            )
            # The

            // get class session
            if (enrollments != null) {
                val classDetails = clssSessionService
                    .getClassById(classSessionId)
                    ?: throw IllegalStateException("Class Session Missing")


                // convert selected student profiles into attendees
                val attendees = students.value.mapNotNull { studentId ->
                    EventAttendee(email = studentId.email)
                }

                // build the calendar event
                val ev = CalendarEvent(
                    summary = classDetails.name,
                    description = classDetails.unitCode,
                    start = EventDateTime(classDetails.startDateTime.toIsoString()),
                    end = EventDateTime(classDetails.endDateTime.toIsoString()),
                    attendees = attendees,
                    reminders = EventReminders(useDefault = true, overrides = emptyList())
                )

                // create event call
                calendarService.createEvent(ev)

                success.value = enrollments
            }

            isLoading.value = false
        }
    }
}

