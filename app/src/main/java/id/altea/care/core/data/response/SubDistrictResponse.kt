package id.altea.care.core.data.response

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import id.altea.care.core.domain.model.SubDistrict

@Keep
data class SubDistrictResponse(
    @SerializedName("geo_area")
    val geoArea: String?,
    @SerializedName("id", alternate = ["sub_district_id"])
    val id: String?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("postal_code")
    val postalCode: String?
) {
    companion object {
        fun mapToModel(response: SubDistrictResponse?): SubDistrict {
            return SubDistrict(
                response?.geoArea,
                response?.id,
                response?.name,
                response?.postalCode
            )
        }
    }
}
