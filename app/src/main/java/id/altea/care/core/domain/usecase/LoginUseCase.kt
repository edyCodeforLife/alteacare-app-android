package id.altea.care.core.domain.usecase

import id.altea.care.core.data.request.LoginRequest
import id.altea.care.core.data.response.ProfileResponse
import id.altea.care.core.domain.cache.AccountCache
import id.altea.care.core.domain.cache.AuthCache
import id.altea.care.core.domain.cache.UserCache
import id.altea.care.core.domain.model.Account
import id.altea.care.core.domain.repository.UserRepository
import id.altea.care.core.exception.UndefinedUsernameType
import id.altea.care.view.login.model.LoginHadleModel
import io.reactivex.Single
import javax.inject.Inject

/**
 * Created by trileksono on 02/03/21.
 */
class LoginUseCase @Inject constructor(
    private val repository: UserRepository,
    private val authCache: AuthCache,
    private val accountCache: AccountCache,
    private val userCache: UserCache
) {

    fun doLogin(email: String, password: String): Single<LoginHadleModel> {
        return repository.login(LoginRequest(email, password))
            .flatMap { auth ->
                when (auth.isVerified) {
                    false -> {
                        Single.just(when (auth.usernameType) {
                            "EMAIL" -> LoginHadleModel.EmailUnverified(auth.accessToken.orEmpty(), auth.username.orEmpty())
                            "PHONE" -> LoginHadleModel.PhoneUnverified(auth.accessToken.orEmpty(), auth.username.orEmpty())
                            else -> throw UndefinedUsernameType("Username type tidak terdefinisi")
                        })
                    }
                    else -> {
                        // set token for request profile me
                        authCache.setToken(auth.accessToken.orEmpty())
                        authCache.setRefreshToken(auth.refreshToken.orEmpty())
                        authCache.setDeviceID(auth.deviceId.orEmpty())

                        repository.getProfile()
                            .map {
                                LoginHadleModel.VerifiedUser(
                                    auth.accessToken.orEmpty(),
                                    ProfileResponse.mapToProfile(it.data, auth),
                                )
                            }
                            .doAfterSuccess {
                                // after success get profile, set cache user is loggedin & user id
                                authCache.setIsLoggedIn(true)
                                authCache.setUserId(it.profile.id.orEmpty())

                                accountCache.setAccountNotActive()

                                // set cache account active
                                accountCache.addAccount(
                                    Account(
                                        it.profile.id.orEmpty(),
                                        it.profile.email.orEmpty(),
                                        true,
                                        "${it.profile.firstName} ${it.profile.lastName}",
                                        it.profile.userDetails?.avatar?.url.orEmpty(),
                                        auth.accessToken.orEmpty(),
                                        auth.refreshToken.orEmpty(),
                                        true
                                    )
                                )

                                // set cache for profile
                                userCache.saveUserProfile(it.profile)
                            }
                    }
                }
            }
    }
}
