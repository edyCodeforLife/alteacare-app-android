package id.altea.care.core.domain.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Profile(
    val id: String?,
    val email: String?,
    val phone: String?,
    val firstName: String?,
    val lastName: String?,
    val isVerifiedEmail: Boolean?,
    val isVerifiedPhone: Boolean?,
    val userRole: List<String>?,
    val userDetails: UserDetails?,
    val userAddresses: List<UserAddress>?,
    val loginAt: String? = null,
    val defaultPatientData: DefaultPatientData? = null
) : Parcelable {
    fun toUserProfile(): UserProfile {
        return UserProfile(firstName, email)
    }
}

@Parcelize
data class UserProfile(
    val name: String?,
    val email: String? = null
) : Parcelable

@Parcelize
data class UserDetails(
    val idCard: String?,
    val gender: String?,
    val birthDate: String?,
    val photoIdCard: String?,
    val avatar: IconImage?,
    val age: Age?,
    val insurance: String? = null
) : Parcelable


@Parcelize
data class IconImage(
    val formats: FormatImage?,
    val url: String?
) : Parcelable

@Parcelize
data class FormatImage(
    val thumbnail: String?,
    val large: String?,
    val medium: String?,
    val small: String?
) : Parcelable

@Parcelize
data class Age(
    val month: Int?,
    val year: Int?
) : Parcelable

@Parcelize
data class UserAddress(
    var id: String?,
    var type: String?,
    val street: String?,
    val rtRw: String?,
    val country: Country?,
    val province: Province?,
    val city: City?,
    val district: District?,
    val subDistrict: SubDistrict?,
    val latitude: Double?,
    val longitude: Double?
) : Parcelable {
    fun completeAddress(): String? {
        return street + "," + "Blok RT/RW $rtRw kel.${subDistrict?.name} Kec.${district?.name} ${city?.name} ${province?.name} ${subDistrict?.postalCode}"
    }
}