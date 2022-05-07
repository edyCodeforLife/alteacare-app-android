package id.altea.care.view.videocall.videofragment

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.projection.MediaProjectionManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.SystemClock
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import com.twilio.audioswitch.AudioDevice
import com.twilio.audioswitch.AudioDevice.*
import com.twilio.audioswitch.AudioSwitch
import com.twilio.chat.CallbackListener
import com.twilio.chat.ChatClient
import com.twilio.chat.ErrorInfo
import com.twilio.video.*
import id.altea.care.R
import id.altea.care.core.base.BaseActivity
import id.altea.care.core.base.BaseFragmentVM
import id.altea.care.core.domain.model.AppointmentRoom
import id.altea.care.core.domain.model.Enable
import id.altea.care.core.domain.model.TrackingSpeed
import id.altea.care.core.domain.model.Twilio
import id.altea.care.core.exception.Failure
import id.altea.care.core.ext.*
import id.altea.care.core.helper.util.CameraCapturerCompat
import id.altea.care.core.helper.util.Constant
import id.altea.care.core.helper.util.ConstantExtra
import id.altea.care.databinding.FragmentVideoCallBinding
import id.altea.care.view.login.LoginActivityRouter
import id.altea.care.view.videocall.VideoCallActivity
import id.altea.care.view.videocall.VideoCallRouter
import id.altea.care.view.videocall.VideoCallVM
import id.altea.care.view.videocall.chat.ChatFragment
import id.altea.care.view.videocall.sharescreen.ScreenCapturerManager
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.sentry.Sentry
import kotlinx.android.synthetic.main.content_disconnect.view.*
import kotlinx.android.synthetic.main.content_reconnecting.view.*
import kotlinx.android.synthetic.main.fragment_video_call.*
import kotlinx.android.synthetic.main.item_mic_primary.view.*
import kotlinx.android.synthetic.main.item_mic_second.view.*
import timber.log.Timber
import tvi.webrtc.VideoSink
import tvi.webrtc.voiceengine.WebRtcAudioUtils
import java.util.concurrent.TimeUnit

class VideoFragment : BaseFragmentVM<FragmentVideoCallBinding, VideoCallVM>() {

    private var localAudioTrack: LocalAudioTrack? = null
    private var localVideoTrack: LocalVideoTrack? = null
    private var shareScreenVideoTrack: LocalVideoTrack? = null

    private var remoteAudioTrack: RemoteAudioTrack? = null
    private var cameraCapture: CameraCapturerCompat? = null
    private var localVideoView: VideoSink? = null
    private var remoteVideoTrack: RemoteVideoTrack? = null
    private var roomActive: Room? = null
    private val router = VideoCallRouter()
    private var token: String? = ""
    private var valueRoom: String? =""
    private var identity: String? =""
    private var enableResponse: Enable? = null
    private var screenCapturerManager: ScreenCapturerManager? = null
    private var screenCapturer: ScreenCapturer? = null
    private val REQUEST_MEDIA_PROJECTION = 250
    private var remoteParticipant: RemoteParticipant? = null
    private var onPauseLocalVideoTrack: LocalVideoTrack? = null
    private var isSpeakerMute = false
    private var failure : Boolean? = false

    private val audioSwitch by lazy {
        AudioSwitch(
            requireContext(), preferredDeviceList = listOf(
                BluetoothHeadset::class.java,
                WiredHeadset::class.java, Speakerphone::class.java, Earpiece::class.java
            )
        )
    }

    private val appointmentId: Int by lazy { viewModel?.appoimentId ?: -1 }
    private val callMethod by lazy { viewModel?.typeConsultation }
    private val orderCode by lazy { viewModel?.orderCode }
    private val profile by lazy { viewModel?.userProfile }
    private val infoDetail by lazy { viewModel?.infoDetail }

    override fun bindViewModel(): VideoCallVM {
        return ViewModelProvider(
            requireActivity().viewModelStore,
            viewModelFactory
        ).get(VideoCallVM::class.java)
    }

    override fun getUiBinding(): FragmentVideoCallBinding =
        FragmentVideoCallBinding.inflate(layoutInflater)

