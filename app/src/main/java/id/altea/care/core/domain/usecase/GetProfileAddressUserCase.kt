package id.altea.care.core.domain.usecase

import id.altea.care.core.domain.model.PageAddress
import id.altea.care.core.domain.repository.UserRepository
import io.reactivex.Single
import javax.inject.Inject

class GetProfileAddressUserCase @Inject constructor(private val repository: UserRepository) {

    fun getProfileAddress(page: Int): Single<PageAddress> {
        return repository.getProfileAddress(page)
    }

}
