package id.altea.care.core.data.response


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep
import id.altea.care.core.domain.model.Options

@Keep
data class OptionsResponse(
    @SerializedName("configOverwrite")
    val configOverwriteResponse: ConfigOverwriteResponse?,
    @SerializedName("interfaceConfigOverwrite")
    val interfaceConfigOverwriteResponse: InterfaceConfigOverwriteResponse?,
    @SerializedName("jwt")
    val jwt: String?,
    @SerializedName("roomName")
    val roomName: String?
){
    fun toOptions() : Options{
        return Options(
            configOverwriteResponse?.toConfigureOverwrite(),
            interfaceConfigOverwriteResponse?.toInterfaceConfigOverwrite(),
            jwt,
            roomName
        )
    }
}