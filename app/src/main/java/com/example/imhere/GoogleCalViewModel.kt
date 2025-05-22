import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.imhere.CalendarApi
import com.example.imhere.CalendarEvent
import com.example.imhere.EventAttendee
import com.example.imhere.EventDateTime
import com.example.imhere.EventReminder
import com.example.imhere.EventReminders
import com.example.imhere.fetchAccessToken
import com.example.imhere.makeCalendarApi
import com.example.imhere.model.ClassSession
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.api.Context
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class AttendanceViewModel : ViewModel() {
    private var calendarApi: CalendarApi? = null

    fun onGoogleSignIn(account: GoogleSignInAccount, context: Context) {
        viewModelScope.launch {
            val token = fetchAccessToken(context, account)
            calendarApi = makeCalendarApi(token)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun scheduleSessionReminder(session: ClassSession) {
        val api = calendarApi ?: return
        viewModelScope.launch {
            val startIso = Instant.ofEpochMilli(session.startTime)
                .atZone(ZoneId.of("Australia/Melbourne"))
                .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)

            val endIso = Instant.ofEpochMilli(session.startTime + session.duration)
                .atZone(ZoneId.of("Australia/Melbourne"))
                .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)

            val event = CalendarEvent(
                summary = "Attend ${session.name}",
                description = session.notes,
                start = EventDateTime(dateTime = startIso),
                end   = EventDateTime(dateTime = endIso),
                attendees = listOf(EventAttendee(session.userEmail)),
                reminders = EventReminders(
                    useDefault = false,
                    overrides = listOf(
                        EventReminder("email", 60),
                        EventReminder("popup", 10)
                    )
                )
            )

            try {
                val created = api.createEvent(event = event)
                Log.d("Calendar", "Event created: ${created.summary}")
            } catch (e: Exception) {
                Log.e("Calendar", "Failed to create event", e)
            }
        }
    }
}
