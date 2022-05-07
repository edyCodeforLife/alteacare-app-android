package id.altea.care.core.data.response

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import id.altea.care.core.domain.model.Hospital

@Keep
data class AppointmentHospitalResponse(
    @SerializedName("id") val id: String?,
    @SerializedName("logo") val logo: String?,
    @SerializedName("name") val name: String?
) {
    companion object {
        fun toHospital(appointmentHospital: AppointmentHospitalResponse?): Hospital {
            return Hospital(
                appointmentHospital?.logo,
                appointmentHospital?.id,
                appointmentHospital?.name
            )
        }
    }
}
