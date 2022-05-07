package id.altea.care.core.data.response


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep
import id.altea.care.core.domain.model.Twilio

@Keep
data class TwilioResponse(
    @SerializedName("enable")
    val enable: EnableResponse?,
    @SerializedName("expired_at")
    val expiredAt: String?,
    @SerializedName("identity")
    val identity: String?,
    @SerializedName("room_code")
    val roomCode: String?,
    @SerializedName("token")
    val token: String?,
    @SerializedName("user_id")
    val userId: String?
){
    fun toTwillio() : Twilio{
        return Twilio(
            enable?.toEnable(),
            expiredAt,
            identity,
            roomCode,
            token,
            userId
        )
    }
}