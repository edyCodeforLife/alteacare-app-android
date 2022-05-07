package id.altea.care.core.domain.usecase

import id.altea.care.core.domain.model.District
import id.altea.care.core.domain.repository.DataRepository
import io.reactivex.Single
import javax.inject.Inject

class GetDistrictUseCase @Inject constructor(private val repository: DataRepository) {

    fun getDistrict(cityId: String): Single<List<District>> {
        return repository.getDistrict(cityId)
    }

}
