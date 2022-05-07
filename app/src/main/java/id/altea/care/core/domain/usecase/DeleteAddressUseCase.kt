package id.altea.care.core.domain.usecase

import id.altea.care.core.domain.repository.UserRepository
import io.reactivex.Completable
import javax.inject.Inject

class DeleteAddressUseCase @Inject constructor(private val repository: UserRepository) {

    fun deleteAddress(addressId: String): Completable {
        return repository.deleteAddress(addressId)
    }
}
