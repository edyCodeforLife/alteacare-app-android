package id.altea.care.core.domain.usecase

import id.altea.care.core.domain.cache.AuthCache
import id.altea.care.core.domain.cache.ShareScreenCache
import javax.inject.Inject

class GetShareScreenUseCase @Inject constructor(private val shareScreenCache: ShareScreenCache) {
    fun getShareScreen(): Boolean = shareScreenCache.getShare()

    fun invalidate() = shareScreenCache.invalidate()
}