    override fun onFirstLaunch(savedInstanceState: Bundle?, view: View) {
        videoCallReconnecting.isVisible = true
        videoCallReconnecting.dialogImgCallVideo.startRippleAnimation()
        viewModel?.doCalculate(requireContext())
//        viewModel?.doGetAppointmentRoom(appointmentId)

        if (Build.VERSION.SDK_INT >= 29) {
            screenCapturerManager = ScreenCapturerManager(requireContext())
        }
        viewBinding?.run {
            videoCallBtmNavigationView.itemIconTintList = null
            videoCallBtmNavigationView.setOnNavigationItemSelectedListener { item ->
                when (item.itemId) {
                    R.id.miMessage -> {
                        viewBinding?.videoCallBtmNavigationView?.menu?.findItem(R.id.miMessage)
                            ?.setIcon(R.drawable.ic_message)
                        router.openChatActivity(
                            (requireActivity() as VideoCallActivity),
                            identity.toString(),
                            infoDetail?.name,
                            ChatFragment.VideoView.TWILLIO
                        )
                    }
                    R.id.miVideo -> {
                        when (enableResponse?.video) {
                            true -> {
                                if (localVideoTrack?.isEnabled == true) {
                                    viewModel?.setVideoState(false)

                                } else {
                                    viewModel?.setVideoState(true)
                                }
                            }
                            false -> {
                                item.title = getString(R.string.str_start_video)
                                item.icon = ContextCompat.getDrawable(
                                    requireContext(),
                                    R.drawable.ic_stop_video
                                )
                                localVideoTrack?.enable(false)
                            }
                        }

                    }
                    R.id.miMic -> {
                        if (localAudioTrack?.isEnabled == true) {
                            localAudioTrack?.enable(false)
                            item.title = getString(R.string.str_start_mic)
                            item.icon = ContextCompat.getDrawable(
                                requireContext(),
                                R.drawable.ic_mic_mute
                            )
                            videoCallMicSecond.videoCallMicSecondImage.setImageResource(R.drawable.ic_mic_mute)
                        } else {
                            localAudioTrack?.enable(true)
                            item.title = getString(R.string.str_stop_mic)
                            item.icon = ContextCompat.getDrawable(
                                requireContext(),
                                R.drawable.ic_mic_on
                            )
                            videoCallMicSecond.videoCallMicSecondImage.setImageResource(R.drawable.ic_mic_on)
                        }
                    }
                    R.id.miScreen -> {
                        if (item.title.equals("Bagikan Layar")) {
                            if (Build.VERSION.SDK_INT >= 29) {
                                screenCapturerManager?.startForeground()
                            }
                            if (screenCapturer == null) {
                                requestScreenCapture()
                            } else {
                                startScreenCapture()
                            }
                        } else if (item.title.equals("Stop Layar")) {
                            item.title = "Bagikan Layar"
                            if (Build.VERSION.SDK_INT >= 29) {
                                screenCapturerManager?.endForeground()
                            }
                            stopScreenCapture()
//                            stopScreenCapture()
                        }
                    }

                    R.id.miAnother -> {
                    }
                }
                true
            }

        }

        audioSwitch.start { audioDevices, audioDevice ->
            updateAudioDeviceIcon(audioDevice)
        }

        sizingShareScreen()

    }

    override fun onStart() {
        super.onStart()
        if (callMethod.equals(Constant.EXTRA_CALL_METHOD_MA)) {
            viewModel?.connectSocket(appointmentId,Constant.EXTRA_CALL_METHOD_MA)
        } else {
            viewModel?.connectSocket(appointmentId,Constant.EXTRA_CALL_METHOD_SPECIALIST)
        }

    }

    private fun stateMicPrimary(status: String) {
        if (status == "onParticipantDisconnected") {
            videoCallMicSecond.videoCallMicSecondText.text = roomActive?.localParticipant?.identity ?: "-"
        } else if (status == "CONNECTED") {
            videoCallMicSecond.videoCallMicSecondText.text = roomActive?.localParticipant?.identity ?: "-"
            videoCallMicPrimary.videoCallMicPrimaryName.text = roomActive?.remoteParticipants?.firstOrNull()?.identity.toString() ?: "-"
        }
    }

    private fun onVideoEnable() {
        if (viewBinding?.videoCallSecodView?.isVisible == true) {
            videocallScreenOffLocal.isVisible = false
        } else {
            videocallScreenOff.isVisible = false
        }
    }


    private fun onVideoDisable() {
        if (viewBinding?.videoCallSecodView?.isVisible == true) {
            videocallScreenOffLocal.isVisible = true
        } else {
            videocallScreenOff.isVisible = true
        }
    }

    override fun initUiListener() {
        viewBinding?.run {
            videoCallEnd.onSingleClick().subscribe {
                showDialogBackConfirmationPayment(
                    requireContext(),
                    R.string.video_call_close_confirmation
                ) {
                    viewModel?.onAcceptEndVideo()
                }
            }.disposedBy(disposable)

            videoCallBtnFlipCamera.onSingleClick(1000)
                .subscribe {
                    if (enableResponse?.video == true) {
                        switchCamera()
                    }
                }
                .disposedBy(disposable)

            videoCallBtnSpeaker.onSingleClick()
                .subscribe { showAudioDevices() }
                .disposedBy(disposable)

            videoCallBtnInfo.setOnClickListener {
                showDialog(requireContext(), infoDetail)
            }

        }

        videoCallDisplayDisconnect.contentDisconnectCloseBtn.onSingleClick().subscribe {
            videoCallDisplayDisconnect.isVisible = false
        }.disposedBy(disposable)

    }

