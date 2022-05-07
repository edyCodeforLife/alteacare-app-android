package id.altea.care.core.data.response


import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import id.altea.care.core.domain.model.Appointment

@Keep
data class AppointmentResponse(
    @SerializedName("appointment_id")
    val appointmentId : Int?,
    @SerializedName("appointment_menthod")
    val appointmentMenthod: String?,
    @SerializedName("order_code")
    val orderCode: String?,
    @SerializedName("room_code")
    val roomCode: String?,
    @SerializedName("status")
    val status: String?,
    @SerializedName("expired_at")
    val expiredAt: String?,
    @SerializedName("in_operational_hour")
    val inOperationalHour : Boolean?
){
    fun toAppointment() : Appointment {
        return Appointment(
            appointmentId = appointmentId,
            appointmentMenthod = appointmentMenthod,
            orderCode = orderCode,
            roomCode = roomCode,
            status = status,
            expiredAt = expiredAt,
            inOperationalHour = inOperationalHour
        )
    }
}