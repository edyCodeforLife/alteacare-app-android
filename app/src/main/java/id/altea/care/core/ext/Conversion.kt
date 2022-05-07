package id.altea.care.core.ext

import id.altea.care.view.home.HomeFragment
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

fun String?.toNewFormat(
    oldFormat: String? = "dd/MM/yyyy" /*default date format from server*/,
    newFormat: String? = "dd/MM/yyyy" /*default date format to UI*/,
    isLocale: Boolean = false
): String {
    if (this.isNullOrEmpty()) return ""
    val dateTimeMillis =
        SimpleDateFormat(oldFormat, isLocaleDate(isLocale)).parse(this)?.time ?: 0L
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = dateTimeMillis
    return SimpleDateFormat(newFormat, isLocaleDate(isLocale)).format(calendar.time)
}

fun String.isDateBeforeToday(
    dateFormat: String = "dd/MM/yyyy",
    isLocale: Boolean = false
): Boolean {
    if (this.isEmpty()) return false
    val calendar = Calendar.getInstance(isLocaleDate(isLocale))
    val sdf = SimpleDateFormat(dateFormat, isLocaleDate(isLocale))
    val todayFormat = sdf.format(calendar.time)
    return sdf.parse(this)?.before(sdf.parse(todayFormat)) ?: false
}

fun Date?.formatDate(
    dateFormat: String = "dd/MM/yyyy",
    timeZone: TimeZone? = null
): String {
    if (this == null) return ""
    return if (timeZone != null){
        val formatter = SimpleDateFormat(dateFormat, Locale.getDefault())
        formatter.timeZone = timeZone
        formatter.format(this)
    }else {
        val sdf = SimpleDateFormat(dateFormat, isLocaleDate(true))
        sdf.format(this)
    }
}

// todo is locale must be change if app support multi language
fun isLocaleDate(
    isLocale: Boolean
): Locale {
    return if (isLocale) Locale("id", "ID")
    else Locale("en", "EN")
}

fun String.getLongFromDate(format: String = "dd/MM/yyyy"): Long {
    return if (this.isEmpty()) {
        0L
    } else {
        SimpleDateFormat(format, isLocaleDate(false)).parse(this)?.time ?: 0L
    }
}


fun Long.getDateFromLong(format: String = "dd/MM/yyyy", isLocale: Boolean = false): String {
    val sdf = SimpleDateFormat(format, isLocaleDate(isLocale))
    return sdf.format(this)
}

fun Long?.toRupiahFormat(): String {
    val localeID = Locale("in", "ID")
    val formatRupiah: NumberFormat = NumberFormat.getCurrencyInstance(localeID)
    val value = this ?: 0L
    return formatRupiah.format(value)
        .replaceAfter(",", "")
        .replace(",", "")
        .replace("Rp", "Rp ")
}

fun Date.getDateTomorrow(format: String = "dd/MM/yyyy"): String {
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = this.time
    calendar.add(Calendar.DATE, 1)
    return calendar.timeInMillis.getDateFromLong(format = format)
}

fun Date.getDateNextWeek(format: String = "dd/MM/yyyy"): String {
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = this.time
    calendar.add(Calendar.DATE, 7)
    return calendar.timeInMillis.getDateFromLong(format = format)
}

fun Date.getDateNextMonth(format: String = "dd/MM/yyyy"): String {
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = this.time
    calendar.add(Calendar.MONTH, 1)
    return calendar.timeInMillis.getDateFromLong(format = format)
}

fun Long.convertSizeFile(): String {
    val kilo = 1024;
    val mega = kilo * kilo
    val kb: Double = this.toDouble() / kilo
    val mb: Double = kb / kilo

    return when {
        this < kilo -> {
            "$this Bytes"
        }
        this in kilo until mega -> {
            String.format("%.2f", kb) + " KB"
        }
        else -> {
            String.format("%.2f", mb) + " MB"
        }
    }
}

fun customDateFormat(): String {
    return Date().toString().toNewFormat("EEE MMM dd HH:mm:ss zzz yyyy", "dd-EEE-yyyy hh:mm:ss aa")
}

fun String?.analyticValidatedLength() : String {
    return if (this?.isNotEmpty() == true && this.length > 100) this.substring(0,100) else this.orEmpty()
}

 fun String.toDateFormat(dateFormat: String = "dd/MM/yyyy", timeZone: TimeZone): Date {
    val parser = SimpleDateFormat(dateFormat, Locale.getDefault())
    parser.timeZone = timeZone
    return parser.parse(this)
}

fun Date.toCalendar(): Calendar {
    return Calendar.getInstance().apply {
        time = this@toCalendar
    }
}