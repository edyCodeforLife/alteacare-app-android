package id.altea.care.core.domain.usecase

import id.altea.care.core.domain.repository.UserRepository
import io.reactivex.Completable
import javax.inject.Inject

class SetPrimaryAddressUseCase  @Inject constructor(private val repository: UserRepository) {

    fun setPrimaryAddress(idAddress: String) : Completable {
        return repository.setPrimaryAddress(idAddress)
    }

}
