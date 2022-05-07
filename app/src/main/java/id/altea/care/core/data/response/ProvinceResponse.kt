package id.altea.care.core.data.response

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import id.altea.care.core.domain.model.Province

@Keep
data class ProvinceResponse(
    @SerializedName("code")
    val code: String?,
    @SerializedName("id", alternate = ["province_id"])
    val id: String?,
    @SerializedName("name")
    val name: String?
) {
    companion object {
        fun mapToModel(response: ProvinceResponse?): Province {
            return Province(
                response?.code,
                response?.id,
                response?.name
            )
        }
    }
}

