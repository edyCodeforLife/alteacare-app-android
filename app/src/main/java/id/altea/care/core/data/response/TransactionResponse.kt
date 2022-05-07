package id.altea.care.core.data.response

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import id.altea.care.core.domain.model.Transaction

@Keep
data class TransactionResponse(
    @SerializedName("expiredAt") val expiredAt: String?,
    @SerializedName("payment_url") val redirectUrl: String?,
    @SerializedName("ref_id") val refId: String?,
    @SerializedName("token") val token: String?,
    @SerializedName("total") val total: Int?,
    @SerializedName("type") val type: String?
) {
    companion object {
        fun mapToTransaction(data: TransactionResponse?): Transaction {
            return Transaction(
                data?.expiredAt,
                data?.redirectUrl,
                data?.refId,
                data?.token,
                data?.total,
                data?.type
            )
        }
    }
}
