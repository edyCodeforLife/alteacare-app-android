package id.altea.care.core.data.response


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class ConsulMedicalResumeResponse(
    @SerializedName("additional_resume")
    val additionalResume: String?,
    @SerializedName("consultation")
    val consultation: String?,
    @SerializedName("diagnosis")
    val diagnosis: String?,
    @SerializedName("drug_resume")
    val drugResume: String?,
    @SerializedName("files")
    val fileResponses: List<FileResponse>?,
    @SerializedName("id")
    val id: Int?,
    @SerializedName("symptom")
    val symptom: String?,
    @SerializedName("notes")
    val notes : String?
)