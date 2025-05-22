package com.example.imhere.model.service

import com.example.imhere.model.service.CalendarEvent

interface CalendarService {
    /** Creates a new event in the user’s primary calendar */
    suspend fun createEvent(event: CalendarEvent): CalendarEvent

    // you can add more methods here, e.g. listEvents(), deleteEvent(), etc.
}
