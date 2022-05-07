package id.altea.care.core.data.response

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import id.altea.care.core.domain.model.PaymentMethod

@Keep
data class PaymentMethodResponse(
    @SerializedName("code") val code: String?,
    @SerializedName("description") val description: String?,
    @SerializedName("icon") val icon: String?,
    @SerializedName("name") val name: String?,
    @SerializedName("provider") val provider: String?
) {
    companion object {
        fun mapToPaymentMethod(data: PaymentMethodResponse): PaymentMethod {
            return PaymentMethod(data.code, data.description, data.icon, data.name, data.provider)
        }
    }
}
