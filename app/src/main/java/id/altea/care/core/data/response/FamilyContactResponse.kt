package id.altea.care.core.data.response


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep
import id.altea.care.core.domain.model.FamilyContact

@Keep
data class FamilyContactResponse(
    @SerializedName("id")
    val id: String?,
    @SerializedName("name")
    val name: String?
){
    fun toFamilyContact() : FamilyContact{
        return FamilyContact(
            id,
            name
        )
    }
}