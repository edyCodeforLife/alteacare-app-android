package id.altea.care.core.domain.usecase

import com.moengage.core.internal.model.database.QueryParams
import id.altea.care.core.domain.model.Doctor
import id.altea.care.core.domain.model.ResultMeta
import id.altea.care.core.domain.repository.DataRepository
import id.altea.care.core.helper.util.Constant
import id.altea.care.core.helper.util.ConstantQueryParam
import io.reactivex.Single
import javax.inject.Inject

class GetDoctorSpecialistUseCase @Inject constructor(
    private val dataRepository: DataRepository
) {

    fun getDoctorSpecialist(
        queryParam: HashMap<String, Any>
    ): Single<ResultMeta<List<Doctor>>> {
        return dataRepository.getDoctorSpecialistFilter(
            queryParam.apply {
                put(ConstantQueryParam.QUERY_LIMIT, 500)
                val querySort = this[ConstantQueryParam.QUERY_SORT]
                if(querySort == null) {
                    put(ConstantQueryParam.QUERY_SORT, "price:DESC")
                }
            }

        )
    }

}
