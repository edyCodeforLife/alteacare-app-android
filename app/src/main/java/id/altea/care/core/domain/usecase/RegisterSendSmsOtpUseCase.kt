package id.altea.care.core.domain.usecase

import id.altea.care.core.data.request.SmsRequest
import id.altea.care.core.domain.cache.AuthCache
import id.altea.care.core.domain.repository.UserRepository
import io.reactivex.Single
import javax.inject.Inject

class RegisterSendSmsOtpUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val authCache: AuthCache
) {

    fun doRequestSmsOtpRegistration(phone: String, token: String): Single<Boolean> {

        var temporaryToken = ""

        return userRepository.doRequestSmsOtpRegistration(SmsRequest(phone), token = "Bearer $token")
            .doOnSubscribe {
                temporaryToken = authCache.getToken()
                authCache.setToken("")
            }.doAfterTerminate {
                authCache.setToken(temporaryToken)
            }
    }
}