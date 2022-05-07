package id.altea.care.core.domain.usecase

import id.altea.care.core.data.request.RegisterRequest
import id.altea.care.core.domain.model.Auth
import id.altea.care.core.domain.model.RegisterInfo
import id.altea.care.core.domain.repository.UserRepository
import id.altea.care.core.ext.toNewFormat
import io.reactivex.Single
import javax.inject.Inject

class RegistrationUseCase @Inject constructor(private val repository: UserRepository) {

    fun doRegister(param: RegisterInfo): Single<Auth> {
        return repository.doRegister(
            RegisterRequest(
                param.birthDate.toNewFormat(newFormat = "yyyy-MM-dd"),
                param.email!!,
                param.firstName,
                param.cityOfBirth!!,
                param.resultFilter?.id!!,
                param.gender!!.name,
                if (param.lastName.isEmpty()) param.firstName else param.lastName,
                param.password!!,
                param.passwordConfirm!!,
                param.phoneNumber!!
            )
        )
    }
}
