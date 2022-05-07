package id.altea.care.core.domain.cache

interface AnalyticsCache {
    fun setTrackingHasBeenSent(hasSentInstall: Boolean)
    fun getTrackingHasBeenSent(): Boolean
}