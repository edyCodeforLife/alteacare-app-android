package id.altea.care.core.data.response


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep
import id.altea.care.core.domain.model.FilesMedicalResume

@Keep
data class FileResponse(
    @SerializedName("formats")
    val formats: FormatResponse?,
    @SerializedName("id")
    val id: String?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("size")
    val size: Double?,
    @SerializedName("url")
    val url: String?
){
    fun toMedicalResumeFiles() : FilesMedicalResume {
        return FilesMedicalResume(
            formats?.small,
            id,
            name,
            size,
            url
        )

    }
}