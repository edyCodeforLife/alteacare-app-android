package id.altea.care.core.data.request

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Keep
data class AddDocumentRequest (
    @SerializedName("appointment_id") val appointmentId : Int,
    @SerializedName("file") val file : String
)