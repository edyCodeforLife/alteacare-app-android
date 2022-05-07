package id.altea.care.core.data.response


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep
import id.altea.care.core.domain.model.AppointmentRoom


@Keep
data class AppointmentRoomResponse(
    @SerializedName("enable")
    val enableResponse: EnableResponse?,
    @SerializedName("expired_at")
    val expiredAt: String?,
    @SerializedName("room_code")
    val roomCode: String?,
    @SerializedName("token")
    val token: String?,
    @SerializedName("identity")
    val identity : String?
){
    fun toRoom() : AppointmentRoom{
        return AppointmentRoom(
            enableResponse?.toEnable(),
            expiredAt,
            roomCode,
            token,
            identity
        )
    }
}