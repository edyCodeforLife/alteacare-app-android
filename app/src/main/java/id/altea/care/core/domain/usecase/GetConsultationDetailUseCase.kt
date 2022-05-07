package id.altea.care.core.domain.usecase

import id.altea.care.core.domain.model.ConsultationDetail
import id.altea.care.core.domain.repository.AppointmentRepository
import io.reactivex.Single
import javax.inject.Inject


class GetConsultationDetailUseCase @Inject constructor(private val appointmentRepository: AppointmentRepository) {
    fun doRequestConsultationDetaiL(consultationId: Int): Single<ConsultationDetail> {
        return appointmentRepository.getConsultationDetail(consultationId)
    }
}