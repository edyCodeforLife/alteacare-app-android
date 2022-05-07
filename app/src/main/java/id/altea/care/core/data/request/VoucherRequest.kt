package id.altea.care.core.data.request


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class VoucherRequest(
    @SerializedName("code")
    val code: String?,
    @SerializedName("transaction_id")
    val transactionId: String?,
    @SerializedName("type_of_service")
    val typeOfService: String?
)