package id.altea.care.core.data.response

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import id.altea.care.core.domain.model.AppointmentData

@Keep
data class AppointmentDataResponse(
    @SerializedName("created") val created: String?,
    @SerializedName("doctor") val doctor: AppointmentDoctorResponse?,
    @SerializedName("id") val id: Int?,
    @SerializedName("order_code") val orderCode: String?,
    @SerializedName("schedule") val schedule: AppointmentScheduleResponse?,
    @SerializedName("status") val status: String?,
    @SerializedName("expired_at") val expiredAt: String?,
    @SerializedName("in_operational_hour") val inOperationalHour: Boolean?,
    @SerializedName("user") val user: AppointmentUserResponse?,
    @SerializedName("status_detail") val statusDetail: AppointmentStatusDetailResponse?,
    @SerializedName("transaction") val transaction: TransactionResponse?,
    @SerializedName("patient") val appointmentPatientResponse: PatientConsulDetailResponse? = null
) {
    companion object {
        fun toAppointmentData(data: AppointmentDataResponse?): AppointmentData {
            return AppointmentData(
                created = data?.created,
                doctor = AppointmentDoctorResponse.toDoctor(data?.doctor),
                id = data?.id,
                orderCode = data?.orderCode,
                schedule = AppointmentScheduleResponse.toDoctorSchedule(data?.schedule),
                status = data?.status,
                expiredAt = data?.expiredAt,
                inOperationalHour = data?.inOperationalHour,
                user = AppointmentUserResponse.toProfile(data?.user),
                statusDetail = AppointmentStatusDetailResponse.mapToDomain(data?.statusDetail),
                transaction = TransactionResponse.mapToTransaction(data?.transaction),
                patient = AppointmentPatientResponse.mapToDomain(data?.appointmentPatientResponse)
            )
        }
    }
}
