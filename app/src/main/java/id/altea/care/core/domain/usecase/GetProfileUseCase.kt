package id.altea.care.core.domain.usecase

import id.altea.care.core.data.response.ProfileResponse
import id.altea.care.core.domain.cache.AuthCache
import id.altea.care.core.domain.cache.UserCache
import id.altea.care.core.domain.model.Profile
import id.altea.care.core.domain.model.Result
import id.altea.care.core.domain.repository.UserRepository
import id.altea.care.core.helper.util.Constant
import io.reactivex.Single
import javax.inject.Inject

class GetProfileUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val userCache: UserCache,
    private val authCache: AuthCache
) {
    var tempAuth : String = ""

    fun getProfile(isFromCache: Boolean = false): Single<Result<Profile>> {
        return Single.just(isFromCache).flatMap {
            if (it) {
                Single.fromCallable { userCache.getUserProfile() }
                    .map { cache -> Result(true, "", cache) }
            } else {
                userRepository.getProfile().map { profileResponse ->
                    ProfileResponse.mapToProfile(profileResponse.data)
                    Result(
                        status = profileResponse.status,
                        message = profileResponse.message,
                        data = ProfileResponse.mapToProfile(profileResponse.data)
                    )
                }.doAfterSuccess { userCache.saveUserProfile(it.data) }
            }
        }
    }

    fun getProfileFromRegister(token: String?): Single<Result<Profile>> {
        return userRepository.getProfileFromRegister("${Constant.BEARER} $token")
            .doOnSubscribe {
            tempAuth = authCache.getToken()
            authCache.setToken("")
        }.doOnTerminate {
            authCache.setToken(tempAuth)
        }.map { profileResponse ->
                ProfileResponse.mapToProfile(profileResponse.data)
                Result(
                    status = profileResponse.status,
                    message = profileResponse.message,
                    data = ProfileResponse.mapToProfile(profileResponse.data)
                )
            }
        }

}