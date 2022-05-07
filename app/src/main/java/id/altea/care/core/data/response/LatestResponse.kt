package id.altea.care.core.data.response


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep
import id.altea.care.core.domain.model.Latest

@Keep
data class LatestResponse(
    @SerializedName("platform")
    val platform: String?,
    @SerializedName("version")
    val version: String?,
    @SerializedName("version_code")
    val versionCode: Int?
){
    fun toLatest() : Latest {
        return Latest(
            platform,
            version,
            versionCode
        )
    }
}