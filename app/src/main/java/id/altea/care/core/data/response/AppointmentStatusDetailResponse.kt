package id.altea.care.core.data.response

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import id.altea.care.core.domain.model.AppointmentStatusDetail

@Keep
data class AppointmentStatusDetailResponse(
    @SerializedName("bg_color") val bgColor: String?,
    @SerializedName("label") val label: String?,
    @SerializedName("text_color") val textColor: String?
) {
    companion object {
        fun mapToDomain(statusDetail: AppointmentStatusDetailResponse?): AppointmentStatusDetail {
            return AppointmentStatusDetail(
                statusDetail?.bgColor,
                statusDetail?.label,
                statusDetail?.textColor
            )
        }
    }
}
