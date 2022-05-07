package id.altea.care.core.data.local.cachesource

import android.content.SharedPreferences
import id.altea.care.core.domain.cache.AnalyticsCache
import id.altea.care.core.ext.update

class AnalyticsCacheSource(private val sharedPreferences: SharedPreferences) : AnalyticsCache {
    override fun setTrackingHasBeenSent(hasSentInstall: Boolean) {
        sharedPreferences.update(hasSentInstall to "HAS_SENT_INSTALL")
    }

    override fun getTrackingHasBeenSent(): Boolean {
        return sharedPreferences.getBoolean("HAS_SENT_INSTALL", false)
    }
}