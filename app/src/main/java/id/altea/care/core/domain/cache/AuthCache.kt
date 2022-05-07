package id.altea.care.core.domain.cache

interface AuthCache : BaseCache {

    fun setToken(token: String)

    fun getToken(): String

    fun getIsLoggedIn(): Boolean

    fun setIsLoggedIn(isLogin: Boolean)

    fun setRefreshToken(refreshToken: String)

    fun getRefreshToken(): String

    fun setUserId(userId: String)

    fun getUserId(): String

    fun setDeviceID(deviceId : String)

    fun getDeviceId(): String
}

