package id.altea.care.view.videocall.videowebview

import android.annotation.SuppressLint
import android.content.Context
import android.media.AudioManager
import android.os.Bundle
import android.os.SystemClock
import android.view.View
import android.webkit.JavascriptInterface
import android.webkit.PermissionRequest
import android.webkit.WebChromeClient
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import com.twilio.audioswitch.AudioDevice
import com.twilio.audioswitch.AudioSwitch
import com.twilio.chat.CallbackListener
import com.twilio.chat.ChatClient
import com.twilio.chat.ErrorInfo
import id.altea.care.R
import id.altea.care.core.base.BaseFragmentVM
import id.altea.care.core.domain.model.Jitsi
import id.altea.care.core.domain.model.TrackingSpeed
import id.altea.care.core.domain.model.Twilio
import id.altea.care.core.ext.*
import id.altea.care.core.helper.util.Constant
import id.altea.care.databinding.FragmentVideoCallWebviewBinding
import id.altea.care.view.videocall.VideoCallActivity
import id.altea.care.view.videocall.VideoCallRouter
import id.altea.care.view.videocall.VideoCallVM
import id.altea.care.view.videocall.chat.ChatFragment
import io.sentry.Sentry
import kotlinx.android.synthetic.main.fragment_video_call_webview.*
import timber.log.Timber


class VideoFragmentWebView : BaseFragmentVM<FragmentVideoCallWebviewBinding, VideoCallVM>() {
    val router = VideoCallRouter()
    private val infoDetail by lazy { viewModel?.infoDetail }
    private val callMethod by lazy { viewModel?.typeConsultation }
    private val appointmentId: Int by lazy { viewModel?.appoimentId ?: -1 }
    private val orderCode by lazy { viewModel?.orderCode }
    private var identity: String? = ""
    private var valueRoom: String? = ""
    private var audioManager: AudioManager? = null
    private val audioSwitch by lazy {
        AudioSwitch(
            requireContext(), preferredDeviceList = listOf(
                AudioDevice.WiredHeadset::class.java,
                AudioDevice.BluetoothHeadset::class.java,
                AudioDevice.Speakerphone::class.java,
                AudioDevice.Earpiece::class.java

            )
        )
    }

    override fun getUiBinding(): FragmentVideoCallWebviewBinding {
        return FragmentVideoCallWebviewBinding.inflate(layoutInflater)
    }


    override fun onFirstLaunch(savedInstanceState: Bundle?, view: View) {
        viewModel?.doCalculate(requireContext())
        audioManager = requireActivity().getSystemService(Context.AUDIO_SERVICE) as AudioManager
        viewBinding?.videoCallReconnectingWebView?.root?.isVisible = true
        viewBinding?.videoCallReconnectingWebView?.dialogImgCallVideo?.startRippleAnimation()
        viewBinding?.videoCallReconnectingWebView?.contentReconnectingText?.text = getString(R.string.str_conecting)
        viewBinding?.videoCallWebView?.webChromeClient = object : WebChromeClient() {
            override fun onPermissionRequest(request: PermissionRequest?) {
                request?.grant(request.resources)
            }

        }
        viewBinding?.videoCallWebView?.getSettings()?.setMediaPlaybackRequiresUserGesture(false);
        viewBinding?.videoCallWebView?.addJavascriptInterface(
            WebAppInterface(
                requireContext()
            ), "NativeAndroid"
        )

    }


    override fun onStart() {
        super.onStart()
        if (callMethod.equals(Constant.EXTRA_CALL_METHOD_MA)) {
            viewModel?.connectSocket(appointmentId, Constant.EXTRA_CALL_METHOD_MA)
        } else {
            viewModel?.connectSocket(appointmentId, Constant.EXTRA_CALL_METHOD_SPECIALIST)
        }
    }

    override fun onReExecute() {}


