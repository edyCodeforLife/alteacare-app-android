package id.altea.care.core.domain.model


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep


data class Twilio(
    val enable: Enable?,
    val expiredAt: String?,
    val identity: String?,
    val roomCode: String?,
    val token: String?,
    val userId: String?
)