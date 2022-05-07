package id.altea.care.core.domain.model


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep


data class Options(
    val configOverwrite: ConfigOverwrite?,
    val interfaceConfigOverwrite: InterfaceConfigOverwrite?,
    val jwt: String?,
    val roomName: String?
)