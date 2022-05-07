package id.altea.care.core.data.response


import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import id.altea.care.core.domain.model.PagePatient

@Keep
data class PagePatientResponse(
        @SerializedName("patient") val patients: List<PatientResponseItem>?,
        @SerializedName("meta") val meta: MetaResponse?
) {
    companion object {
        fun toPagePatient(response: PagePatientResponse?): PagePatient {
            return PagePatient(
                    response?.patients?.map { it.toFamily() } ?: listOf(),
                    MetaResponse.toMetaPage(response?.meta)
            )
        }
    }
}