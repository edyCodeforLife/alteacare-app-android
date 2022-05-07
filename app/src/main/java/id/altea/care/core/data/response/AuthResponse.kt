package id.altea.care.core.data.response

import com.google.gson.annotations.SerializedName
import id.altea.care.core.domain.model.Auth

data class AuthResponse(
    @SerializedName("login_at")
    val loginAt: String? = null,
    @SerializedName("access_token")
    val accessToken: String? = null,
    @SerializedName("device_id")
    val deviceId: String? = null,
    @SerializedName("is_email_verified")
    val isEmailVerified: Boolean? = null,
    @SerializedName("is_phone_verified")
    val isPhoneVerified: Boolean? = null,
    @SerializedName("is_registered")
    val isRegistered: Boolean? = null,
    @SerializedName("is_verified")
    val isVerified: Boolean? = null,
    @SerializedName("refresh_token")
    val refreshToken: String? = null,
    @SerializedName("username_type")
    val usernameType: String? = null,
    @SerializedName("username")
    val username: String? = null // this value will be phone number or email depend on usernameType

) {

    fun toAuth(): Auth {
        return Auth(
            accessToken,
            deviceId,
            isEmailVerified,
            isPhoneVerified,
            isRegistered,
            isVerified,
            refreshToken,
            usernameType,
            username,
            loginAt
        )
    }
}
