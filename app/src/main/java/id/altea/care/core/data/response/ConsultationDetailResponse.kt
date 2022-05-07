package id.altea.care.core.data.response

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import id.altea.care.core.domain.model.ConsulMedicalResume
import id.altea.care.core.domain.model.ConsultationDetail
import id.altea.care.core.domain.model.Invoice
import id.altea.care.core.domain.model.TransactionConsulDetail

@Keep
data class ConsultationDetailResponse(
    @SerializedName("id") val id: String?,
    @SerializedName("order_code") val orderCode: String?,
    @SerializedName("refference_appointment_id") val refferenceAppointmentId: String?,
    @SerializedName("status") val status: String?,
    @SerializedName("status_detail") val statusDetail: AppointmentStatusDetailResponse?,
    @SerializedName("symptom_note") val symptomNote: String?,
    @SerializedName("schedule") val schedule: AppointmentScheduleResponse?,
    @SerializedName("doctor") val doctor: AppointmentDoctorResponse?,
    @SerializedName("user") val user: ConsultationUserResponse?,
    @SerializedName("patient") val patient : PatientConsulDetailResponse?,
    @SerializedName("total_price") val totalPrice: Int?,
    @SerializedName("transaction") val transactionConsulDetailResponse: TransactionConsulDetailResponse?,
    @SerializedName("medical_resume") val medicalResume: ConsulMedicalResumeResponse?,
    @SerializedName("medical_document") val medicalDocument: List<ConsultationMedicalDocumentResponse>?,
    @SerializedName("fees") val fee: List<ConsultationFeeResponse>?,
    @SerializedName("consultation_method") val consultationMethod: String?,
    @SerializedName("invoice") val invoice: InvoiceResponse?
) {
    fun toConsultationDetail(consultationDetail: ConsultationDetailResponse?): ConsultationDetail {
        return ConsultationDetail(
            consultationDetail?.id,
            consultationDetail?.orderCode,
            consultationDetail?.refferenceAppointmentId,
            consultationDetail?.status,
            AppointmentStatusDetailResponse.mapToDomain(consultationDetail?.statusDetail),
            consultationDetail?.symptomNote,
            AppointmentScheduleResponse.toDoctorSchedule(consultationDetail?.schedule),
            AppointmentDoctorResponse.toConsultationDoctor(consultationDetail?.doctor),
            ConsultationUserResponse.toConsultationUser(consultationDetail?.user),
            PatientConsulDetailResponse.toPatientConsulDetailModel(consultationDetail?.patient),
            consultationDetail?.totalPrice,
            TransactionConsulDetail(
                transactionConsulDetailResponse?.bank,
                transactionConsulDetailResponse?.expiredAt,
                transactionConsulDetailResponse?.paymentUrl,
                transactionConsulDetailResponse?.provider,
                transactionConsulDetailResponse?.refId,
                transactionConsulDetailResponse?.total,
                transactionConsulDetailResponse?.type,
                transactionConsulDetailResponse?.vaNumber,
                transactionConsulDetailResponse?.detail?.toDetailTransaction()
            )
            ,
            ConsulMedicalResume(
                medicalResume?.additionalResume,
                medicalResume?.consultation,
                medicalResume?.diagnosis,
                medicalResume?.drugResume,
                medicalResume?.fileResponses?.map { it.toMedicalResumeFiles() },
                medicalResume?.id,
                medicalResume?.symptom,
                medicalResume?.notes
            ),
            consultationDetail?.medicalDocument?.map {
                ConsultationMedicalDocumentResponse.toConsultationMedicalDocument(it)
            }.orEmpty(),
            consultationDetail?.fee?.map { ConsultationFeeResponse.toConsultationFee(it) }.orEmpty(),
            consultationDetail?.consultationMethod,
            AppointmentPatientResponse.mapToDomain(consultationDetail?.patient),
            Invoice(invoice?.originalName,invoice?.url)
        )
    }

}
