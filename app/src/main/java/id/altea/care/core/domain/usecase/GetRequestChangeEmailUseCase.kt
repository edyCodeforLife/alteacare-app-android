package id.altea.care.core.domain.usecase

import id.altea.care.core.data.request.EmailRequest
import id.altea.care.core.domain.model.General
import id.altea.care.core.domain.repository.UserRepository
import io.reactivex.Single
import javax.inject.Inject

class GetRequestChangeEmailUseCase @Inject constructor(private val repository: UserRepository) {

    fun requestChangeEmail(email: String): Single<Boolean> {
        return repository.requestChangeEmail(EmailRequest(email))
    }

}