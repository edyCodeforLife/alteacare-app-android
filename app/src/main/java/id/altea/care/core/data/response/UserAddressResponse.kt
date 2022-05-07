package id.altea.care.core.data.response

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import id.altea.care.core.domain.model.UserAddress

@Keep
data class UserAddressResponse(
    @SerializedName("id")
    val id: String?,
    @SerializedName("city")
    val city: CityResponse?,
    @SerializedName("country")
    val country: CountryResponse?,
    @SerializedName("district")
    val district: DistrictResponse?,
    @SerializedName("latitude")
    val latitude: Double?,
    @SerializedName("longitude")
    val longitude: Double?,
    @SerializedName("province")
    val province: ProvinceResponse?,
    @SerializedName("rt_rw")
    val rtRw: String?,
    @SerializedName("street")
    val street: String?,
    @SerializedName("sub_district")
    val subDistrict: SubDistrictResponse?,
    @SerializedName("type")
    val type: String?
) {
    fun toAddress(): UserAddress {
        return UserAddress(
            id = id,
            city = CityResponse.mapToModel(city),
            country = country?.toCountry(),
            district = DistrictResponse.mapToModel(district),
            latitude = latitude,
            longitude = longitude,
            province = ProvinceResponse.mapToModel(province),
            rtRw = rtRw,
            street = street,
            subDistrict = SubDistrictResponse.mapToModel(subDistrict),
            type = type
        )
    }
}