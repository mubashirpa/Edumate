package app.edumate.core.utils

import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.todayIn

object DateTimeUtils {
    private val systemTimeZone = TimeZone.currentSystemDefault()

    fun isPast(instant: Instant): Boolean {
        val currentInstant = Clock.System.now()
        return instant < currentInstant
    }

    fun isToday(date: LocalDate): Boolean {
        val today = Clock.System.todayIn(systemTimeZone)
        return date == today
    }

    fun isThisYear(date: LocalDate): Boolean {
        val today = Clock.System.todayIn(systemTimeZone)
        return date.year == today.year
    }

    fun getRelativeDateStatus(date: LocalDate): RelativeDate {
        val now = Clock.System.now()
        val today = now.toLocalDateTime(systemTimeZone).date
        val tomorrow =
            now.plus(1, DateTimeUnit.DAY, systemTimeZone).toLocalDateTime(systemTimeZone).date
        val yesterday =
            now.minus(1, DateTimeUnit.DAY, systemTimeZone).toLocalDateTime(systemTimeZone).date

        return when (date) {
            today -> RelativeDate.TODAY
            tomorrow -> RelativeDate.TOMORROW
            yesterday -> RelativeDate.YESTERDAY
            else -> RelativeDate.OTHER
        }
    }
}

enum class RelativeDate {
    TODAY,
    TOMORROW,
    YESTERDAY,
    OTHER,
}
