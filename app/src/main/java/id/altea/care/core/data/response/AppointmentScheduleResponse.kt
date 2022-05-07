package id.altea.care.core.data.response

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import id.altea.care.core.domain.model.DoctorSchedule

@Keep
data class AppointmentScheduleResponse(
    @SerializedName("code") val code: String?,
    val date: String?,
    @SerializedName("id") val id: Int?,
    @SerializedName("time_end") val timeEnd: String?,
    @SerializedName("time_start") val timeStart: String?
) {
    companion object {
        fun toDoctorSchedule(appointmentSchedule: AppointmentScheduleResponse?): DoctorSchedule {
            return DoctorSchedule(
                appointmentSchedule?.code,
                appointmentSchedule?.date,
                appointmentSchedule?.timeEnd,
                appointmentSchedule?.timeStart,
                if (appointmentSchedule?.timeStart != null && appointmentSchedule.timeEnd != null)
                    if (appointmentSchedule.timeStart.length > 5 && appointmentSchedule.timeEnd.length > 5) {
                        appointmentSchedule.timeStart.substring(0, 5)
                            .plus(" - ${appointmentSchedule.timeEnd.substring(0, 5)}")
                    } else {
                        "${appointmentSchedule.timeStart}-${appointmentSchedule.timeEnd}"
                    }
                else null,
                appointmentSchedule?.id
            )
        }
    }
}
