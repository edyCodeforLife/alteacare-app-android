package id.altea.care.core.domain.model

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import id.altea.care.core.data.response.*
import kotlinx.android.parcel.Parcelize

@Parcelize
data class AddressRaw(
    val addressId: String?,
    val city: City?,
    val country: Country?,
    val district: District?,
    val latitude: String?,
    val longitude: String?,
    val province: Province?,
    val rtRw: String?,
    val street: String?,
    val subDistrict: SubDistrict?
) : Parcelable{
    companion object{
        fun toAddressRawModel(addressRawResponse: AddressRawResponse) : AddressRaw{
            return AddressRaw(
                addressRawResponse.addressId,
                CityResponse.mapToModel(addressRawResponse.city),
                addressRawResponse.country?.toCountry(),
                DistrictResponse.mapToModel(addressRawResponse.district),
                addressRawResponse.latitude,
                addressRawResponse.longitude,
                ProvinceResponse.mapToModel(addressRawResponse.province),
                addressRawResponse.rtRw,
                addressRawResponse.street,
                SubDistrictResponse.mapToModel(addressRawResponse.subDistrict)
            )
        }
    }

    fun completeAddress(): String? {
        return street + "," + "Blok RT/RW $rtRw kel.${subDistrict?.name} Kec.${district?.name} ${city?.name} ${province?.name} ${subDistrict?.postalCode}"
    }
}