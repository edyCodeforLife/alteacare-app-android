package id.altea.care.core.data.response


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep
import id.altea.care.core.domain.model.DefaultPatientData

@Keep
data class DefaultPatientDataResponse(
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
    val cardPhoto: PhotoIdCard?,
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
    @SerializedName("is_default")
    val isDefault: Boolean?,
    @SerializedName("is_registered")
    val isRegistered: Boolean?,
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
    @SerializedName("user_addresses")
    val userAddresses: List<UserAddressResponse>?
){
    fun toDefaultPatientData() : DefaultPatientData{
        return DefaultPatientData(
            addressId = addressId,
            age = age?.toAge(),
            birthCountry = birthCountry?.toBirthCountry(),
            birthDate = birthDate,
            birthPlace = birthPlace,
            cardId = cardId,
            cardPhoto = cardPhoto?.formats?.small,
            cardType = cardType,
            city = CityResponse.mapToModel(city),
            country = country?.toCountry(),
            district = DistrictResponse.mapToModel(district),
            email = email,
            externalPatientId = externalPatientId?.toExternalPatientId(),
            familyRelationType = familyRelationType?.toFamilyRelationType(),
            firstName = firstName,
            gender = gender,
            id = id,
            insurance = insurance?.toInsurance(),
            isDefault = isDefault,
            isRegistered = isRegistered,
            isValid = isValid,
            lastName = lastName,
            nationality = nationality?.toNationality(),
            phone = phone,
            province = ProvinceResponse.mapToModel(province),
            refId = refId,
            rtRw = rtRw,
            status = status,
            street = street,
            subDistrict = SubDistrictResponse.mapToModel(subDistrict),
            userAddresses = userAddresses?.map { it.toAddress() }
        )
    }
}