package id.altea.care.core.data.request

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class PasswordVerifyRequest(
    @SerializedName("username") val username: String,
    @SerializedName("otp") val otp: String
)