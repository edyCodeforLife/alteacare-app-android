package id.altea.care.core.domain.model

import android.os.Parcelable
import id.altea.care.core.ext.toNewFormat
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Consultation(
    val hospitalId: String,
    val iconRs: String,
    val nameRs: String,
    val idDoctor: String,
    val imgDoctor: String,
    val priceDoctor: String,
    val nameDoctor: String,
    val specialistDoctor: String,
    val codeSchedule: String,
    val dateSchedule: String,
    val startTime: String,
    val endTime: String,
    val priceDoctorDecimal: Long = 0,
    val priceStrikeDecimal: Long = 0,
    val priceStrike: String,
    val specializationId: String
) : Parcelable {

    val bookingDate: String
        get() = dateSchedule.toNewFormat(
            "yyyy-MM-dd",
            "EEEE, dd MMMM yyyy",
            true
        )

    val bookingHour: String
        get() = "$startTime-$endTime"

}