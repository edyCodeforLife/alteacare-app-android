package id.altea.care.core.data.response

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import id.altea.care.core.domain.model.PaymentTypes

@Keep
data class PaymentTypesResponse(
    @SerializedName("payment_methods") val paymentMethods: List<PaymentMethodResponse>?,
    @SerializedName("type") val type: String?
) {
    companion object {
        fun mapToPaymentTypes(data: PaymentTypesResponse): PaymentTypes {
            return PaymentTypes(
                data.paymentMethods?.map { PaymentMethodResponse.mapToPaymentMethod(it) },
                data.type
            )
        }
    }
}
