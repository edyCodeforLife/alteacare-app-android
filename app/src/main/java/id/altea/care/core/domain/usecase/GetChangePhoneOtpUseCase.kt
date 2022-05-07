package id.altea.care.core.domain.usecase

import id.altea.care.core.data.request.PhoneNumberOtpValidationRequest
import id.altea.care.core.data.request.PhoneNumberRequest
import id.altea.care.core.domain.repository.UserRepository
import io.reactivex.Single
import javax.inject.Inject

class GetChangePhoneOtpUseCase @Inject constructor(val userRepository: UserRepository) {

    fun requestChangePhoneOtp(phone : String,otp : String): Single<Boolean> {
        return userRepository.changePhoneNumberOtp(PhoneNumberOtpValidationRequest(phone,otp))
    }

}