package id.altea.care.core.domain.usecase

import id.altea.care.core.domain.model.DoctorSchedule
import id.altea.care.core.domain.repository.DataRepository
import io.reactivex.Single
import javax.inject.Inject

class GetDoctorScheduleUseCase @Inject constructor(private val repository: DataRepository) {

    fun getDoctorSchedule(doctorId: String, date: String): Single<List<DoctorSchedule>> {
        return repository.getDoctorSchedule(doctorId, date)
    }
}
