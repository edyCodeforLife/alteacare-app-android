package id.altea.care.view.videocall

import androidx.fragment.app.FragmentActivity
import id.altea.care.view.endcall.EndCallActivity
import id.altea.care.view.endcall.specialist.EndCallSpecialistActivity
import id.altea.care.view.videocall.chat.ChatFragment
import id.altea.care.view.videocall.videofragment.VideoFragment
import id.altea.care.view.videocall.videowebview.VideoFragmentWebView
import id.altea.care.view.viewdocument.ViewDocumentActivity

class VideoCallRouter {

    fun openEndCallActivity(
        source: VideoCallActivity,
        value: String,
        orderCode: String,
        appointmentId: Int
    ) {
        source.finish()
        source.startActivity(
            EndCallActivity.createIntent(
                source,
                value,
                orderCode,
                appointmentId
            )
        )

    }

    fun openChatActivity(source: VideoCallActivity, identity: String, doctorName: String?,videoView: ChatFragment.VideoView) {
        source.showFragment(ChatFragment.createInstance(identity, doctorName,videoView), "chat")
    }

    fun openVideoFragment(source: VideoCallActivity) {
        source.showFragment(VideoFragment(), "video")
    }

    fun openVideoWebViewFragment(source: VideoCallActivity){
        source.showFragment(VideoFragmentWebView(),"videoWebView")
    }

    fun openEndCallSpecialistActivity(source: VideoCallActivity, value: String,consultationId : Int) {
        source.finish()
        source.startActivity(EndCallSpecialistActivity.createIntent(source, value,consultationId))
    }


}