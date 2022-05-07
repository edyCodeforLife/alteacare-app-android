package id.altea.care.core.domain.cache

import id.altea.care.core.domain.model.Profile

interface UserCache : BaseCache {
    // TODO tolong ganti logic ini kalo ada waktu senggang
    fun saveUserProfile(profile: Profile)
    fun getUserProfile(): Profile?
}
