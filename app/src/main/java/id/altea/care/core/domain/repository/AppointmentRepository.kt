package id.altea.care.core.domain.repository

import id.altea.care.core.data.request.*
import id.altea.care.core.data.response.AppointmentProviderResponse
import id.altea.care.core.data.response.Response
import id.altea.care.core.domain.model.*
import io.reactivex.Single
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface AppointmentRepository {
    fun createAppoiment(appointmentRequest: AppointmentRequest): Single<Appointment>

    fun appointmentRoom(appoimentId: Int): Single<AppointmentRoom>

    fun getOnGoingAppointment(appointmentListRequest: AppointmentListRequest): Single<List<AppointmentData>>

    fun getHistoryAppointment(appointmentListRequest: AppointmentListRequest): Single<List<AppointmentData>>

    fun getCancelAppointment(appointmentListRequest: AppointmentListRequest): Single<List<AppointmentData>>

    fun getConsultationDetail(consultationId: Int): Single<ConsultationDetail>

    fun doPayment(paymentRequest: PaymentRequest): Single<Payment>

    fun addDocument(addDocumentRequest: AddDocumentRequest) : Single<AddDocumentData>

    fun removeDocument(removeDocumentRequest: RemoveDocumentRequest) : Single<Boolean>

    fun setCancelReasonOrder(cancelReasonOrderRequest: CancelReasonOrderRequest) : Single<Boolean>


    fun appointmentRoomProvider(appointmentId: Int) : Single<AppointmentProvider>

}