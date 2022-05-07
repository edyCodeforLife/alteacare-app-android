package id.altea.care.core.domain.usecase

import id.altea.care.core.domain.cache.ShareScreenCache
import javax.inject.Inject

class CreateShareUseCase @Inject constructor(private val shareScreenCache: ShareScreenCache) {
    fun setShareScreen(flag : Boolean) {
        shareScreenCache.run {
            setShare(flag)
        }
    }
}