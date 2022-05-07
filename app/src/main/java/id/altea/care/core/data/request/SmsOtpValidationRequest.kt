package id.altea.care.core.data.request

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class SmsOtpValidationRequest(
    @SerializedName("phone") val phone: String,
    @SerializedName("otp") val otp: String
)