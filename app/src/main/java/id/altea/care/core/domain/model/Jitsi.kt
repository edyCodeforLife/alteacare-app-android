package id.altea.care.core.domain.model


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep


data class Jitsi(
    val host: String?,
    val jwt: String?,
    val options: Options?,
    val url: String?
)