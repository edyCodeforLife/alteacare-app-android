package id.altea.care.core.domain.usecase

import id.altea.care.core.data.request.AppointmentRequest
import id.altea.care.core.domain.model.Appointment
import id.altea.care.core.domain.model.AppointmentRoom
import id.altea.care.core.domain.repository.AppointmentRepository
import io.reactivex.Single
import javax.inject.Inject

class AppointmentUseCase @Inject constructor(private val repository: AppointmentRepository) {

    fun createAppoiment(appointmentRequest: AppointmentRequest): Single<Appointment> {
        return repository.createAppoiment(appointmentRequest)
    }

    fun appointmentRoom(appoimentId: Int): Single<AppointmentRoom> {
        return repository.appointmentRoom(appoimentId)
    }
}