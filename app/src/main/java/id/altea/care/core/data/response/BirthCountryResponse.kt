package id.altea.care.core.data.response


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep
import id.altea.care.core.domain.model.BirthCountry

@Keep
data class BirthCountryResponse(
    @SerializedName("code")
    val code: String?,
    @SerializedName("id")
    val id: String?,
    @SerializedName("name")
    val name: String?
){
    fun toBirthCountry() : BirthCountry {
        return BirthCountry(
          code,
          id,
          name
        )
    }
}