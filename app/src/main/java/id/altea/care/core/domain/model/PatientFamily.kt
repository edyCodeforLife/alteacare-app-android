package id.altea.care.core.domain.model

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import id.altea.care.core.data.response.*

import kotlinx.android.parcel.Parcelize

@Parcelize
data class PatientFamily(
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
        var isDefault : Boolean?
) : Parcelable {
        fun toUserProfile(): UserProfile {
                return UserProfile(firstName, email)
        }
}