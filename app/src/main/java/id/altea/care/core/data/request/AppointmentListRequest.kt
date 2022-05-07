package id.altea.care.core.data.request


import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class AppointmentListRequest(
    @SerializedName("consultation_method") val consultationMethod: String? = null,
    @SerializedName("date_end") val dateEnd: String? = null,
    @SerializedName("date_start") val dateStart: String? = null,
    @SerializedName("doctor_id") val doctorId: String? = null,
    @SerializedName("hospital_id") val hospitalId: String? = null,
    @SerializedName("page") val page: Int? = null,
    @SerializedName("specialist_id") val specialistId: String? = null,
    @SerializedName("status") val status: List<Any?>? = null,
    @SerializedName("user_id") val userId: String? = null,
    @SerializedName("keyword") val keyword: String? = null,
    @SerializedName("sort_by") val sortBy: String? = null,
    @SerializedName("sort_type") val sortType: String? = null,
    @SerializedName("schedule_date_start") val scheduleDateStart: String? = null,
    @SerializedName("schedule_date_end") val scheduleDateEnd: String? = null,
    @SerializedName("patient_id") val patientId: String? = null
)
