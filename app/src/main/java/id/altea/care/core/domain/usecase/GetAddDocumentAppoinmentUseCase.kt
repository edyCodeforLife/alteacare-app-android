package id.altea.care.core.domain.usecase

import id.altea.care.core.data.repositoryimpl.AppointmentRepositoryImpl
import id.altea.care.core.data.request.AddDocumentRequest
import id.altea.care.core.domain.model.AddDocumentData
import id.altea.care.core.domain.repository.AppointmentRepository
import id.altea.care.core.domain.repository.DataRepository
import io.reactivex.Single
import javax.inject.Inject

class GetAddDocumentAppoinmentUseCase @Inject constructor(val appointmentRespository : AppointmentRepository) {

    fun addDocumentAppointment(appointmentId : Int?,documentId : String?) : Single<AddDocumentData> {
        return appointmentRespository.addDocument(AddDocumentRequest(appointmentId ?:0 ,documentId ?: ""))
    }

}