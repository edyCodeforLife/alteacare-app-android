package id.altea.care.core.domain.usecase

import id.altea.care.core.data.request.AddressRequest
import id.altea.care.core.domain.repository.UserRepository
import io.reactivex.Completable
import javax.inject.Inject

class CreateAddressUseCase @Inject constructor(private val repository: UserRepository) {

    fun createAddress(
        street: String,
        countryId: String,
        provinceId: String,
        cityId: String,
        districtId: String,
        subDistrictId: String,
        rtRw: String
    ): Completable {
        return repository.createAddress(
            AddressRequest(cityId, countryId, districtId, provinceId, rtRw, street, subDistrictId)
        )
    }

}