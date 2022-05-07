package id.altea.care.core.data.response

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import id.altea.care.core.domain.model.FormatImage

@Keep
data class FormatResponse(
    @SerializedName("large") val large: String?,
    @SerializedName("medium") val medium: String?,
    @SerializedName("small") val small: String?,
    @SerializedName("thumbnail") val thumbnail: String?
) {
    companion object {
        fun toFormatImage(data: FormatResponse?): FormatImage {
            return FormatImage(data?.thumbnail, data?.large, data?.medium, data?.small)
        }
    }

}
