package id.altea.care.core.data.repositoryimpl

import id.altea.care.core.data.network.api.AppointmentApi
import id.altea.care.core.data.request.*
import id.altea.care.core.data.response.AppointmentDataResponse
import id.altea.care.core.data.response.PaymentResponse
import id.altea.care.core.data.response.Response
import id.altea.care.core.domain.model.*
import id.altea.care.core.domain.repository.AppointmentRepository
import io.reactivex.Single

class AppointmentRepositoryImpl(private val appointmentApi: AppointmentApi) :
    AppointmentRepository {

    override fun createAppoiment(appointmentRequest: AppointmentRequest): Single<Appointment> {
        return appointmentApi.createAppointment(appointmentRequest).map { it.data.toAppointment() }
    }

    override fun appointmentRoom(appoimentId: Int): Single<AppointmentRoom> {
        return appointmentApi.appointmentRoom(appoimentId).map { it.data.toRoom() }
    }

    override fun getOnGoingAppointment(appointmentListRequest: AppointmentListRequest): Single<List<AppointmentData>> {
        return appointmentApi.getOnGoingAppointment(appointmentListRequest).map {
            it.data.map { AppointmentDataResponse.toAppointmentData(it) }
        }
    }

    override fun getHistoryAppointment(appointmentListRequest: AppointmentListRequest): Single<List<AppointmentData>> {
        return appointmentApi.getHistoryAppointment(appointmentListRequest).map {
            it.data.map { AppointmentDataResponse.toAppointmentData(it) }
        }
    }

    override fun getCancelAppointment(appointmentListRequest: AppointmentListRequest): Single<List<AppointmentData>> {
        return appointmentApi.getCancelAppointment(appointmentListRequest).map {
            it.data.map { AppointmentDataResponse.toAppointmentData(it) }
        }
    }

    override fun doPayment(paymentRequest: PaymentRequest): Single<Payment> {
        return appointmentApi.doPayment(paymentRequest).map {
            PaymentResponse.mapToPayment(it.data)
        }
    }

    override fun addDocument(addDocumentRequest: AddDocumentRequest): Single<AddDocumentData> {
        return  appointmentApi.addDocument(addDocumentRequest).map {
            it.data.toAddDocumentData()
        }
    }

    override fun removeDocument(removeDocumentRequest: RemoveDocumentRequest): Single<Boolean> {
        return appointmentApi.removeDocument(removeDocumentRequest).map { it.status }
    }

    override fun setCancelReasonOrder(cancelReasonOrderRequest: CancelReasonOrderRequest): Single<Boolean> {
        return appointmentApi.setCancelReasonOrder(cancelReasonOrderRequest).map { it.status }
    }

    override fun appointmentRoomProvider(appointmentId: Int): Single<AppointmentProvider> {
        return  appointmentApi.appointmentRoomProvider(appointmentId).map { it.data.toAppointmentProvider() }
    }

    override fun getConsultationDetail(consultationId: Int): Single<ConsultationDetail> {
       return appointmentApi.getConsultationDetail(consultationId).map {
           it.data.toConsultationDetail(it.data)
       }
    }

}