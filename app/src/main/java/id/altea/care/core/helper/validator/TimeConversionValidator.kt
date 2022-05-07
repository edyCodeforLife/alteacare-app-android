package id.altea.care.core.helper.validator

import id.altea.care.core.ext.formatDate
import id.altea.care.core.ext.toCalendar
import id.altea.care.core.ext.toDateFormat
import id.altea.care.core.helper.util.Constant.DEFAULT_DATE_TIME_FORMAT
import id.altea.care.core.helper.util.Constant.TIME_FORMAT
import id.altea.care.core.helper.util.Constant.TIME_ZONE_ID_JAKARTA
import timber.log.Timber
import java.util.*

class TimeConversionValidator {
    companion object {
        fun istTimeConversion(startTime: String, endTime: String): Boolean {
            val userDate = Calendar.getInstance().time
            val currentTimeZone = TimeZone.getDefault()
            val jakartaTimeZone = TimeZone.getTimeZone(TIME_ZONE_ID_JAKARTA)

            val jakartaStart = startTime
                .toDateFormat(dateFormat = TIME_FORMAT,timeZone = jakartaTimeZone)
            val jakartaEnd = endTime
                .toDateFormat(dateFormat = TIME_FORMAT,timeZone = jakartaTimeZone)

            val endUser = userDate
                .formatDate(dateFormat = TIME_FORMAT,timeZone = jakartaTimeZone)
            val jakartaStartFormat = jakartaStart
                .formatDate(dateFormat = TIME_FORMAT,timeZone = jakartaTimeZone)
            val jakartaEndFormat = jakartaEnd
                .formatDate(dateFormat = TIME_FORMAT,timeZone = jakartaTimeZone)

            val endUserHour = endUser
                .toDateFormat(dateFormat = TIME_FORMAT,timeZone = jakartaTimeZone)
                .toCalendar()
                .get(Calendar.HOUR_OF_DAY)

            val jakartaStartHour = jakartaStartFormat
                .toDateFormat(dateFormat = TIME_FORMAT,timeZone = currentTimeZone)
                .toCalendar()
                .get(Calendar.HOUR_OF_DAY)
            val jakartaEndHour = jakartaEndFormat
                .toDateFormat(dateFormat = TIME_FORMAT,timeZone = currentTimeZone)
                .toCalendar()
                .get(Calendar.HOUR_OF_DAY)

            val result = endUserHour in jakartaStartHour..jakartaEndHour
            Timber.d("user date nya $endUserHour | di jakartaStart $jakartaStartHour || di Jakarta End $jakartaEndHour")
            return result
        }

        fun String.countDownCalculate(): Long {
            val userDate = Calendar.getInstance()
            val currentTimeZone = userDate.timeZone
            val jakartaTimeZone = TimeZone.getTimeZone(TIME_ZONE_ID_JAKARTA)

            // convert to date with jakarta timezone
            val jakartaFormat = toDateFormat(dateFormat = DEFAULT_DATE_TIME_FORMAT,timeZone = jakartaTimeZone)

            // get string date jakarta timezone
            val format = jakartaFormat.formatDate(dateFormat = DEFAULT_DATE_TIME_FORMAT,timeZone = jakartaTimeZone)

            // change current date with jakarta timezone
            val endDate = format.toDateFormat(dateFormat = DEFAULT_DATE_TIME_FORMAT,timeZone = currentTimeZone)

            Timber.d("asli nya ${jakartaFormat.toCalendar().get(Calendar.HOUR_OF_DAY)} | di jakarta ${endDate.toCalendar().get(Calendar.HOUR_OF_DAY)}")
            return endDate.time - userDate.timeInMillis
        }
    }
}