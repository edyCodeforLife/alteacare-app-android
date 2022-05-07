package id.altea.care.core.data.response


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep
import id.altea.care.core.domain.model.ExternalPatientId

@Keep
data class ExternalPatientIdResponse(
    @SerializedName("SAP_MIKA_BINTARO")
    val sAPMIKABINTARO: String?
){
    fun toExternalPatientId() : ExternalPatientId {
        return ExternalPatientId(sAPMIKABINTARO)
    }
}