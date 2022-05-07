package id.altea.care.core.data.response


import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import id.altea.care.core.domain.model.District

@Keep
data class DistrictResponse(
    @SerializedName("id", alternate = ["district_id"])
    val id: String?,
    @SerializedName("name")
    val name: String?
) {
    companion object {
        fun mapToModel(response: DistrictResponse?): District {
            return District(response?.id, response?.name)
        }
    }
}
