package id.altea.care.core.domain.usecase

import id.altea.care.core.data.request.EmailOtpValidationRequest
import id.altea.care.core.domain.repository.UserRepository
import io.reactivex.Single
import javax.inject.Inject

class GetChangeEmailOtpUseCase @Inject constructor(private val repository: UserRepository) {

    fun changeEmailOtp(email: String,otp : String): Single<Boolean> {
        return repository.changeEmailOtp(EmailOtpValidationRequest(email,otp))
    }

}