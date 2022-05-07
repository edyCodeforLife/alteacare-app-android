package id.altea.care.core.data.response


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class SubDistrict(
    @SerializedName("geo_area")
    val geoArea: String?,
    @SerializedName("id")
    val id: String?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("postal_code")
    val postalCode: String?
)