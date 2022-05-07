package id.altea.care.core.data.response


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class CardPhotoResponse(
    @SerializedName("formats")
    val formats: FormatImage?,
    @SerializedName("id")
    val id: String?,
    @SerializedName("size_formatted")
    val sizeFormatted: String?,
    @SerializedName("url")
    val url: String?
)