package id.altea.care.core.domain.usecase

import id.altea.care.core.domain.cache.AnalyticsCache
import timber.log.Timber
import javax.inject.Inject

class AnalyticsUseCase @Inject constructor(private val analyticsCache: AnalyticsCache) {

    fun checkTrackingHasBeenSent(invoke: () -> Unit) {
        if (!analyticsCache.getTrackingHasBeenSent()) {
            analyticsCache.setTrackingHasBeenSent(true)
            invoke()
            Timber.d("send tracking")
        } else {
            Timber.d("tracking has been sent")
        }
    }
}