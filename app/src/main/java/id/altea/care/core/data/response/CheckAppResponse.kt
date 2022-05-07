package id.altea.care.core.data.response


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep
import id.altea.care.core.domain.model.CheckApp

@Keep
data class CheckAppResponse(
    @SerializedName("isUpdateRequired")
    val isUpdateRequired: Boolean?,
    @SerializedName("latest")
    val latestResponse: LatestResponse?
){
    fun toCheckApp() : CheckApp {
        return CheckApp(
            isUpdateRequired,
            latestResponse?.toLatest()
        )
    }
}