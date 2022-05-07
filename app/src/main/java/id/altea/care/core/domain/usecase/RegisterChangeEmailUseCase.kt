package id.altea.care.core.domain.usecase

import id.altea.care.core.data.request.EmailRequest
import id.altea.care.core.domain.cache.AuthCache
import id.altea.care.core.domain.repository.UserRepository
import io.reactivex.Single
import javax.inject.Inject

class RegisterChangeEmailUseCase @Inject constructor(
    private val repository: UserRepository,
    private val authCache: AuthCache
) {

    /**
     * before call api. it will be remove token on authcache and put on the tempToken var
     * so AltecareInterceptor will not generate auth header
     * after call, set the token again using tempToken
     */

    fun doChangeEmailRegister(email: String, token: String): Single<Boolean> {
        var tempToken = ""
        return repository.requestChangeEmailRegister(EmailRequest(email), "Bearer $token")
            .doOnSubscribe {
                tempToken = authCache.getToken()
                authCache.setToken("")
            }
            .doAfterTerminate { authCache.setToken(tempToken) }
    }

}