package id.altea.care.core.domain.usecase

import id.altea.care.core.domain.model.Specialization
import id.altea.care.core.domain.repository.DataRepository
import id.altea.care.core.helper.util.ConstantQueryParam.QUERY_IS_POPULAR
import io.reactivex.Single
import javax.inject.Inject

class GetSpecialistUseCase @Inject constructor(private val repository: DataRepository) {

    data class Param(val isPopular: Boolean)

    fun getSpecialist(param: Param? = null): Single<List<Specialization>> {
        val queryParam = mutableMapOf<String, String>()
        if (param != null) {
            queryParam[QUERY_IS_POPULAR] = if (param.isPopular) "YES" else "NO"
        }
        return repository.getSpecialist(queryParam)
    }

    fun searchSpecialist(queryParam : Map<String,String>) : Single<List<Specialization>>{
        return repository.getSpecialist(queryParam)
    }

    fun getSpecialist() : Single<List<Specialization>>{
        return getSpecialist(null)
    }

}
