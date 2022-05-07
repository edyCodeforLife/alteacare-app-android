package id.altea.care.core.data.local.cachesource

import android.content.SharedPreferences
import com.google.gson.Gson
import id.altea.care.core.domain.cache.UserCache
import id.altea.care.core.domain.model.Profile
import id.altea.care.core.ext.update

// TODO tolong ganti logic ini kalo ada waktu senggang
class UserCacheSource(private val preferences: SharedPreferences) : UserCache {

    private val gson = Gson()

    companion object {
        private const val USER_PROFILE = "UserCache.profile"
    }

    override fun saveUserProfile(profile: Profile) {
        preferences.update(gson.toJson(profile) to USER_PROFILE)
    }

    override fun getUserProfile(): Profile? {
        return try {
            gson.fromJson(preferences.getString(USER_PROFILE, null), Profile::class.java)
        } catch (ex: Exception) {
            null
        }
    }

    override fun invalidate() {
        preferences.update("" to USER_PROFILE)
    }

}
