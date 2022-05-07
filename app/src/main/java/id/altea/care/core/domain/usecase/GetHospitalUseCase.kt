package id.altea.care.core.domain.usecase

import id.altea.care.core.domain.model.HospitalResult
import id.altea.care.core.domain.repository.DataRepository
import io.reactivex.Single
import javax.inject.Inject

class GetHospitalUseCase @Inject constructor(private val repository: DataRepository) {

    fun getHospital() : Single<List<HospitalResult>> {
        return searchHospital(mutableMapOf())
    }

    fun searchHospital(queryMap : Map<String,String>) : Single<List<HospitalResult>>{
        return repository.searchHospital(queryMap)
    }

}