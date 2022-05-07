package id.altea.care.core.domain.usecase

import id.altea.care.core.data.request.EmailOtpValidationRequest
import id.altea.care.core.domain.cache.AuthCache
import id.altea.care.core.domain.model.Auth
import id.altea.care.core.domain.repository.UserRepository
import io.reactivex.Single
import javax.inject.Inject

class RegisterValidateEmailOtpUseCase
@Inject constructor(private val repository: UserRepository, private val authCache: AuthCache) {

    /**
     * before call api. it will be remove token on authcache and put on the tempToken var
     * so AltecareInterceptor will not generate auth header
     * after call, set the token again using tempToken
     */
    fun doValidate(email: String, otp: String, token: String): Single<Auth> {
        var tempToken = ""
        return repository.requestValidateOtpRegister(
            EmailOtpValidationRequest(email, otp),
            "Bearer $token"
        )
            .doOnSubscribe {
                tempToken = authCache.getToken()
                authCache.setToken("")
            }
            .doAfterTerminate { authCache.setToken(tempToken) }
    }
}
