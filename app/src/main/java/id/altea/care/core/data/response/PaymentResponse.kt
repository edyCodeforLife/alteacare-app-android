package id.altea.care.core.data.response


import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import id.altea.care.core.domain.model.Payment

@Keep
data class PaymentResponse(
    @SerializedName("bank") val bank: String?,
    @SerializedName("expiredAt") val expiredAt: String?,
    @SerializedName("payment_url") val paymentUrl: String?,
    @SerializedName("ref_id") val refId: String?,
    @SerializedName("total") val total: Double?,
    @SerializedName("type") val type: String?,
    @SerializedName("va_number") val vaNumber: String?,
    @SerializedName("redirect_url") val redirectUrl: String?,
    @SerializedName("token") val token: String?,
) {
    companion object {
        fun mapToPayment(paymentResponse: PaymentResponse): Payment {
            return Payment(
                paymentResponse.redirectUrl,
                paymentResponse.token,
                paymentResponse.total,
                paymentResponse.refId,
                paymentResponse.bank,
                paymentResponse.expiredAt,
                paymentResponse.paymentUrl,
                paymentResponse.type,
                paymentResponse.vaNumber
            )
        }
    }
}