    override fun observeViewModel(viewModel: VideoCallVM) {
        viewModel.videoCallProviderTwillio.subscribe {
            getResult(it)
        }.disposedBy(disposable)
        viewModel.chatProviderTwillio.subscribe {
            connectTwilioChat(it)
        }.disposedBy(disposable)
        observe(viewModel.enableEvent, ::getEnableResult)
        observe(viewModel.localVideoTrackEvent, ::initVideo)
        observe(viewModel.localAudioTrackEvent, ::initAudio)
        observe(viewModel.roomTrackEvent, ::getRoom)
        observe(viewModel.statusEventTrack, ::getStatusRoom)
        observe(viewModel.remoteVideoTrack, ::addRemoteParticipantVideo)
        observe(viewModel.cameraCapturerTrackEvent, ::getCameraCapture)
        observe(viewModel.remoteAudioTrack, ::getRemoteAudioTrack)
        observe(viewModel.remoteParticipant, ::getRemoteParticipant)
        observe(viewModel.endCallConfirmationEvent, { endCall() })
        observe(viewModel.stateVideo, ::handleVideoState)
        observe(viewModel.speedTrackEvent, ::handleSpeedTrack)
    }

    private fun handleSpeedTrack(trackingSpeed: TrackingSpeed?) {
        trackingSpeed?.let { mSpeed ->
            if (mSpeed.speedRaw < 50000) {
                viewBinding?.videoCallTrackConnection?.videoCallTextConnection?.text =
                    "${mSpeed.speedValue}${mSpeed.speedUnit}"
                viewBinding?.videoCallTrackConnection?.videoCallIconBar?.setImageResource(
                    R.drawable.ic_bar_network_red
                )
                onDelayDisconnected()
            } else {
                viewBinding?.videoCallTrackConnection?.videoCallIconBar?.setImageResource(
                    R.drawable.ic_bar_network_green
                )
                viewBinding?.videoCallTrackConnection?.videoCallTextConnection?.text =
                    "${mSpeed.speedValue}${mSpeed.speedUnit}"
            }

        }
    }

    override fun handleFailure(failure: Failure?) {
        when (failure) {
            is Failure.NetworkConnection -> (activity as BaseActivity<*>).showErrorSnackbar(
                getString(R.string.error_disconnect)
            )
            is Failure.ServerError ->{
                (activity as BaseActivity<*>).showErrorSnackbar(failure.message)
                Sentry.captureMessage(failure.message ?: "-")
            }
            is Failure.ExpiredSession -> {
                showToast(getString(R.string.session_expired_error_toast))
                startActivity(
                    LoginActivityRouter.createIntent(requireContext())
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                )
                requireActivity().finish()
            }
            else -> {
                (activity as BaseActivity<*>).showErrorSnackbar(getString(R.string.error_default))
                Sentry.captureMessage(failure.toString() ?: "-")
            }
        }
    }

    private fun handleVideoState(status: Boolean?) {
        if (status == true) {
            viewBinding?.videoCallBtmNavigationView?.menu?.findItem(R.id.miVideo)
                ?.setIcon(R.drawable.ic_video_camera_on)
            viewBinding?.videoCallBtmNavigationView?.menu?.findItem(R.id.miVideo)?.title =
                getString(R.string.stop_video)
            localVideoTrack?.enable(true)
            videocallScreenOffLocal.isVisible = false

        } else {
            viewBinding?.videoCallBtmNavigationView?.menu?.findItem(R.id.miVideo)
                ?.setIcon(R.drawable.ic_stop_video)
            viewBinding?.videoCallBtmNavigationView?.menu?.findItem(R.id.miVideo)?.title =
                getString(R.string.str_start_video)
            localVideoTrack?.enable(false)
            videocallScreenOffLocal.isVisible = true
        }
    }

    private fun getEnableResult(enableResponse: Enable?) {
        this.enableResponse = enableResponse
    }

    private fun getRemoteParticipant(remoteParticipant: RemoteParticipant?) {
        try {
            this.remoteParticipant = remoteParticipant
            remoteParticipant?.let {
                removeRemoteParticipant(it)
            }
        } catch (e: Exception) {
            Sentry.captureException(e)
        }
    }

