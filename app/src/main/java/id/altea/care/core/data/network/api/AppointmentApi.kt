package id.altea.care.core.data.network.api

import id.altea.care.core.data.request.*
import id.altea.care.core.data.response.*
import id.altea.care.core.domain.model.AppointmentProvider
import io.reactivex.Single
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface AppointmentApi {
    @POST("/appointment/v3/consultation")
    fun createAppointment(@Body appointmentRequest: AppointmentRequest): Single<Response<AppointmentResponse>>

    @POST("/appointment/add-document")
    fun addDocument(@Body addDocumentRequest: AddDocumentRequest) : Single<Response<AddDocumentResponse>>

    @POST("/appointment/remove-document")
    fun removeDocument(@Body removeDocumentRequest: RemoveDocumentRequest) : Single<Response<Any>>

    @POST("/appointment/v1/consultation/cancel-by-user")
    fun setCancelReasonOrder(@Body cancelReasonOrderRequest: CancelReasonOrderRequest) : Single<Response<Any>>

    @GET("/appointment/detail/{appoimentId}/room")
    fun appointmentRoom(@Path("appoimentId") appoimentId: Int): Single<Response<AppointmentRoomResponse>>

    @POST("/appointment/on-going")
    fun getOnGoingAppointment(
        @Body appointmentListRequest: AppointmentListRequest
    ): Single<Response<List<AppointmentDataResponse>>>

    @POST("/appointment/history")
    fun getHistoryAppointment(
        @Body appointmentListRequest: AppointmentListRequest
    ): Single<Response<List<AppointmentDataResponse>>>

    @POST("/appointment/cancel")
    fun getCancelAppointment(
        @Body appointmentListRequest: AppointmentListRequest
    ): Single<Response<List<AppointmentDataResponse>>>

    @GET("/appointment/detail/{consultationId}")
    fun getConsultationDetail(@Path("consultationId") consultationId: Int
    ): Single<Response<ConsultationDetailResponse>>

    @POST("/appointment/pay")
    fun doPayment(@Body paymentRequest: PaymentRequest): Single<Response<PaymentResponse>>

    @GET("/appointment/v2/room/{appointmentId}")
    fun appointmentRoomProvider(@Path("appointmentId") appointmentId: Int) : Single<Response<AppointmentProviderResponse>>
}