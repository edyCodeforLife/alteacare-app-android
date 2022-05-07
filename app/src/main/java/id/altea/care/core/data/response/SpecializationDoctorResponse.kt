package id.altea.care.core.data.response


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep
import id.altea.care.core.domain.model.Specialization
import id.altea.care.core.domain.model.SpecializationDoctor

@Keep
data class SpecializationDoctorResponse(
    @SerializedName("id")
    val id: String?,
    @SerializedName("name")
    val name: String?
){
    fun toSpecialization() : Specialization {
        return Specialization(null,null,name,false,id, listOf())
    }
}