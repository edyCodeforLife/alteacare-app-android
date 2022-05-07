package id.altea.care.core.data.local.cachesource

import android.content.SharedPreferences
import id.altea.care.core.domain.cache.OnboardingCache
import id.altea.care.core.ext.update

class OboardingCacheSource(private val preferences: SharedPreferences) : OnboardingCache {

    companion object {
        const val IS_ONBOARD_IN = "OboardingCache.IS_ONBOARD_IN"
    }
    override fun setIsOnBoarding(status: Boolean) {
        preferences.update(status to IS_ONBOARD_IN)
    }

    override fun getIsOnBoarding(): Boolean {
     return preferences.getBoolean(IS_ONBOARD_IN,false)
    }
}