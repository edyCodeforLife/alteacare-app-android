package id.altea.care.core.data.response

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import id.altea.care.core.domain.model.DoctorSchedule

@Keep
class DoctorScheduleResponse(
    @SerializedName("code")
    val code: String?,

    val date: String?,
    @SerializedName("end_time")
    val endTime: String?,
    @SerializedName("start_time")
    val startTime: String?
) {
    companion object {
        fun toDoctorSchedule(data: DoctorScheduleResponse): DoctorSchedule {
            val startTime = if (data.startTime != null && data.startTime.length >= 4)
                data.startTime.substring(0, 5) else data.startTime
            val endTime = if (data.endTime != null && data.endTime.length >= 4)
                data.endTime.substring(0, 5) else data.endTime
            return DoctorSchedule(
                data.code,
                data.date,
                endTime,
                startTime,
                if (startTime != null && endTime != null)
                    "${startTime}-${endTime}" else null
            )
        }
    }
}
