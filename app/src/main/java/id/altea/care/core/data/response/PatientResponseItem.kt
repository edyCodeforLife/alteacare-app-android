package id.altea.care.core.data.response


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep
import id.altea.care.core.domain.model.Country
import id.altea.care.core.domain.model.PatientFamily
import id.altea.care.view.consultation.item.PatientItem

@Keep
data class PatientResponseItem(
        @SerializedName("address_id")
    val addressId: String?,
        @SerializedName("age")
    val age: AgeResponse?,
        @SerializedName("birth_country")
    val birthCountryResponse: BirthCountryResponse?,
        @SerializedName("birth_date")
    val birthDate: String?,
        @SerializedName("birth_place")
    val birthPlace: String?,
        @SerializedName("card_id")
    val cardId: String?,
        @SerializedName("card_photo")
    val cardPhoto: CardPhotoResponse?,
        @SerializedName("card_type")
    val cardType: String?,
        @SerializedName("city")
    val city: CityResponse?,
        @SerializedName("country")
    val country: CountryResponse?,
        @SerializedName("email")
        val email: String?,
        @SerializedName("district")
    val district: DistrictResponse?,
        @SerializedName("external_patient_id")
    val externalPatientId: ExternalPatientIdResponse?,
        @SerializedName("family_relation_type")
    val familyRelationTypeResponse: FamilyRelationTypeResponse?,
        @SerializedName("first_name")
    val firstName: String?,
        @SerializedName("gender")
    val gender: String?,
        @SerializedName("id")
    val id: String?,
        @SerializedName("insurance")
    val insurance: InsuranceResponse?,
        @SerializedName("is_valid")
    val isValid: Boolean?,
        @SerializedName("last_name")
    val lastName: String?,
        @SerializedName("nationality")
    val nationalityResponse: NationalityResponse?,
        @SerializedName("phone")
        val phone: String?,
        @SerializedName("province")
    val province: ProvinceResponse?,
        @SerializedName("ref_id")
    val refId: String?,
        @SerializedName("rt_rw")
    val rtRw: String?,
        @SerializedName("status")
    val status: String?,
        @SerializedName("street")
    val street: String?,
        @SerializedName("sub_district")
    val subDistrict: SubDistrictResponse?,
        @SerializedName("is_default")
    val isDefault : Boolean?
){
    fun toFamily() : PatientFamily {
        return PatientFamily(
                addressId,
                age?.toAge(),
                birthCountryResponse?.toBirthCountry(),
                birthDate,
                birthPlace,
                cardId,
                cardPhoto?.formats?.small,
                cardType,
                CityResponse.mapToModel(city),
                country?.toCountry(),
                DistrictResponse.mapToModel(district),
                email,
                externalPatientId?.toExternalPatientId(),
                familyRelationTypeResponse?.toFamilyRelationType(),
                firstName,
                gender,
                id,
                insurance?.toInsurance(),
                isValid,
                lastName,
                nationalityResponse?.toNationality(),
                phone,
                ProvinceResponse.mapToModel(province),
                refId,
                rtRw,
                status,
                street,
                SubDistrictResponse.mapToModel(subDistrict),
                isDefault
        )
    }
}