package com.example.imhere.model.service

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

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

/** Wrapper for listing */
data class EventsListResponse(
    val items: List<CalendarEvent>
)
