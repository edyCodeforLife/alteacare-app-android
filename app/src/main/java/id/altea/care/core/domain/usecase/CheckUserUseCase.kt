package id.altea.care.core.domain.usecase

import id.altea.care.core.data.request.AddCheckUserRequest
import id.altea.care.core.domain.model.CheckUser
import id.altea.care.core.domain.repository.UserRepository
import io.reactivex.Single
import javax.inject.Inject

class CheckUserUseCase @Inject constructor(private val userRepository: UserRepository) {
    fun checkUser(params: AddCheckUserRequest): Single<CheckUser> {
        return userRepository.checkUser(params)
    }
}