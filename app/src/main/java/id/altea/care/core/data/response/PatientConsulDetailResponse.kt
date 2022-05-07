package id.altea.care.core.data.response


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep
import id.altea.care.core.domain.model.AddressRaw
import id.altea.care.core.domain.model.PatientCosulDetail

@Keep
data class PatientConsulDetailResponse(
    @SerializedName("address")
    val address: String?,
    @SerializedName("address_raw")
    val addressRaw : List<AddressRawResponse>?,
    @SerializedName("age")
    val age: AgeResponse?,
    @SerializedName("avatar")
    val avatar: IconResponse?,
    @SerializedName("birthdate")
    val birthdate: String?,
    @SerializedName("card_id")
    val cardId: String?,
    @SerializedName("card_photo")
    val cardPhoto: CardPhotoResponse?,
    @SerializedName("card_type")
    val cardType: String?,
    @SerializedName("email")
    val email: String?,
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
    @SerializedName("last_name")
    val lastName: String?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("phone_number")
    val phoneNumber: String?,
    @SerializedName("type")
    val type: String?
){
    companion object{
        fun toPatientConsulDetailModel(patientConsulDetailResponse: PatientConsulDetailResponse?) : PatientCosulDetail?{
            return PatientCosulDetail(
                patientConsulDetailResponse?.address,
                patientConsulDetailResponse?.addressRaw?.map { AddressRaw.toAddressRawModel(it) },
                patientConsulDetailResponse?.age?.toAge(),
                patientConsulDetailResponse?.avatar?.formats?.small,
                patientConsulDetailResponse?.birthdate,
                patientConsulDetailResponse?.cardId,
                patientConsulDetailResponse?.cardPhoto?.formats?.small,
                patientConsulDetailResponse?.cardType,
                patientConsulDetailResponse?.email,
                patientConsulDetailResponse?.familyRelationType?.toFamilyRelationType(),
                patientConsulDetailResponse?.firstName,
                patientConsulDetailResponse?.gender,
                patientConsulDetailResponse?.id,
                patientConsulDetailResponse?.insurance?.toInsurance(),
                patientConsulDetailResponse?.lastName,
                patientConsulDetailResponse?.name,
                patientConsulDetailResponse?.phoneNumber,
                patientConsulDetailResponse?.type
            )
        }
    }
}