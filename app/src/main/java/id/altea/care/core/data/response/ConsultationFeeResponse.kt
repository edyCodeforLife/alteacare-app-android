package id.altea.care.core.data.response

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import id.altea.care.core.domain.model.ConsultationFee


@Keep
data class ConsultationFeeResponse(
    @SerializedName("amount") val amount: Long?,
    @SerializedName("id") val id: Int?,
    @SerializedName("label") val label: String?,
    @SerializedName("status") val status: String?,
    @SerializedName("type") val type: String?,
    @SerializedName("data_id") val dataId: Int?,
    @SerializedName("category") val category: String?
) {
    companion object {
        fun toConsultationFee(consultationFee: ConsultationFeeResponse?): ConsultationFee {
            return ConsultationFee(
                consultationFee?.amount,
                consultationFee?.id,
                consultationFee?.label,
                consultationFee?.status,
                consultationFee?.type,
                consultationFee?.dataId,
                consultationFee?.category
            )
        }
    }
}
