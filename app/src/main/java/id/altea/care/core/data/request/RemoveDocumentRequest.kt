package id.altea.care.core.data.request

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class RemoveDocumentRequest (
    @SerializedName("appointment_id") val appointmentId : Int?,
    @SerializedName("document_id") val documentId : Int?
)