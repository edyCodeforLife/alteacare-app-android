package id.altea.care.core.domain.usecase

import id.altea.care.core.domain.model.SubDistrict
import id.altea.care.core.domain.repository.DataRepository
import io.reactivex.Single
import javax.inject.Inject

class GetSubDistrictUseCase @Inject constructor(private val repository: DataRepository) {

    fun getSubDistrict(districtId: String): Single<List<SubDistrict>> {
        return repository.getSubDistrict(districtId)
    }

}
