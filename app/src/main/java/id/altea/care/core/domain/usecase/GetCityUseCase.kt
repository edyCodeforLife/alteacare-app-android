package id.altea.care.core.domain.usecase

import id.altea.care.core.domain.model.City
import id.altea.care.core.domain.repository.DataRepository
import io.reactivex.Single
import javax.inject.Inject

class GetCityUseCase @Inject constructor(private val repository: DataRepository) {

    fun getCity(provinceId: String): Single<List<City>> {
        return repository.getCity(provinceId)
    }

}
