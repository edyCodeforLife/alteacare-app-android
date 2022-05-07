package id.altea.care.core.domain.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import id.altea.care.core.data.response.ChatProviderDataResponse
import id.altea.care.core.data.response.VideoCallProviderDataResponse


data class AppointmentProvider(
    val chatProvider: String?,
    val chatProviderDataResponse: ChatProviderData?,
    val videoCallProvider: String?,
    val videoCallProviderDataResponse: VideoCallProviderData?
)