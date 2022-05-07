package id.altea.care.core.data.response


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class AddressRawResponse(
    @SerializedName("address_id")
    val addressId: String?,
    @SerializedName("city")
    val city: CityResponse?,
    @SerializedName("country")
    val country: CountryResponse?,
    @SerializedName("district")
    val district: DistrictResponse?,
    @SerializedName("latitude")
    val latitude: String?,
    @SerializedName("longitude")
    val longitude: String?,
    @SerializedName("province")
    val province: ProvinceResponse?,
    @SerializedName("rt_rw")
    val rtRw: String?,
    @SerializedName("street")
    val street: String?,
    @SerializedName("sub_district")
    val subDistrict: SubDistrictResponse?
)