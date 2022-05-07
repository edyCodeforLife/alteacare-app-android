package id.altea.care.core.domain.usecase

import id.altea.care.core.domain.model.Province
import id.altea.care.core.domain.repository.DataRepository
import io.reactivex.Single
import javax.inject.Inject

class GetProvinceUseCase @Inject constructor(private val repository: DataRepository) {

    fun getProvince(countryId: String): Single<List<Province>> {
        return repository.getProvince(countryId)
    }

}
