package id.altea.care.core.data.response

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import id.altea.care.core.domain.model.ConsultationDoctor
import id.altea.care.core.domain.model.Doctor

@Keep
data class AppointmentDoctorResponse(
    @SerializedName("hospital") val hospital: AppointmentHospitalResponse?,
    @SerializedName("id") val id: String?,
    @SerializedName("name") val name: String?,
    @SerializedName("photo") val photo: IconResponse?,
    @SerializedName("specialist") val specialist: AppointmentSpecialistResponse?
) {

    companion object {
        fun toDoctor(appointmentDoctor: AppointmentDoctorResponse?): Doctor {
            return Doctor(
                null,
                null,
                appointmentDoctor?.id,
                null,
                listOf(AppointmentHospitalResponse.toHospital(appointmentDoctor?.hospital)),
                appointmentDoctor?.name,
                if (appointmentDoctor?.photo?.formats?.small == null)
                    appointmentDoctor?.photo?.url else appointmentDoctor.photo.formats.small,
                null,
                null,
                AppointmentSpecialistResponse.toSpecialization(appointmentDoctor?.specialist)
            )
        }

        fun toConsultationDoctor(appointmentDoctor: AppointmentDoctorResponse?): ConsultationDoctor {
            return ConsultationDoctor(
                appointmentDoctor?.id,
                appointmentDoctor?.name,
                if (appointmentDoctor?.photo?.formats?.small == null)
                    appointmentDoctor?.photo?.url else appointmentDoctor.photo.formats.small,
                AppointmentSpecialistResponse.toSpecialization(appointmentDoctor?.specialist),
                AppointmentHospitalResponse.toHospital(appointmentDoctor?.hospital)
            )
        }
    }
}
