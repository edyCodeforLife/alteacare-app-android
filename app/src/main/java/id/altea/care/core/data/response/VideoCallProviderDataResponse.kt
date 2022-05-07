package id.altea.care.core.data.response


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep
import id.altea.care.core.domain.model.VideoCallProviderData

@Keep
data class VideoCallProviderDataResponse(
    @SerializedName("flutter_webrtc")
    val flutterWebrtc: Any?,
    @SerializedName("jitsi")
    val jitsiResponse: JitsiResponse?,
    @SerializedName("twilio")
    val twilio: TwilioResponse?
){
    fun toVideoCallProviderData() : VideoCallProviderData{
        return VideoCallProviderData(
            flutterWebrtc,
            jitsiResponse?.toJitsi(),
            twilio?.toTwillio()
        )
    }
}