package id.altea.care.core.data.request

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class ChangePasswordRequest(
    @SerializedName("password") val password: String,
    @SerializedName("password_confirmation") val passwordConfirm: String
)
