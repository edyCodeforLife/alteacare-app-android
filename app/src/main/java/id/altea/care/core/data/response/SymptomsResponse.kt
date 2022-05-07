package id.altea.care.core.data.response

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import id.altea.care.core.domain.model.Symptoms

@Keep
data class SymptomsResponse(
    @SerializedName("name")
    val name: String?,
    @SerializedName("slug")
    val slug: String?,
    @SerializedName("symtomId")
    val symptomId: String?
) {
    companion object {
        fun SymptomsResponse.toSymptoms() : Symptoms {
            return Symptoms(
                name = name,
                slug = slug,
                symptomId = symptomId
            )
        }
    }
}
