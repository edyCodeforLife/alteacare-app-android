package id.altea.care.core.data.response


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class PhotoIdCard(
    @SerializedName("formats")
    val formats: FormatResponse?,
    @SerializedName("id")
    val id: String?,
    @SerializedName("url")
    val url: String?,
    @SerializedName("size_formatted")
    val sizeFormatted : String?,
    val mimeType : String?,
)