package id.altea.care.core.domain.usecase

import id.altea.care.core.data.request.RemoveDocumentRequest
import id.altea.care.core.domain.repository.AppointmentRepository
import io.reactivex.Single
import javax.inject.Inject

class RemoveDocumentUseCase @Inject constructor(val appointmentRepository: AppointmentRepository) {

    fun removeDocument(appointmentId: Int?,documentId: Int?) : Single<Boolean>{
        return appointmentRepository.removeDocument(RemoveDocumentRequest(appointmentId,documentId))
    }
}