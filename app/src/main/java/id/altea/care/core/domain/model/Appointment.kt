package id.altea.care.core.domain.model

data class Appointment(
    val appointmentId : Int?,
    val appointmentMenthod: String?,
    val orderCode: String?,
    val roomCode: String?,
    val status: String?,
    val expiredAt: String?,
    val inOperationalHour : Boolean?
)