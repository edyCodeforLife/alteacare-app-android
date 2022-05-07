package id.altea.care.core.data.response


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep
import id.altea.care.core.domain.model.FamilyRelationType

@Keep
data class FamilyRelationTypeResponse(
    @SerializedName("id")
    val id: String?,
    @SerializedName("name")
    val name: String?
){
    fun toFamilyRelationType() : FamilyRelationType {
        return  FamilyRelationType(id,name)
    }
}