package id.altea.care.core.domain.usecase

import id.altea.care.core.data.response.ProfileResponse
import id.altea.care.core.domain.cache.UserCache
import id.altea.care.core.domain.model.Profile
import id.altea.care.core.domain.repository.UserRepository
import io.reactivex.Single
import javax.inject.Inject

class GetProfileCacheUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val userCache: UserCache
) {

    fun getUserProfile(): Single<Profile> {
        val profileCache = userCache.getUserProfile()
        return if (profileCache != null) {
            Single.just(profileCache)
        } else {
            userRepository.getProfile()
                .map {
                    it.data.let { profileResponse ->
                        ProfileResponse.mapToProfile(profileResponse)
                    }
                }
                .doAfterSuccess { userCache.saveUserProfile(it) }
        }
    }

}