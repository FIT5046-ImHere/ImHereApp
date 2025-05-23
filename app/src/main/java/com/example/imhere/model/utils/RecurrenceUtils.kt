// src/main/java/com/example/imhere/util/RecurrenceUtils.kt
package com.example.imhere.util

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.imhere.model.ClassSessionRecurrence
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date

object RecurrenceUtils {
    // Use your local zone for UNTIL conversion
    @RequiresApi(Build.VERSION_CODES.O)
    private val LOCAL_ZONE = ZoneId.of("Australia/Melbourne")
    // RFC-5545 requires ITZT format: YYYYMMDD'T'HHMMSS'Z'
    @RequiresApi(Build.VERSION_CODES.O)
    private val UNTIL_FMT = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss'Z'")

    /**
     * @param type  the recurrence value (ONCE, WEEKLY, MONTHLY)
     * @param until the final Date to stop repeating on (firestore endDateTime)
     * @return      null for ONCE, or a single-element List containing your RRULE
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun buildRrule(
        type: ClassSessionRecurrence,
        until: Date?
    ): List<String>? {
        // no rule for a one‐off
        if (type == ClassSessionRecurrence.ONCE || until == null) return null

        // convert your Date → UTC string
        val untilUtc = until.toInstant()
            .atZone(LOCAL_ZONE)                                   // localize
            .withZoneSameInstant(ZoneId.of("UTC"))                // shift to UTC
            .format(UNTIL_FMT)                                    // format as 20250530T050000Z

        // pick the frequency
        val freq = when (type) {
            ClassSessionRecurrence.WEEKLY  -> "WEEKLY"
            ClassSessionRecurrence.MONTHLY -> "MONTHLY"
            else                           -> return null
        }

        // build the single-element list
        return listOf("RRULE:FREQ=$freq;UNTIL=$untilUtc")
    }
}
