package id.altea.care.core.domain.model



data class AppointmentRoom (
    val enableResponse: Enable?,
    val expiredAt: String?,
    val roomCode: String?,
    val token: String?,
    val identity : String?
)

data class Enable(
    val chat: Boolean?,
    val video: Boolean?,
    val voice: Boolean?
)