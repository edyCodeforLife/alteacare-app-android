package id.altea.care.core.domain.model


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class VideoCallProviderData(
    @SerializedName("flutter_webrtc")
    val flutterWebrtc: Any?,
    @SerializedName("jitsi")
    val jitsi: Jitsi?,
    @SerializedName("twilio")
    val twilio: Twilio?
)