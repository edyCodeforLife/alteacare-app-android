package id.altea.care.core.data.request


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class CancelReasonOrderRequest(
    @SerializedName("appointment_id")
    val appointmentId: Int?,
    @SerializedName("note")
    val note: String?
)