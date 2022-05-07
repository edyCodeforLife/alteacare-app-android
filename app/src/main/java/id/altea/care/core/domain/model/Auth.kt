package id.altea.care.core.domain.model

data class Auth(
    val accessToken: String? = null,
    val deviceId: String? = null,
    val isEmailVerified: Boolean? = null,
    val isPhoneVerified: Boolean? = null,
    val isRegistered: Boolean? = null,
    val isVerified: Boolean? = null,
    val refreshToken: String? = null,
    val usernameType: String? = null,
    val username: String? = null,
    val loginAt: String? = null
)