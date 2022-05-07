package id.altea.care.core.data.response

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import id.altea.care.core.domain.model.ConsultationMedicalDocument
import id.altea.care.core.domain.model.ConsultationMedicalResume

@Keep
data class ConsultationMedicalDocumentResponse(
    @SerializedName("id") val id: String?,
    @SerializedName("url") val url: String?,
    @SerializedName("original_name") val originalName: String?,
    @SerializedName("size") val size: String?,
    @SerializedName("upload_by_user") val uploadByUser: Int?,
    @SerializedName("date_raw") val dateRaw : String?,
    val date : String?
) {
    companion object{
        fun toConsultationMedicalDocument(consultationMedicalDocument: ConsultationMedicalDocumentResponse?): ConsultationMedicalDocument {
            return ConsultationMedicalDocument(
                consultationMedicalDocument?.id,
                consultationMedicalDocument?.url,
                consultationMedicalDocument?.originalName,
                consultationMedicalDocument?.size,
                consultationMedicalDocument?.uploadByUser,
                consultationMedicalDocument?.dateRaw,
                consultationMedicalDocument?.date
            )
        }
    }
}