package id.altea.care.core.domain.model


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep


data class ConfigOverwrite(
    val disableDeepLinking: Boolean?,
    val enableWelcomePage: Boolean?,
    val prejoinPageEnabled: Boolean?,
    val toolbarButtons: List<String>?
)