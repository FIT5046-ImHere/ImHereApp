package com.example.imhere.model.service

import com.example.imhere.model.service.CalendarEvent

interface CalendarService {
    suspend fun createEvent(event: CalendarEvent): CalendarEvent
}
