package id.altea.care.core.data.request


import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class AppointmentRequest(
    @SerializedName("doctor_id")
    val doctorId: String?,
    @SerializedName("patient_id")
    val patientId: String?,
    @SerializedName("consultation_method")
    val consultationMethod: String?,
    @SerializedName("schedules")
    val schedules: List<ScheduleRequest>?,
    @SerializedName("next_step")
    val nextStep: String? = null,
    @SerializedName("refference_appointment_id")
    val refferenceAppointmentId: String? = null

) {
    companion object {
        const val NEXT_STEP_MA = "ASK_MA"
        const val NEXT_STEP_DIRECT = "DIRECT"
    }
}