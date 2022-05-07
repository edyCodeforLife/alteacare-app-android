package id.altea.care.core.domain.usecase

import id.altea.care.core.data.request.PasswordRequest
import id.altea.care.core.domain.repository.UserRepository
import io.reactivex.Single
import javax.inject.Inject

class CheckPasswordUseCase @Inject constructor(val userRepository: UserRepository) {

    fun checkPassword(password : String?) : Single<Boolean>{
        return userRepository.requestCheckPassword(PasswordRequest(password))
    }
}