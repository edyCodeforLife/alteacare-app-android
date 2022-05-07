package id.altea.care.core.data.response

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import id.altea.care.core.domain.model.HospitalResult

@Keep
data class HospitalDomisiliResponse(
    @SerializedName("address")
    val address: String?,
    @SerializedName("hospital_id")
    val hospitalId: String?,
    @SerializedName("icon")
    val icon: IconResponse?,
    @SerializedName("image")
    val image: IconResponse?,
    @SerializedName("latitude")
    val latitude: String?,
    @SerializedName("longitude")
    val longitude: String?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("phone")
    val phone: String?
){
    fun toHospital() : HospitalResult {
        return HospitalResult(
            address,
            hospitalId,
            IconResponse.toIconImage(icon),
            IconResponse.toIconImage(image),
            latitude,
            longitude,
            name,
            phone
        )
    }
}