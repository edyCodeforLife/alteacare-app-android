package id.altea.care.core.data.response


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep
import id.altea.care.core.domain.model.Jitsi

@Keep
data class JitsiResponse(
    @SerializedName("host")
    val host: String?,
    @SerializedName("jwt")
    val jwt: String?,
    @SerializedName("options")
    val optionsResponse: OptionsResponse?,
    @SerializedName("url")
    val url: String?
){
    fun toJitsi() : Jitsi{
        return  Jitsi(
            host,
            jwt,
            optionsResponse?.toOptions(),
            url
        )
    }
}