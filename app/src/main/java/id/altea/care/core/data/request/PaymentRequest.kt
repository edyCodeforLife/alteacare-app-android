package id.altea.care.core.data.request


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class PaymentRequest(
    @SerializedName("appointment_id") val appointmentId: Int?,
    @SerializedName("method") val method: String?,
    @SerializedName("voucher_code") val voucherCode : String? = ""
)