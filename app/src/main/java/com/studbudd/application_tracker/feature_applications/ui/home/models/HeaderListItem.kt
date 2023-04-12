package com.studbudd.application_tracker.feature_applications.ui.home.models

import com.studbudd.application_tracker.core.utils.TimestampHelper
import java.util.*

class HeaderListItem(private val createdAtTimestamp: String) : ListItem() {

    fun getTag(): String {
        TimestampHelper.getDate(createdAtTimestamp)?.let { date ->
            if (isSameDay(date)) return HEADER_TODAY
            if (isPreviousDay(date)) return HEADER_YESTERDAY
            if (isSameWeek(date)) return HEADER_THIS_WEEK
            if (isSameMonth(date) && isSameYear(date)) return HEADER_THIS_MONTH
            return TimestampHelper.getFormattedString(createdAtTimestamp, HEADER_OTHER_FORMAT)
        } ?: return "-"
    }

    private fun getCalendarInstance(date: Date): Calendar {
        return Calendar.getInstance().apply { time = date }
    }

    private fun getFromDate(date: Date, whatToGet: Int): Int {
        return getCalendarInstance(date).get(whatToGet)
    }

    private fun isSameDay(date: Date): Boolean {
        val todayDate = Date()
        val propToGet = Calendar.DAY_OF_MONTH
        return getFromDate(todayDate, propToGet) == getFromDate(date, propToGet)
    }

    private fun isPreviousDay(date: Date): Boolean {
        val previousDate = Calendar.getInstance().apply {
            time = Date()
            add(Calendar.DATE, -1)
        }.time
        return getFromDate(previousDate, PROP_DAY) == getFromDate(date, PROP_DAY)
    }

    private fun isSameWeek(date: Date): Boolean {
        return getFromDate(Date(), PROP_WEEK) == getFromDate(date, PROP_WEEK)
    }

    private fun isSameMonth(date: Date): Boolean {
        return getFromDate(Date(), PROP_MONTH) == getFromDate(date, PROP_MONTH)
    }

    private fun isSameYear(date: Date): Boolean {
        return getFromDate(Date(), PROP_YEAR) == getFromDate(date, PROP_YEAR)
    }

    override fun getViewType(): Int {
        return VIEW_TYPE_HEADER
    }

    companion object {
        const val HEADER_TODAY = "today"
        const val HEADER_YESTERDAY = "yesterday"
        const val HEADER_THIS_WEEK = "this week"
        const val HEADER_THIS_MONTH = "this month"
        const val HEADER_OTHER_FORMAT = "MMM, YYYY"

        const val PROP_DAY = Calendar.DAY_OF_YEAR
        const val PROP_WEEK = Calendar.WEEK_OF_YEAR
        const val PROP_MONTH = Calendar.MONTH
        const val PROP_YEAR = Calendar.YEAR
    }

}