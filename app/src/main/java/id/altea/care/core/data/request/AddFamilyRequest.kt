package id.altea.care.core.data.request


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class AddFamilyRequest(
    @SerializedName("family_relation_type")
    val family_relation_type : String?,
    @SerializedName("address_id")
    val addressId: String?,
    @SerializedName("birth_country")
    val birthCountry: String?,
    @SerializedName("birth_date")
    val birthDate: String?,
    @SerializedName("birth_place")
    val birthPlace: String?,
    @SerializedName("card_id")
    val cardId: String?,
    @SerializedName("first_name")
    val firstName: String?,
    @SerializedName("gender")
    val gender: String?,
    @SerializedName("last_name")
    val lastName: String?,
    @SerializedName("nationality")
    val nationality: String?
)

@Keep
data class AddMemberFamilyRegisterAccountRequest(
    @SerializedName("family_relation_type")
    val family_relation_type : String?,
    @SerializedName("address_id")
    val addressId: String?,
    @SerializedName("birth_country")
    val birthCountry: String?,
    @SerializedName("birth_date")
    val birthDate: String?,
    @SerializedName("birth_place")
    val birthPlace: String?,
    @SerializedName("card_id")
    val cardId: String?,
    @SerializedName("first_name")
    val firstName: String?,
    @SerializedName("gender")
    val gender: String?,
    @SerializedName("last_name")
    val lastName: String?,
    @SerializedName("nationality")
    val nationality: String?,
    @SerializedName("email")
    val email : String?,
    @SerializedName("phone")
    val phone : String?
)

