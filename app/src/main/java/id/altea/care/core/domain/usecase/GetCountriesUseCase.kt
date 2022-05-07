package id.altea.care.core.domain.usecase

import id.altea.care.core.domain.model.Country
import id.altea.care.core.domain.repository.DataRepository
import io.reactivex.Single
import javax.inject.Inject

class GetCountriesUseCase @Inject constructor(private val repository: DataRepository) {

    fun getCountries(queryParam : Map<String,String>) : Single<List<Country>>{
        return repository.getCountries(queryParam)
    }
}