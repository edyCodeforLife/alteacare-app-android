package id.altea.care.core.domain.usecase

import id.altea.care.core.data.request.CancelReasonOrderRequest
import id.altea.care.core.domain.repository.AppointmentRepository
import io.reactivex.Single
import javax.inject.Inject

class CancelReasonOrderUseCase @Inject constructor(private val appointmentRepository: AppointmentRepository) {

    fun setCancelReasonOrder(appointmentId : Int,note : String) : Single<Boolean>{
        return appointmentRepository.setCancelReasonOrder(CancelReasonOrderRequest(appointmentId,note))
    }

}