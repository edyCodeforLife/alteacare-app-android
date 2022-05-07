package id.altea.care.core.domain.model

import android.os.Parcelable
import id.altea.care.view.common.enums.TypeAppointment
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MyAppointment(
    val id: Int,
    val orderCode: String,
    val doctorName: String,
    val specialization: String,
    val hospitalName: String,
    val scheduleDate: String,
    val scheduleStart: String,
    val scheduleEnd: String,
    val doctorImage: String,
    val hospitalImage: String = "",
    val status: TypeAppointment?,
    val expiredAt: String?,
    val inOperationalHour: Boolean?,
    val statusDetail: AppointmentStatusDetail?,
    val transaction: Transaction?,
    val patient: AppointmentPatient?,
    val doctorId : String? = ""
) : Parcelable