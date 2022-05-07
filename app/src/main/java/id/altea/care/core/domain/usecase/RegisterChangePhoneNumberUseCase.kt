package id.altea.care.core.domain.usecase

import id.altea.care.core.data.request.SmsRequest
import id.altea.care.core.domain.cache.AuthCache
import id.altea.care.core.domain.repository.UserRepository
import io.reactivex.Single
import javax.inject.Inject

class RegisterChangePhoneNumberUseCase @Inject constructor(
    private val repository: UserRepository,
    private val authCache: AuthCache
) {

    /**
     * before call api. it will be remove token on authcache and put on the tempToken var
     * so AltecareInterceptor will not generate auth header
     * after call, set the token again using tempToken
     */

    fun doChangePhoneNumberRegister(phoneNumber: String, token: String): Single<Boolean> {
        var tempToken = ""
        return repository.doChangePhoneNumberRegister(SmsRequest(phoneNumber), "Bearer $token")
            .doOnSubscribe {
                tempToken = authCache.getToken()
                authCache.setToken("")
            }.doAfterTerminate {
                authCache.setToken(tempToken)
            }
    }

}