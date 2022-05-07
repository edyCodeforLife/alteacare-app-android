package id.altea.care.core.domain.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class DoctorSchedule(
    val code: String?,
    val date: String?,
    val endTime: String?,
    val startTime: String?,
    val schedule: String?,
    val id: Int? = null // this value will be mapping only for schedule appointment
) : Parcelable {

    val endDateTimeOfTeleconsultation: String
        get() = "$date $endTime"

}