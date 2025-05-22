package com.example.imhere

import com.google.android.gms.auth.GoogleAuthUtil
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.api.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Retrofit

// writing teh code for all google cal shit

/* Using app authentication
The permissions or "authority" the principal has to access data or perform operations.
The act of authorization is carried out through code you write in your app.
This code informs the user that the app wishes to act on their behalf and, if allowed, uses
your app's unique credentials to obtain an access token from Google used to access data
or perform operations.
*/

// credential type used : OAuth 2 client ID

//client ID: 482684834489-p50fskjgsii7jjpbgn8hvac68h254shv.apps.googleusercontent.com
// API Key (unrestricted): AIzaSyAeJCybbzR-qRLF6x3nCCMv-2pyFcVhEQM


class googleCal {
    private val BASE_URL = "https://www.googleapis.com/calendar/v3"

}

// get access token
suspend fun fetchAccessToken(
    context: Context,
    account: GoogleSignInAccount
): String = withContext(Dispatchers.IO) {
    GoogleAuthUtil.getToken(
        context,
        account.account,
        "oauth2: https://www.googleapis.com/auth/calendar"
    )
}

// data classes for responses
data class CalendarEvent(
    val summary: String,
    val description: String?,
    val start: EventDateTime,
    val end: EventDateTime,
    val attendees: List<EventAttendee>?,
    val reminders: EventReminders? = null
)

data class EventDateTime(
    @SerializedName("dateTime") val dateTime: String,  // e.g. "2025-06-01T14:00:00-07:00"
    val timeZone: String = "Australia/Melbourne"
)

data class EventAttendee(
    val email: String
)

data class EventReminders(
    @SerializedName("useDefault") val useDefault: Boolean = false,
    @SerializedName("overrides") val overrides: List<EventReminder>
)

data class EventReminder(
    val method: String,    // "email" or "popup"
    val minutes: Int
)

// Interface for setting events
interface CalendarApi {
    @POST("calendars/{calendarId}/events")
    suspend fun createEvent(
        @Path("calendarId") calendarId: String = "primary",
        @Query("sendUpdates") sendUpdates: String = "all",
        @Body event: CalendarEvent
    ): CalendarEvent

    @GET("calendars/{calendarId}/events")
    suspend fun listEvents(
        @Path("calendarId") calendarId: String = "primary",
        @Query("timeMin") timeMin: String? = null,
        @Query("maxResults") maxResults: Int = 50
    ): EventsListResponse
}

// A wrapper to list events:
data class EventsListResponse(
    val items: List<CalendarEvent>
)

fun makeOkHttpClient(accessToken: String): OkHttpClient =
    OkHttpClient.Builder()
        .addInterceptor { chain ->
            val req = chain.request()
                .newBuilder()
                .addHeader("Authorization", "Bearer $accessToken")
                .build()
            chain.proceed(req)
        }
        .build()


fun makeCalendarApi(accessToken: String): CalendarApi {
    return Retrofit.Builder()
        .baseUrl("https://www.googleapis.com/calendar/v3/")
        .client(makeOkHttpClient(accessToken))
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(CalendarApi::class.java)
}

