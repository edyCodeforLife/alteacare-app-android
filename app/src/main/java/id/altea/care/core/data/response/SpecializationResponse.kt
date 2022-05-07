package id.altea.care.core.data.response

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import id.altea.care.core.domain.model.GeneralSearch
import id.altea.care.core.domain.model.Specialization

@Keep
data class SpecializationResponse(
    @SerializedName("description") val description: String?,
    @SerializedName("icon") val icon: IconResponse?,
    @SerializedName("name") val name: String?,
    @SerializedName("is_popular") val isPopular: Boolean,
    @SerializedName("specialization_id") val specializationId: String?,
    @SerializedName("sub_specialization") val subSpecialization: List<SubSpecializationResponse>?
) {

    fun toSpecialization(): GeneralSearch.Specialization {
        return GeneralSearch.Specialization(
            name.orEmpty(), specializationId.orEmpty(),
            icon?.url.orEmpty()
        )
    }

    fun toSpecializationModel(): Specialization {
        return Specialization(
            description,
            IconResponse.toIconImage(icon),
            name,
            isPopular,
            specializationId,
            subSpecialization?.map { SubSpecializationResponse.toSubSpecialization(it) }.orEmpty()
        )
    }
}
