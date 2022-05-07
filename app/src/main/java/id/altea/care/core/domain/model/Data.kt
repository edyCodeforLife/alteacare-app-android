package id.altea.care.core.domain.model


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep


data class Data(
    val chatProvider: String?,
    val chatProviderData: ChatProviderData?,
    val videoCallProvider: String?,
    val videoCallProviderData: VideoCallProviderData?
)