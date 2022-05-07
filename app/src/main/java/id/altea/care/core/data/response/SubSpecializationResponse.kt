package id.altea.care.core.data.response

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import id.altea.care.core.domain.model.SubSpecialization

@Keep
data class SubSpecializationResponse(
    @SerializedName("description") val description: String?,
    @SerializedName("icon") val icon: IconResponse?,
    @SerializedName("name") val name: String?,
    @SerializedName("specialization_id") val specializationId: String?
) {
    companion object {
        fun toSubSpecialization(data: SubSpecializationResponse): SubSpecialization {
            return SubSpecialization(
                data.description,
                IconResponse.toIconImage(data.icon),
                data.name,
                data.specializationId
            )
        }
    }
}
