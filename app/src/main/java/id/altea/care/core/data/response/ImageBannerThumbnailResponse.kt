package id.altea.care.core.data.response


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class ImageBannerThumbnailResponse(
    @SerializedName("formats")
    val formats: FormatResponse?,
    @SerializedName("mimeType")
    val mimeType: String?,
    @SerializedName("size_formatted")
    val sizeFormatted: String?,
    @SerializedName("uploadedBy")
    val uploadedByResponse: UploadedByResponse?,
    @SerializedName("url")
    val url: String?
)