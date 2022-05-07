package id.altea.care.core.domain.model


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep


data class ImageBannerThumbnail(
    val formats: Formats?,
    val mimeType: String?,
    val sizeFormatted: String?,
    val uploadedBy: UploadedBy?,
    val url: String?
)