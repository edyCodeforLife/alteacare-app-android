package id.altea.care.core.domain.usecase

import id.altea.care.core.domain.model.AppointmentProvider
import id.altea.care.core.domain.repository.AppointmentRepository
import io.reactivex.Single
import javax.inject.Inject

class GetAppointmentRoomProviderUseCase @Inject constructor(private val appointmentRepository: AppointmentRepository) {

    fun getAppointmetRoomProvider(appointmentId : Int) : Single<AppointmentProvider>{
        return appointmentRepository.appointmentRoomProvider(appointmentId)
    }
}