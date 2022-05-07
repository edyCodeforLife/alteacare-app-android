package id.altea.care.core.data.local.cachesource

import android.content.SharedPreferences
import id.altea.care.core.domain.cache.ShareScreenCache
import id.altea.care.core.ext.update

class ShareScreenSource(private val preferences: SharedPreferences) : ShareScreenCache {

    companion object {
        const val FLAG_SHARE = "ShareScreenCache.FLAG"
    }
    override fun setShare(flag: Boolean) {
       preferences.update(flag to FLAG_SHARE)
    }

    override fun getShare(): Boolean {
       return preferences.getBoolean(FLAG_SHARE,false)
    }

    override fun invalidate() {
        preferences.run {
            update(false to ShareScreenSource.FLAG_SHARE)
        }
    }
}