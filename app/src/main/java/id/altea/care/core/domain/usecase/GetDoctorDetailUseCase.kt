package id.altea.care.core.domain.usecase

import id.altea.care.core.domain.cache.AuthCache
import id.altea.care.core.domain.model.Doctor
import id.altea.care.core.domain.repository.DataRepository
import id.altea.care.core.helper.util.Constant
import io.reactivex.Single
import javax.inject.Inject

class GetDoctorDetailUseCase @Inject constructor(
    private val dataRepository: DataRepository,
    private val authCache: AuthCache
) {

    fun doRequestDoctorDetail(doctorId: String): Single<Doctor> {
        return dataRepository.getDoctorDetail(
            doctorId
        )
    }

}
