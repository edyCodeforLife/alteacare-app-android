package id.altea.care.core.data.response


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep
import id.altea.care.core.domain.model.ConfigOverwrite

@Keep
data class ConfigOverwriteResponse(
    @SerializedName("disableDeepLinking")
    val disableDeepLinking: Boolean?,
    @SerializedName("enableWelcomePage")
    val enableWelcomePage: Boolean?,
    @SerializedName("prejoinPageEnabled")
    val prejoinPageEnabled: Boolean?,
    @SerializedName("toolbarButtons")
    val toolbarButtons: List<String>?
){
    fun toConfigureOverwrite() : ConfigOverwrite{
        return ConfigOverwrite(
            disableDeepLinking,
            enableWelcomePage,
            prejoinPageEnabled,
            toolbarButtons
        )
    }
}