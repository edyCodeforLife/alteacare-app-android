package id.altea.care.core.data.response


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class TransactionConsulDetailResponse(
    @SerializedName("bank")
    val bank: String?,
    @SerializedName("expiredAt")
    val expiredAt: String?,
    @SerializedName("payment_url")
    val paymentUrl: String?,
    @SerializedName("provider")
    val provider: String?,
    @SerializedName("ref_id")
    val refId: String?,
    @SerializedName("total")
    val total: Int?,
    @SerializedName("type")
    val type: String?,
    @SerializedName("va_number")
    val vaNumber: String?,
    @SerializedName("detail")
    val detail : DetailTransactionResponse?
)