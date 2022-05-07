package id.altea.care.core.data.response


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep
import id.altea.care.core.domain.model.Hospital
import id.altea.care.core.domain.model.HospitalResult



@Keep
data class HospitalResponse(
    @SerializedName("icon") val icon: IconResponse?,
    @SerializedName("id") val id: String?,
    @SerializedName("image") val image: IconResponse?,
    @SerializedName("name") val name: String?
) {
    companion object {
        fun toHospital(data: HospitalResponse?): Hospital {
            return Hospital(data?.icon?.url, data?.id, data?.name)
        }
    }
}