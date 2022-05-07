package id.altea.care.core.domain.usecase

import id.altea.care.core.data.request.ForgotPasswordRequest
import id.altea.care.core.data.request.PasswordVerifyRequest
import id.altea.care.core.domain.cache.AuthCache
import id.altea.care.core.domain.model.Auth
import id.altea.care.core.domain.model.ForgotPassword
import id.altea.care.core.domain.repository.UserRepository
import io.reactivex.Single
import javax.inject.Inject

class ForgotPasswordUseCase @Inject constructor(
    private val repository: UserRepository,
    private val authCache: AuthCache
) {

    /**
     * before call api. it will be remove token on authcache and put on the tempToken var
     * so AltecareInterceptor` will not generate auth header
     * after call, set the token again using tempToken
     */
    fun doRequestEmailOrSmsOtp(username: String): Single<ForgotPassword> {
        var tempToken = ""
        return repository.requestEmailOrSmsOtpForgotPassword(ForgotPasswordRequest(username))
            .doOnSubscribe {
                tempToken = authCache.getToken()
                authCache.setToken("")
            }
            .doAfterTerminate { authCache.setToken(tempToken) }
    }

    fun doValidateTokenOtp(username: String, token: String): Single<Auth> {
        var tempToken = ""
        return repository.validationEmailOtpForgotPassword(
            PasswordVerifyRequest(username, token)
        )
            .doOnSubscribe {
                tempToken = authCache.getToken()
                authCache.setToken("")
            }
            .doAfterTerminate { authCache.setToken(tempToken) }
    }

}
