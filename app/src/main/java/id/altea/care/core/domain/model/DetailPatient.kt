package id.altea.care.core.domain.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class DetailPatient(
        val addressId: String?,
        val age: Age?,
        val birthCountry: BirthCountry?,
        var birthDate: String?,
        val birthPlace: String?,
        val cardId: String?,
        val cardPhoto: String?,
        val cardType: String?,
        val city: City?,
        val country: Country?,
        val district: District?,
        val email: String?,
        val externalPatientId: ExternalPatientId?,
        val familyRelationType: FamilyRelationType?,
        val firstName: String?,
        val gender: String?,
        val id: String?,
        val insurance: Insurance?,
        val isValid: Boolean?,
        val lastName: String?,
        val nationality: Nationality?,
        val phone: String?,
        val province: Province?,
        val refId: String?,
        val rtRw: String?,
        val status: String?,
        val street: String?,
        val subDistrict: SubDistrict?,
        val isRegistered : Boolean?
) : Parcelable{
        fun toUserProfile(): UserProfile {
                return UserProfile(firstName, email)
        }
    fun completeAddress(): String? {
        return street + "," + "Blok RT/RW $rtRw kel.${subDistrict?.name} Kec.${district?.name} ${city?.name} ${province?.name} ${subDistrict?.postalCode}"
    }
}