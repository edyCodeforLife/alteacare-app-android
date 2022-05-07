package id.altea.care.core.data.local.cachesource

import android.content.SharedPreferences
import id.altea.care.core.domain.cache.AuthCache
import id.altea.care.core.ext.update

class AuthCacheSource(private val preferences: SharedPreferences) : AuthCache {

    companion object {
        const val REFRESH_TOKEN = "AuthCache.REFRESH_TOKEN"
        const val TOKEN = "AuthCache.TOKEN"
        const val IS_LOGGED_IN = "AuthCache.IS_LOGGED_IN"
        const val USER_ID = "AuthCache.USER_ID"
        const val DEVICE_ID = "AuthCache.DEVICE_ID"
    }

    override fun setToken(token: String) {
        preferences.update(token to TOKEN)
    }

    override fun getToken(): String {
        return preferences.getString(TOKEN, "").orEmpty()
    }

    override fun getIsLoggedIn(): Boolean {
        return preferences.getBoolean(IS_LOGGED_IN, false)
    }

    override fun setIsLoggedIn(isLogin: Boolean) {
        preferences.update(isLogin to IS_LOGGED_IN)
    }

    override fun setRefreshToken(refreshToken: String) {
        preferences.update(refreshToken to REFRESH_TOKEN)
    }

    override fun getRefreshToken(): String {
        return preferences.getString(REFRESH_TOKEN, "").orEmpty()
    }

    override fun setUserId(userId: String) {
        preferences.update(userId to USER_ID)
    }

    override fun getUserId(): String {
        return preferences.getString(USER_ID, "").orEmpty()
    }

    override fun setDeviceID(deviceId: String) {
        preferences.update(deviceId to DEVICE_ID)
    }

    override fun getDeviceId(): String {
       return preferences.getString(DEVICE_ID,"").orEmpty()
    }

    override fun invalidate() {
        preferences.run {
            update("" to TOKEN)
            update("" to REFRESH_TOKEN)
            update(false to IS_LOGGED_IN)
            update("" to USER_ID)
            update("" to DEVICE_ID)
        }
    }
}