    fun getStatusRoom(statusEventTrack: String?) {
        if (statusEventTrack.equals("onVideoTrackUnpublished")) {
            shareScreenVideoTrack?.removeSink(videoCallShareView)
            videoCallShareView.visibility = View.GONE
            videoCallPrimaryView.visibility = View.VISIBLE
            videoCallSecodView.visibility = View.INVISIBLE
            viewBinding?.videoCallSecodView?.visibility = View.VISIBLE
        } else if (statusEventTrack.equals("onVideoTrackEnabled")) {
            videocallScreenOff.visibility = View.GONE
        } else if (statusEventTrack.equals("onVideoTrackDisabled")) {
            videocallScreenOff.visibility = View.VISIBLE
        } else if (statusEventTrack.equals("onAudioTrackEnabled")) {
            videoCallMicPrimary.videoCallMicPrimaryImage.setImageResource(R.drawable.ic_mic_on)
        } else if (statusEventTrack.equals("onAudioTrackDisabled")) {
            videoCallMicPrimary.videoCallMicPrimaryImage.setImageResource(R.drawable.ic_mic_mute)
        } else if (statusEventTrack.equals("onParticipantConnected")) {
            handleLoadingReconect("", false)
            localVideoTrack?.addSink(videoCallSecodView)
            videocallScreenOffSignal.isVisible = false
            videocallScreenOffLocal.isVisible = false
            videoCallDisplayDisconnect.isVisible = false
        } else if (statusEventTrack.equals("onParticipantDisconnected")) {
            if (videoCallPrimaryView.isActivated == false) {
                handleLoadingReconect("", false)
                videoCallDisplayDisconnect.isVisible = true
                videoCallReconnecting.dialogImgCallVideo.stopRippleAnimation()
                videoCallReconnecting.contentReconnectingText.text =
                    getString(R.string.str_end_call_reconnecting)
                if (callMethod.equals(Constant.EXTRA_CALL_METHOD_MA)) {
                    videoCallDisplayDisconnect.apply {
                        this.loadingText.text =
                            getString(R.string.str_disconnect_from_dashboard_alert)
                    }
                } else {
                    videoCallDisplayDisconnect.apply {
                        this.loadingText.text =
                            getString(R.string.str_disconnect_from_dashboard_alert_specialist)
                    }
                }
            }
        } else if (statusEventTrack.equals("onReconnectingLocal")) {
            localVideoTrack?.removeSink(videoCallSecodView)
            videocallScreenOffSignal.isVisible = true
            videocallScreenOffLocal.isVisible = true
            handleLoadingReconect(
                "Koneksi Anda tidak stabil ${System.getProperty("line.separator")}Mencoba menghubungkan kembali.",
                true
            )
        } else if (statusEventTrack.equals("onReconnectingRemote")) {
            if (callMethod.equals(Constant.EXTRA_CALL_METHOD_MA)) {
                handleLoadingReconect(
                    "Sinyal Medical Advisor sedang tidak stabil${System.getProperty("line.separator")}Mencoba menghubungkan kembali.",
                    true
                )
            }else{
                handleLoadingReconect(
                    "Sinyal Dokter Specialist sedang tidak stabil${System.getProperty("line.separator")}Mencoba menghubungkan kembali.",
                    true
                )
            }
        } else if (statusEventTrack.equals("onReconnected")) {
            handleLoadingReconect("", false)
            localVideoTrack?.addSink(videoCallSecodView)
            videocallScreenOffSignal.isVisible = false
            videocallScreenOffLocal.isVisible = false
            videoCallDisplayDisconnect.isVisible = false
        }else if (statusEventTrack.equals("onParticipantReconnected")){
            handleLoadingReconect("", false)
            videoCallDisplayDisconnect.isVisible = false
        } else if (statusEventTrack.equals("onDisconnected")) {
            handleLoadingReconect("", false)
        } else if (statusEventTrack.equals("onConnectFailure")) {
            handleLoadingReconect("", false)
            videoCallDisplayDisconnect.isVisible = true
            videoCallReconnecting.dialogImgCallVideo.stopRippleAnimation()
            videoCallReconnecting.contentReconnectingText.text = getString(R.string.str_end_call_reconnecting)
            if (callMethod.equals(Constant.EXTRA_CALL_METHOD_MA)){
                videoCallDisplayDisconnect.apply {
                    this.loadingText.text = getString(R.string.str_disconnect_from_dashboard_alert)
                }
            }else{
                videoCallDisplayDisconnect.apply {
                    this.loadingText.text = getString(R.string.str_disconnect_from_dashboard_alert_specialist)
                }
            }
        }else if(statusEventTrack.equals("onMessageAdded")){
            viewBinding?.videoCallBtmNavigationView?.menu?.findItem(R.id.miMessage)
                ?.setIcon(R.drawable.ic_message_add)
        } else {

        }
    }

