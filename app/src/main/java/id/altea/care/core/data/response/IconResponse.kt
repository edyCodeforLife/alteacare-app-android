package id.altea.care.core.data.response

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import id.altea.care.core.domain.model.IconImage

@Keep
data class IconResponse(
    @SerializedName("formats") val formats: FormatResponse?,
    @SerializedName("url") val url: String?
) {
    companion object {
        fun toIconImage(data: IconResponse?): IconImage {
            return IconImage(FormatResponse.toFormatImage(data?.formats), data?.url)
        }
    }
}
