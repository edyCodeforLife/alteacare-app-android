package id.altea.care.core.data.response


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep
import id.altea.care.core.domain.model.InformationCentral

@Keep
data class InformationCentralResponse(
    @SerializedName("content")
    val contentResponse: ContentResponse?,
    @SerializedName("content_id")
    val contentId: String?,
    @SerializedName("title")
    val title: String?,
    @SerializedName("type")
    val type: String?
){
    fun toInformationCentral() : InformationCentral{
        return InformationCentral(
            contentResponse?.toContent(),
            contentId,
            title,
            type
        )
    }
}