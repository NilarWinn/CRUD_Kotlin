package com.example.bulletinboard.ui.util

import java.text.SimpleDateFormat
import java.util.*

class TimeUtil {

    companion object {

        const val DATE_FORMAT = "yyyy-MM-dd"
        const val DATE_FORMAT_MMDDYY = "dd/MM/yyyy"
        const val DATE_FORMAT_UTC = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"

        fun changeDateFormat(
            dateString: String?,
            currentFormat: String = DATE_FORMAT_UTC,
            formatToChange: String = DATE_FORMAT
        ): String? {
            if (dateString == null) return null
            return getDateString(
                getDate(dateString, currentFormat), formatToChange
            )
        }

        fun getDate(date: String, format: String): Date {
            return SimpleDateFormat(format, Locale.getDefault()).parse(date) as Date
        }

        fun getDate(day: Int, month: Int, year: Int): Date {
            return Calendar.getInstance(Locale.getDefault()).apply {
                set(year, month, day)
            }.time
        }

        fun getCurrentDate(): Date {
            return Calendar.getInstance().apply {
                clear(Calendar.HOUR)
                clear(Calendar.MINUTE)
                clear(Calendar.SECOND)
                clear(Calendar.MILLISECOND)
            }.time
        }

        fun getDateString(date: Date, dateFormat: String): String? {
            return SimpleDateFormat(dateFormat, Locale.getDefault()).format(date)
        }
    }
}