    private fun onDelayDisconnected() {
        Completable.timer(120000, TimeUnit.MILLISECONDS,AndroidSchedulers.mainThread())
            .subscribe {
                if (viewModel?.statusEventTrack?.value?.equals(Room.State.DISCONNECTED) == true || viewModel?.statusEventTrack?.value.equals("onConnectFailure")){
                    if (localVideoTrack?.isEnabled == true && roomActive != null) {
                        onDisconnectState()
                    }else{
                        videoCallDisplayDisconnect.isVisible = true
                        videoCallReconnecting.dialogImgCallVideo.stopRippleAnimation()
                        videoCallReconnecting.contentReconnectingText.text = getString(R.string.str_end_call_reconnecting)
                        if (callMethod.equals(Constant.EXTRA_CALL_METHOD_MA)){
                            videoCallDisplayDisconnect.apply {
                                this.loadingText.text = getString(R.string.str_disconnect_from_dashboard_alert)
                            }
                        }else{
                            videoCallDisplayDisconnect.apply {
                                this.loadingText.text = getString(R.string.str_disconnect_from_dashboard_alert_specialist)
                            }
                        }
                    }
                }
            }.disposedBy(disposable)
    }

    private fun getRemoteAudioTrack(remoteAudioTrack: RemoteAudioTrack?) {
        try {
            this.remoteAudioTrack = remoteAudioTrack
        } catch (e: Exception) {
            Sentry.captureException(e)
        }

    }

    private fun initVideo(localVideoTrack: LocalVideoTrack?) {
        try {
            viewBinding?.run {
                videoCallPrimaryView.mirror = true
                localVideoView = videoCallPrimaryView
                localVideoTrack?.addSink(videoCallPrimaryView)
            }
            this.localVideoTrack = localVideoTrack
        } catch (e: Exception) {
            Sentry.captureException(e)
        }
    }

    private fun getCameraCapture(cameraCapturer: CameraCapturerCompat?) {
        try {
            this.cameraCapture = cameraCapturer
        } catch (e: Exception) {
            Sentry.captureException(e)
        }
    }

    private fun getRoom(room: Pair<Room,String>?) {
        try {
            roomActive = room?.first
            stateMicPrimary(room?.first?.state.toString())
            handleLoadingReconect("", false)
            if (room?.first?.state == Room.State.CONNECTED || room?.first?.state?.equals("onParticipantConnected") == true) {
                videoCallReconnecting.isVisible = false
                audioSwitch.activate()
                videoCallTvTimer.isVisible = true
                videoCallTvTimer.start()
                room.first.remoteParticipants.forEach {
                    viewModel?.addRemoteParticipant(it)
                }
                videoCallDisplayDisconnect.isVisible = false
                videocallScreenOffSignal.isVisible = false
                videocallScreenOffLocal.isVisible = false
                roomActive?.remoteParticipants?.firstOrNull()?.remoteVideoTracks?.firstOrNull()?.remoteVideoTrack?.addSink(
                    videoCallPrimaryView
                )
                localVideoTrack?.addSink(videoCallSecodView)
                localVideoTrack?.let {
                    if (roomActive?.localParticipant?.videoTracks?.size == 0) {
                        roomActive?.localParticipant?.publishTrack(it)
                    }

                }

            } else if (room?.first?.state == Room.State.DISCONNECTED) {
                onDisconnectState()
            }else if (room?.second.equals("onDisconnected")){
                onDisconnectState()
            }
        } catch (e: Exception) {
            Sentry.captureException(e)
        }
    }

    private fun onDisconnectState() {
        videoCallDisplayDisconnect.isVisible = true
        if (callMethod.equals(Constant.EXTRA_CALL_METHOD_MA)){
            videoCallDisplayDisconnect.apply {
                this.loadingText.text = getString(R.string.str_disconnect_from_dashboard_alert)
            }
        }else{
            videoCallDisplayDisconnect.apply {
                this.loadingText.text = getString(R.string.str_disconnect_from_dashboard_alert_specialist)
            }
        }
        roomActive?.remoteParticipants?.firstOrNull()?.remoteVideoTracks?.firstOrNull()?.remoteVideoTrack?.removeSink(
            videoCallPrimaryView
        )
        localVideoTrack?.removeSink(videoCallSecodView)
        videocallScreenOffSignal.isVisible = true
        videocallScreenOffLocal.isVisible = true
    }

    private fun initAudio(localAudioTrack: LocalAudioTrack?) {
        try {
            this.localAudioTrack = localAudioTrack
        } catch (e: Exception) {
            Sentry.captureException(e)
        }
    }

