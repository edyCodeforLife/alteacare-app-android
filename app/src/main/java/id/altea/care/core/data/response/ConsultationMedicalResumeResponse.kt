package id.altea.care.core.data.response

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import id.altea.care.core.domain.model.ConsultationMedicalResume


@Keep
data class ConsultationMedicalResumeResponse(
    @SerializedName("id") val id: String?,
    @SerializedName("url") val url: String?,
    @SerializedName("originalName") val originalName: String?,
    @SerializedName("size") val size: String?,
    @SerializedName("uploadByUser") val uploadByUser: Int?,
) {
    companion object {
        fun toConsultationMedicalResume(consultationMedicalResume: ConsultationMedicalResumeResponse?): ConsultationMedicalResume {
            return ConsultationMedicalResume(
                consultationMedicalResume?.id,
                consultationMedicalResume?.url,
                consultationMedicalResume?.originalName,
                consultationMedicalResume?.size,
                consultationMedicalResume?.uploadByUser
            )
        }
    }
}