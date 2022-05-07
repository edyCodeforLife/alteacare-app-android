package id.altea.care.core.domain.cache

interface ShareScreenCache : BaseCache{
    fun setShare(flag : Boolean)

    fun getShare(): Boolean
}