    private fun getResult(appointmentRoom: Twilio?) {
        appointmentRoom?.let {
            if (it != null) {
                WebRtcAudioUtils.setWebRtcBasedAcousticEchoCanceler(true)
                WebRtcAudioUtils.setWebRtcBasedNoiseSuppressor(true)
                WebRtcAudioUtils.setWebRtcBasedAutomaticGainControl(true)
                tvi.webrtc.voiceengine.WebRtcAudioManager.setBlacklistDeviceForOpenSLESUsage(
                    false
                )
                WebRtcAudioUtils.deviceIsBlacklistedForOpenSLESUsage()
                viewModel?.onReceiveToken(
                    requireContext(),
                    it.roomCode.toString(),
                    it.token.toString(),
                    it.enable!!,
                )
                token = it.token.toString()
                valueRoom = it.roomCode.toString()
                identity = it.identity.toString()
                if (it.enable?.video == false) {
                    videocallScreenOff.visibility = View.VISIBLE
                    videocallScreenOffLocal.visibility = View.VISIBLE
                    viewBinding?.videoCallBtmNavigationView?.menu?.findItem(R.id.miVideo)
                        ?.setIcon(R.drawable.ic_stop_video)
                    viewBinding?.videoCallBtmNavigationView?.menu?.findItem(R.id.miVideo)?.title =
                        getString(R.string.str_start_video)
                }

            }
        }
    }

    private fun requestScreenCapture() {

        val mediaProjection: MediaProjectionManager =
            requireContext().getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager

        startActivityForResult(
            mediaProjection.createScreenCaptureIntent(),
            REQUEST_MEDIA_PROJECTION
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_MEDIA_PROJECTION) {
            if (resultCode == AppCompatActivity.RESULT_OK) {
                screenCapturer =
                    ScreenCapturer(
                        requireContext(),
                        resultCode,
                        data!!,
                        screenCapturerListener
                    )
                startScreenCapture()
                viewBinding?.videoCallBtmNavigationView?.menu?.findItem(R.id.miScreen)?.title =
                    "Stop Layar"
            } else {

            }

        }
    }

    private val screenCapturerListener: ScreenCapturer.Listener =
        object : ScreenCapturer.Listener {
            override fun onScreenCaptureError(errorDescription: String) {

            }

            override fun onFirstFrameAvailable() {

            }


        }

    private fun addRemoteParticipantVideo(videoTrack: RemoteVideoTrack?) {
        try {
            if (videoTrack?.name?.contains("-share-screen") == true) {
                videoCallSecodView.visibility = View.INVISIBLE
                viewBinding?.videoCallShareView?.visibility = View.VISIBLE
                viewBinding?.videoCallPrimaryView?.visibility = View.GONE
                viewBinding?.videoCallSecodView?.visibility = View.VISIBLE
                videoTrack?.addSink(videoCallShareView)
            } else {
                viewBinding?.videoCallPrimaryView?.visibility = View.VISIBLE
                moveLocalVideoToSecondView()
                videoCallPrimaryView.setMirror(false)
                videoTrack?.addSink(videoCallPrimaryView)
                remoteVideoTrack = videoTrack
            }
        } catch (e: Exception) {
            Sentry.captureException(e)
        }

    }

    private fun moveLocalVideoToSecondView() {
        videoCallSecodView.visibility = View.INVISIBLE
        videoCallSecodView.visibility = View.VISIBLE
        this.localVideoTrack?.removeSink(videoCallPrimaryView)
        this.localVideoTrack?.addSink(videoCallSecodView)
        localVideoView = videoCallSecodView
        videoCallSecodView.mirror = true
    }

    override fun onResume() {
        super.onResume()
        if ( viewModel?.statusEventTrack?.value.equals("onConnectFailure")){
            handleLoadingReconect("", false)
            videoCallDisplayDisconnect.isVisible = true
            videoCallReconnecting.dialogImgCallVideo.stopRippleAnimation()
            videoCallReconnecting.contentReconnectingText.text = getString(R.string.str_end_call_reconnecting)
            if (callMethod.equals(Constant.EXTRA_CALL_METHOD_MA)){
                videoCallDisplayDisconnect.apply {
                    this.loadingText.text = getString(R.string.str_disconnect_from_dashboard_alert)
                }
            }else{
                videoCallDisplayDisconnect.apply {
                    this.loadingText.text = getString(R.string.str_disconnect_from_dashboard_alert_specialist)
                }
            }
        }
        publishVideoTrack()

    }

    fun publishVideoTrack() {
        try {
            cameraCapture = CameraCapturerCompat.newInstance(requireContext())
            localVideoTrack = if (localVideoTrack == null) {
                LocalVideoTrack.create(
                    requireContext(),
                    true,
                    cameraCapture!!,
                    ConstantExtra.LOCAL_VIDEO_TRACK_NAME
                )
            } else {
                localVideoTrack
            }
            if (localVideoView != null) {
                localVideoTrack = LocalVideoTrack.create(
                    requireContext(),
                    true,
                    cameraCapture!!,
                    ConstantExtra.LOCAL_VIDEO_TRACK_NAME
                )
                localVideoTrack!!.addSink(videoCallSecodView)
            }
            if (viewModel?.getShareScreenFlag() == true) {
                viewBinding?.videoCallSecodView?.visibility = View.INVISIBLE
                viewBinding?.videoCallSecodView?.visibility = View.VISIBLE
            } else {
                onPauseLocalVideoTrack?.let {
                    roomActive?.localParticipant?.unpublishTrack(
                        it
                    )
                }
                localVideoTrack?.let { roomActive?.localParticipant?.publishTrack(it) }
            }
        } catch (e: Exception) {
            Sentry.captureException(e)
        }
    }

