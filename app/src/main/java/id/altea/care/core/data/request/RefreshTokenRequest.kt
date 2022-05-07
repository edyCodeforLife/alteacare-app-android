package id.altea.care.core.data.request

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class RefreshTokenRequest(
    @SerializedName("refresh_token") val refreshToken: String
)
