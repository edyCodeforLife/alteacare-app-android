package id.altea.care.core.data.response


import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import id.altea.care.core.domain.model.City

@Keep
data class CityResponse(
    @SerializedName("id", alternate = ["city_id"])
    val id: String?,
    @SerializedName("name")
    val name: String?
) {
    companion object {
        fun mapToModel(response: CityResponse?): City {
            return City(response?.id, response?.name)
        }
    }
}