    fun unPublishLocalVideoTrack() {
//        localVideoTrack?.let { roomActive?.localParticipant?.unpublishTrack(it) }
        viewModel?.setVideoState(false)
    }

    override fun onPause() {
        super.onPause()
        onPauseLocalVideoTrack = localVideoTrack
    }

    override fun onDestroy() {
        if (localVideoTrack?.isEnabled == true && roomActive != null) {
            audioSwitch.stop()
            viewModel?.onAcceptEndVideo()
            viewModel?.invalidateShareScreen()
        }
        viewModel?.disconnect()
        super.onDestroy()
    }


    private fun switchCamera() {
        cameraCapture?.switchCamera()
    }

    private fun endCall() {
        //send event to moengage
        viewModel?.sendEventEndCallMaToAnalytics(
            appointmentId.toString(),
            orderCode.toString(),
            valueRoom.toString()
        )

        val time = SystemClock.elapsedRealtime() - videoCallTvTimer.base
        val h = (time / 3600000).toInt()
        val m = (time - h * 3600000).toInt() / 60000
        val s = (time - h * 3600000 - m * 60000).toInt() / 1000
        val t =
            (if (h < 10) "0$h" else h).toString() + ":" + (if (m < 10) "0$m" else m) + ":" + if (s < 10) "0$s" else s
        videoCallTvTimer.stop()
        destroyVideoAndChat()
        viewModel?.disconnect()

        if (callMethod.equals(Constant.EXTRA_CALL_METHOD_MA)) {
            router.openEndCallActivity(
                (requireActivity() as VideoCallActivity),
                t,
                orderCode ?: "",
                appointmentId
            )
        } else {
            router.openEndCallSpecialistActivity(
                (requireActivity() as VideoCallActivity),
                t,
                appointmentId
            )
        }
    }

    private fun removeRemoteParticipant(remoteParticipant: RemoteParticipant) {

        /*
 * Remove participant renderer
 */
        this.remoteParticipant = remoteParticipant
        remoteParticipant.remoteVideoTracks.firstOrNull()
            ?.let { remoteVideoTrackPublication ->
                if (remoteVideoTrackPublication.isTrackSubscribed) {
                    remoteVideoTrackPublication.remoteVideoTrack?.let {
                        removeParticipantVideo(
                            it
                        )
                    }
                }
            }
        moveLocalVideoToPrimaryView()
    }

    private fun removeParticipantVideo(videoTrack: VideoTrack) {
        videoTrack.removeSink(videoCallPrimaryView)
    }

    private fun moveLocalVideoToPrimaryView() {
        if (videoCallSecodView.visibility == View.VISIBLE) {
            videoCallSecodView.visibility = View.INVISIBLE
            with(localVideoTrack) {
                this?.removeSink(videoCallSecodView)
                this?.removeSink(videoCallPrimaryView)
            }
            localVideoView = videoCallPrimaryView
            videoCallPrimaryView.mirror = true
            videocallScreenOffLocal.isVisible = true
        }
    }

    private fun startScreenCapture() {
        viewBinding?.videoCallBtmNavigationView?.menu?.findItem(R.id.miScreen)?.title =
            "Stop Layar"
        viewModel?.setShareScreenFlag(true)
        shareScreenVideoTrack = LocalVideoTrack.create(
            requireContext(),
            true,
            screenCapturer!!, "share-screenremote"
        )
        viewBinding?.run {
            videoCallThreeView.visibility = View.VISIBLE
            videoCallSecodView.visibility = View.INVISIBLE
            videoCallSecodView.visibility = View.VISIBLE
            shareScreenVideoTrack?.addSink(videoCallThreeView)
            localVideoView = videoCallThreeView
            if (roomActive?.localParticipant != null && shareScreenVideoTrack != null) {
                roomActive?.localParticipant?.publishTrack(shareScreenVideoTrack!!)
                roomActive?.localParticipant?.unpublishTrack(localVideoTrack!!)
            }
        }

    }

