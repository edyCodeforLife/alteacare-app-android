package id.altea.care.core.data.response


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep
import id.altea.care.core.domain.model.DetailPatient

@Keep
data class DetailPatientResponse(
    @SerializedName("address_id")
    val addressId: String?,
    @SerializedName("age")
    val age: AgeResponse?,
    @SerializedName("birth_country")
    val birthCountry: BirthCountryResponse?,
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
    @SerializedName("district")
    val district: DistrictResponse?,
    @SerializedName("email")
    val email: String?,
    @SerializedName("external_patient_id")
    val externalPatientId: ExternalPatientIdResponse?,
    @SerializedName("family_relation_type")
    val familyRelationType: FamilyRelationTypeResponse?,
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
    val nationality: NationalityResponse?,
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
    @SerializedName("is_registered")
    val isRegistered : Boolean?
){
    fun toPatientDetailResponse() : DetailPatient{
        return DetailPatient(
           addressId,
           age?.toAge(),
           birthCountry?.toBirthCountry(),
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
           familyRelationType?.toFamilyRelationType(),
           firstName,
           gender,
           id,
           insurance?.toInsurance(),
           isValid,
           lastName,
           nationality?.toNationality(),
           phone,
           ProvinceResponse.mapToModel(province),
           refId,
           rtRw,
           status,
           street,
           SubDistrictResponse.mapToModel(subDistrict),
           isRegistered
        )
    }
}