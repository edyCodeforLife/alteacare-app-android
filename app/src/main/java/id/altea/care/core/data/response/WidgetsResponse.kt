package id.altea.care.core.data.response

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class WidgetsResponse(
    val android: Android?,
    val id: String?,
    val title: String?,
    @SerializedName("need_login")
    val needLogin : Boolean,
    val weight: Int?
) {
    data class Android(
        @SerializedName("deeplink_type")
        val deeplinkType: String?,
        @SerializedName("deeplink_url")
        val deeplinkUrl: String?,
        val icon: Icon?
    ) {
        data class Icon(
            val formats: Formats?,
            val id: String?,
            val mimeType: String?,
            val name: String?,
            val size: Double?,
            @SerializedName("size_formatted")
            val sizeFormatted: String?,
            val url: String?
        ) {
            data class Formats(
                val large: String?,
                val medium: String?,
                val small: String?,
                val thumbnail: String?
            )
        }
    }
}