package id.altea.care.core.data.response


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep
import id.altea.care.core.domain.model.AppointmentProvider

@Keep
data class AppointmentProviderResponse(
    @SerializedName("chat_provider")
    val chatProvider: String?,
    @SerializedName("chat_provider_data")
    val chatProviderDataResponse: ChatProviderDataResponse?,
    @SerializedName("video_call_provider")
    val videoCallProvider: String?,
    @SerializedName("video_call_provider_data")
    val videoCallProviderDataResponse: VideoCallProviderDataResponse?
){
   fun  toAppointmentProvider() : AppointmentProvider {

       return AppointmentProvider(
        chatProvider,
        chatProviderDataResponse?.toChatProviderData(),
        videoCallProvider,
        videoCallProviderDataResponse?.toVideoCallProviderData()
       )
   }
}