    private fun showAudioDevices() {
        val availableAudioDevices = audioSwitch.availableAudioDevices

        audioSwitch.selectedAudioDevice?.let { selectedDevice ->
            val audioDeviceNames = ArrayList<String>()

            for (a in availableAudioDevices) {
                audioDeviceNames.add(a.name)
            }

//            audioDeviceNames.add(getString(R.string.str_mute_unmute))
            val selectedDeviceIndex =
                availableAudioDevices?.indexOf(selectedDevice)

            AlertDialog.Builder(requireContext())
                .setTitle(getString(R.string.choose_audio))
                .setSingleChoiceItems(
                    audioDeviceNames.toTypedArray<CharSequence>(),
                    selectedDeviceIndex ?: 0
                ) { dialog, index ->
                    dialog.dismiss()
                    val selectedAudioDevice = if (index == audioDeviceNames.size - 1) {
                        null
                    } else {
                        availableAudioDevices?.get(index)
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
//                remoteAudioTrack?.enablePlayback(false)
//                setIconImage(R.drawable.ic_volume_mute)
//                isSpeakerMute = true
        } else {
//                isSpeakerMute = false
            audioSwitch.selectDevice(selectedAudioDevice)
            audioManager?.adjustStreamVolume(
                AudioManager.STREAM_VOICE_CALL,
                AudioManager.ADJUST_LOWER,
                AudioManager.FLAG_PLAY_SOUND
            )
            audioManager?.adjustStreamVolume(
                AudioManager.STREAM_VOICE_CALL,
                AudioManager.ADJUST_LOWER,
                AudioManager.FLAG_PLAY_SOUND
            )
            audioManager?.adjustStreamVolume(
                AudioManager.STREAM_VOICE_CALL,
                AudioManager.ADJUST_RAISE,
                AudioManager.FLAG_PLAY_SOUND
            )
            audioManager?.adjustStreamVolume(
                AudioManager.STREAM_VOICE_CALL,
                AudioManager.ADJUST_RAISE,
                AudioManager.FLAG_PLAY_SOUND
            )
            audioManager?.adjustStreamVolume(
                AudioManager.STREAM_VOICE_CALL,
                AudioManager.ADJUST_RAISE,
                AudioManager.FLAG_PLAY_SOUND
            )
            audioManager?.adjustStreamVolume(
                AudioManager.STREAM_VOICE_CALL,
                AudioManager.ADJUST_RAISE,
                AudioManager.FLAG_PLAY_SOUND
            )
            audioManager?.adjustStreamVolume(
                AudioManager.STREAM_VOICE_CALL,
                AudioManager.ADJUST_RAISE,
                AudioManager.FLAG_PLAY_SOUND
            )
            audioManager?.adjustStreamVolume(
                AudioManager.STREAM_VOICE_CALL,
                AudioManager.ADJUST_RAISE,
                AudioManager.FLAG_PLAY_SOUND
            )
            audioManager?.adjustStreamVolume(
                AudioManager.STREAM_VOICE_CALL,
                AudioManager.ADJUST_RAISE,
                AudioManager.FLAG_PLAY_SOUND
            )

            when (selectedAudioDevice) {
                is AudioDevice.BluetoothHeadset -> setIconImage(R.drawable.ic_headphone)
                is AudioDevice.WiredHeadset -> setIconImage(R.drawable.ic_headphone)
                is AudioDevice.Speakerphone -> {
                    setIconImage(R.drawable.ic_volume_on)

                }
            }
        }


    }

    private fun setIconImage(audioDeviceMenuIcon: Int) {
        if (audioDeviceMenuIcon != 0) {
            viewBinding?.videoCallBtnSpeaker?.background =
                ContextCompat.getDrawable(requireContext(), audioDeviceMenuIcon)
        }
    }


    @SuppressLint("ClickableViewAccessibility")
    override fun initUiListener() {
        viewBinding?.run {
            floatingMessageVideoBtn.setOnClickListener {
                viewBinding?.floatingMessageVideoBtn?.setImageDrawable(context?.getDrawable(R.drawable.ic_message_floating))
                router.openChatActivity(
                    (requireActivity() as VideoCallActivity),
                    identity.toString(),
                    infoDetail?.name,
                    ChatFragment.VideoView.JITSI
                )
            }

            videoFragmentWebViewRefresh.onSingleClick().subscribe {
                viewModel?.onReinit(appointmentId)
            }.disposedBy(disposable)

            videoCallBtnInfoWebView.setOnClickListener {
                showDialog(requireContext(), infoDetail)
            }

            videoCallEnd.onSingleClick().subscribe {
                showDialogBackConfirmationPayment(
                    requireContext(),
                    R.string.video_call_close_confirmation
                ) {
                    viewModel?.onAcceptEndVideo()
                }
            }.disposedBy(disposable)

        }
    }

    override fun initMenu(): Int = 0

    override fun observeViewModel(viewModel: VideoCallVM) {
        viewModel.videoCallProviderJitsi.subscribe {
            getResultRoomProvider(it)
        }.disposedBy(disposable)
        viewModel.chatProviderTwillio.subscribe {
            connectTwilioChat(it)
        }.disposedBy(disposable)
        observe(viewModel.reInitEvent, { handleReinit() })
        observe(viewModel.speedTrackEvent, ::handleSpeedTrack)
        observe(viewModel.statusEventTrack, ::getStatusRoom)
        observe(viewModel.endCallConfirmationEvent, { endCall() })
    }

    private fun handleReinit() {
        audioManager = requireActivity().getSystemService(Context.AUDIO_SERVICE) as AudioManager
        viewBinding?.videoCallReconnectingWebView?.root?.isVisible = true
        viewBinding?.videoCallReconnectingWebView?.dialogImgCallVideo?.startRippleAnimation()
        viewBinding?.videoCallReconnectingWebView?.contentReconnectingText?.text = getString(R.string.str_conecting)
        viewBinding?.videoCallWebView?.webChromeClient = object : WebChromeClient() {
            override fun onPermissionRequest(request: PermissionRequest?) {
                request?.grant(request.resources)
            }

        }
        viewBinding?.videoCallWebView?.getSettings()?.setMediaPlaybackRequiresUserGesture(false);
        viewBinding?.videoCallWebView?.addJavascriptInterface(
            WebAppInterface(
                requireContext()
            ), "NativeAndroid"
        )


    }

    fun getStatusRoom(statusEventTrack: String?) {
        if (statusEventTrack.equals("onMessageAdded")) {
            viewBinding?.floatingMessageVideoBtn?.setImageDrawable(context?.getDrawable(R.drawable.ic_chat_with_dot_notif))
        }
    }

    private fun handleSpeedTrack(trackingSpeed: TrackingSpeed?) {
        trackingSpeed?.let { mSpeed ->
            if (mSpeed.speedRaw < 50000) {
                viewBinding?.videoCallTrackConnectionWebView?.videoCallTextConnection?.text =
                    "${mSpeed.speedValue}${mSpeed.speedUnit}"
                viewBinding?.videoCallTrackConnectionWebView?.videoCallIconBar?.setImageResource(
                    R.drawable.ic_bar_network_red
                )
            } else {
                viewBinding?.videoCallTrackConnectionWebView?.videoCallIconBar?.setImageResource(
                    R.drawable.ic_bar_network_green
                )
                viewBinding?.videoCallTrackConnectionWebView?.videoCallTextConnection?.text =
                    "${mSpeed.speedValue}${mSpeed.speedUnit}"
            }

        }
    }

    private fun getResultRoomProvider(jitsiData: Jitsi?) {
        jitsiData?.let {
            viewBinding?.videoCallTvTimerWebView?.isVisible = true
            viewBinding?.videoCallTvTimerWebView?.start()
            viewBinding?.videoCallWebView?.loadUrl(it.url.toString())
        }
    }

    override fun bindViewModel(): VideoCallVM {
        return ViewModelProvider(
            requireActivity().viewModelStore,
            viewModelFactory
        ).get(VideoCallVM::class.java)

    }

    private fun endCall() {
        //send event to moengage
        viewModel?.sendEventEndCallMaToAnalytics(
            appointmentId.toString(),
            orderCode.toString(),
            valueRoom.toString()
        )

        val time = SystemClock.elapsedRealtime() - videoCallTvTimerWebView.base
        val h = (time / 3600000).toInt()
        val m = (time - h * 3600000).toInt() / 60000
        val s = (time - h * 3600000 - m * 60000).toInt() / 1000
        val t =
            (if (h < 10) "0$h" else h).toString() + ":" + (if (m < 10) "0$m" else m) + ":" + if (s < 10) "0$s" else s

        videoCallTvTimerWebView.stop()
        viewModel?.disconnect()
        viewModel?.leaveChannelChat()
        viewBinding?.videoCallWebView?.loadUrl("about:blank")
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

    override fun onDestroy() {
        audioSwitch.stop()
        super.onDestroy()
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        if (viewModel?.isMeetStarted == true) {
            viewBinding?.videoCallWebView?.loadUrl("javascript:window.AlteaCareMeet.toggleVideo();setTimeout(() => { javascript:window.AlteaCareMeet.toggleVideo() }, 1000);")
        }
    }


    private fun connectTwilioChat(chatTwillio: Twilio?) {
        if (chatTwillio != null) {
            identity = chatTwillio.identity
            ChatClient.create(requireContext(),
                chatTwillio.token.toString(),
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
        } else {
            floatingMessageVideoBtn.isVisible = false
        }
    }

    inner class WebAppInterface(val context: Context) {
        @JavascriptInterface
        fun setMeetStarted() {
            audioSwitch.start { _, audioDevice ->
                updateAudioDeviceIcon(audioDevice)
            }
            audioSwitch.activate()
            viewModel?.isMeetStarted = true
            viewBinding?.videoCallReconnectingWebView?.root?.visibility = View.INVISIBLE
            viewBinding?.floatingMessageVideoBtn?.visibility = View.VISIBLE
//            viewBinding?.videoCallReconnectingWebView?.dialogImgCallVideo?.stopRippleAnimation()

        }
    }
}