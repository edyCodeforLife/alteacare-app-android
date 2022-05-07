package id.altea.care.core.data.response


import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import id.altea.care.core.domain.model.Country

@Keep
data class CountryResponse(
    @SerializedName("code")
    val code: String?,
    @SerializedName("id", alternate = ["country_id"])
    val countryId: String?,
    @SerializedName("name")
    val name: String?
) {
    fun toCountry(): Country {
        return Country(code, countryId, name)
    }
}