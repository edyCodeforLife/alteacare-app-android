package id.altea.care.core.data.request


import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class AddressRequest(
    @SerializedName("city") val city: String?,
    @SerializedName("country") val country: String?,
    @SerializedName("district") val district: String?,
    @SerializedName("province") val province: String?,
    @SerializedName("rt_rw") val rtRw: String?,
    @SerializedName("street") val street: String?,
    @SerializedName("sub_district") val subDistrict: String?
)