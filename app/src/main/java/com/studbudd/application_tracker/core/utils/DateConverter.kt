package com.studbudd.application_tracker.core.utils

import androidx.room.TypeConverter
import java.util.*

class DateConverter {
    @TypeConverter fun calendarToDatestamp(calendar: Calendar): Long = calendar.timeInMillis

    @TypeConverter
    fun datestampToCalendar(value: Long): Calendar =
        Calendar.getInstance().apply { timeInMillis = value }

}