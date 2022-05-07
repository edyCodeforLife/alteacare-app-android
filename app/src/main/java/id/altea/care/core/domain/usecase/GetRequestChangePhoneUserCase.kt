package id.altea.care.core.domain.usecase


import id.altea.care.core.data.request.PhoneNumberRequest
import id.altea.care.core.domain.repository.UserRepository
import io.reactivex.Single
import javax.inject.Inject

class GetRequestChangePhoneUserCase @Inject constructor(val userRepository: UserRepository){

    fun requestChangePhone(phone : String): Single<Boolean> {
        return userRepository.requestChangePhoneNumber(PhoneNumberRequest(phone))
    }

}