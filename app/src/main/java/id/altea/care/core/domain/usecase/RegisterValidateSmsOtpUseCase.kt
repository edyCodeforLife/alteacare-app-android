package id.altea.care.core.domain.usecase

import id.altea.care.core.data.request.SmsOtpValidationRequest
import id.altea.care.core.domain.cache.AuthCache
import id.altea.care.core.domain.model.Auth
import id.altea.care.core.domain.repository.UserRepository
import io.reactivex.Single
import javax.inject.Inject

class RegisterValidateSmsOtpUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val authCache: AuthCache
) {

    fun doValidationSmsOtpRegistration(phone: String, otp: String, token: String): Single<Auth> {

        var temporaryToken = ""

        return userRepository.doValidationSmsOtpRegistration(SmsOtpValidationRequest(phone, otp), token = "Bearer $token")
            .doOnSubscribe {
                temporaryToken = authCache.getToken()
                authCache.setToken("")
            }.doAfterTerminate {
                authCache.setToken(temporaryToken)
            }
    }

}