    private fun stopScreenCapture() {
        viewModel?.setShareScreenFlag(false)
        viewBinding?.videoCallThreeView?.visibility = View.INVISIBLE
        viewBinding?.videoCallSecodView?.visibility = View.INVISIBLE
        viewBinding?.videoCallSecodView?.visibility = View.VISIBLE
        viewBinding?.videoCallShareView?.visibility = View.GONE
        viewBinding?.videoCallPrimaryView?.visibility = View.VISIBLE
        if (shareScreenVideoTrack != null && localVideoTrack != null) {
            roomActive?.localParticipant?.unpublishTrack(shareScreenVideoTrack!!)
            roomActive?.localParticipant?.publishTrack(localVideoTrack!!)
        }

    }

    override fun onReExecute() {}

    override fun initMenu(): Int = 0


    private fun showAudioDevices() {
        val availableAudioDevices = audioSwitch.availableAudioDevices

        audioSwitch.selectedAudioDevice?.let { selectedDevice ->
            val audioDeviceNames = ArrayList<String>()
            for (a in availableAudioDevices) {
                audioDeviceNames.add(a.name)
            }
            audioDeviceNames.add(getString(R.string.str_mute_unmute))
            val selectedDeviceIndex = if (isSpeakerMute) {
                audioDeviceNames.size - 1
            } else {
                availableAudioDevices.indexOf(selectedDevice)
            }
            AlertDialog.Builder(requireContext())
                .setTitle(getString(R.string.choose_audio))
                .setSingleChoiceItems(
                    audioDeviceNames.toTypedArray<CharSequence>(),
                    selectedDeviceIndex
                ) { dialog, index ->
                    dialog.dismiss()
                    val selectedAudioDevice = if (index == audioDeviceNames.size - 1) {
                        null
                    } else {
                        availableAudioDevices[index]
                    }
                    updateAudioDeviceIcon(selectedAudioDevice)

                }.create().show()

        }
    }


    /*
* Update the menu icon based on the currently selected audio device.
*/
    private fun updateAudioDeviceIcon(selectedAudioDevice: AudioDevice?) {
        if (selectedAudioDevice == null) {
            remoteAudioTrack?.enablePlayback(false)
            setIconImage(R.drawable.ic_volume_mute)
            isSpeakerMute = true
        } else {
            isSpeakerMute = false
            audioSwitch.selectDevice(selectedAudioDevice)
            remoteAudioTrack?.enablePlayback(true)
            when (selectedAudioDevice) {
                is BluetoothHeadset -> setIconImage(R.drawable.ic_headphone)
                is WiredHeadset -> setIconImage(R.drawable.ic_headphone)
                is Speakerphone -> setIconImage(R.drawable.ic_volume_on)
                is Earpiece -> setIconImage(R.drawable.ic_volume_on)
            }
        }


    }

    private fun sizingShareScreen() {
        val metrics = DisplayMetrics()
        activity?.windowManager?.defaultDisplay?.getMetrics(metrics)
        val params = viewBinding?.videoCallShareView?.layoutParams
        params?.height = (250 * metrics.density).toInt()
        viewBinding?.videoCallShareView?.layoutParams = params
    }

    private fun setIconImage(audioDeviceMenuIcon: Int) {
            viewBinding?.videoCallBtnSpeaker?.background =
                ContextCompat.getDrawable(requireContext(), audioDeviceMenuIcon ?: R.drawable.ic_volume_on)

    }

    private fun destroyVideoAndChat() {
        roomActive?.disconnect()
        viewModel?.leaveChannelChat()
        screenCapturerManager?.endForeground()
        screenCapturerManager?.unbindService()
        cameraCapture?.stopCapture()
        cameraCapture?.dispose()
        localAudioTrack?.release()
        localVideoTrack?.release()
        shareScreenVideoTrack?.release()
        onPauseLocalVideoTrack?.release()
        remoteParticipant = null
        localVideoView = null
        remoteAudioTrack = null
        remoteVideoTrack = null
    }

    /**
     * Connect to twilio chat, if success, bind chat client to viewmodel and load channel
     * if fail set twilio chat client error
     */
    private fun connectTwilioChat(twilioData : Twilio) {
        ChatClient.create(requireContext(),
            twilioData.token ?: "",
            viewModel?.getChatClientProperties()!!,
            object : CallbackListener<ChatClient?>() {
                override fun onSuccess(chatClient: ChatClient?) {
                    if (chatClient != null) {
                        viewModel?.chatClient = chatClient
                        viewModel?.loadChannels()
                    } else {
                        viewModel?.setTwilioChatError()
                        showToast(getString(R.string.error_default))
                    }
                }

                override fun onError(errorInfo: ErrorInfo?) {
                    super.onError(errorInfo)
                    viewModel?.setTwilioChatError()
                    showToast(getString(R.string.error_default))
                    Timber.e(errorInfo?.message)
                    errorInfo?.message?.let { Sentry.captureMessage(it) }
                }
            }
        )
    }
}




