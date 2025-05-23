package com.example.imhere.model.service.impl

import com.example.imhere.model.service.CalendarApi
import com.example.imhere.model.service.CalendarEvent
import com.example.imhere.model.service.CalendarService
import javax.inject.Inject

class CalendarServiceImpl @Inject constructor(
    private val api: CalendarApi
) : CalendarService {

    /** Create a new event in the user’s primary calendar */
    override suspend fun createEvent(event: CalendarEvent): CalendarEvent {
        return api.createEvent(
            calendarId = "primary",
            sendUpdates = "all",
            event = event
        )
    }


}
