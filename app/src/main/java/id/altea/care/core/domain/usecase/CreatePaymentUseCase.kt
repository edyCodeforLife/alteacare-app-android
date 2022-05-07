package id.altea.care.core.domain.usecase

import id.altea.care.core.data.request.PaymentRequest
import id.altea.care.core.domain.model.Payment
import id.altea.care.core.domain.repository.AppointmentRepository
import io.reactivex.Single
import javax.inject.Inject

class CreatePaymentUseCase @Inject constructor(private val appointmentRepository: AppointmentRepository) {

    fun doPayment(appointmentId: Int, paymentMethod: String,voucherCode : String?): Single<Payment> {
        return appointmentRepository.doPayment(PaymentRequest(appointmentId, paymentMethod,voucherCode))
    }
}
