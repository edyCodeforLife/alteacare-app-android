package id.altea.care.core.data.response

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class NotificationAdditionalData(
    @SerializedName("appointment_id") val appointmentId: Int?,
    @SerializedName("doctor_name") val doctorName: String?,
    @SerializedName("order_id") val orderId: String?,
    @SerializedName("type") val type: String?
)
