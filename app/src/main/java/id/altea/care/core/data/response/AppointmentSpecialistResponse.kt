package id.altea.care.core.data.response

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import id.altea.care.core.domain.model.Specialization

@Keep
data class AppointmentSpecialistResponse(
    @SerializedName("child") val child: AppointmentSpecialistChildResponse?,
    @SerializedName("id") val id: String?,
    @SerializedName("name") val name: String?
) {
    companion object {
        fun toSpecialization(appointmentSpecialist: AppointmentSpecialistResponse?): Specialization {
            return Specialization(
                null,
                null,
                appointmentSpecialist?.name,
                false,
                appointmentSpecialist?.id,
                listOf(
                    AppointmentSpecialistChildResponse.toSpecializationChild(
                        appointmentSpecialist?.child
                    )
                )
            )
        }
    }
}
