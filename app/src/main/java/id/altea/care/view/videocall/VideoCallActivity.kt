package id.altea.care.view.videocall

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import id.altea.care.R
import id.altea.care.core.base.BaseActivity
import id.altea.care.core.base.BaseActivityVM
import id.altea.care.core.domain.event.AppointmentStatusUpdateEvent
import id.altea.care.core.domain.model.AppointmentProvider
import id.altea.care.core.domain.model.InfoDetail
import id.altea.care.core.domain.model.Twilio
import id.altea.care.core.domain.model.UserProfile
import id.altea.care.core.exception.Failure
import id.altea.care.core.ext.disposedBy
import id.altea.care.core.ext.observe
import id.altea.care.core.ext.showDialogBackConfirmationPayment
import id.altea.care.core.helper.RxBus
import id.altea.care.databinding.ActivityVideoCallBinding
import id.altea.care.view.consultation.ConsultationFragment.Companion.KEY_DATA_PATIENT
import id.altea.care.view.login.LoginActivityRouter
import id.altea.care.view.videocall.chat.ChatFragment
import id.altea.care.view.videocall.videofragment.VideoFragment
import id.altea.care.view.videocall.videowebview.VideoFragmentWebView
import io.sentry.Sentry
import kotlinx.android.synthetic.main.activity_video_call.*
import kotlinx.android.synthetic.main.content_reconnecting.view.*
import kotlinx.android.synthetic.main.fragment_video_call.*
import kotlinx.android.synthetic.main.fragment_video_call.videoCallReconnecting


class VideoCallActivity : BaseActivityVM<ActivityVideoCallBinding, VideoCallVM>() {

    val router by lazy { VideoCallRouter() }
    var videoViewType : ChatFragment.VideoView? = null

    companion object {
        private const val APPOINTMENT_ID = "APPOINTMENT_ID"
        private const val EXTRA_ORDER_CODE = "VideoCall.OrderCode"
        private const val TYPE_CALL_METHOD = "ExtraType.CallMethod"
        private const val TYPE_INFO_DETAIL = "VideoCall.InfoDetail"
        fun createIntent(
            caller: Context,
            appointmentId: Int,
            orderCode: String,
            callMethod: String,
            profile: UserProfile?,
            infoDetail: InfoDetail?,
        ): Intent {
            return Intent(caller, VideoCallActivity::class.java)
                .putExtra(APPOINTMENT_ID, appointmentId)
                .putExtra(EXTRA_ORDER_CODE, orderCode)
                .putExtra(TYPE_CALL_METHOD, callMethod)
                .putExtra(KEY_DATA_PATIENT, profile)
                .putExtra(TYPE_INFO_DETAIL, infoDetail)
        }
    }

    override fun observeViewModel(viewModel: VideoCallVM) {
        observe(viewModel.isLoadingLiveData, ::handleLoading)
        observe(viewModel.failureLiveData, ::handleFailure)
        observe(viewModel.appointmentRoomProviderEvent, ::getResult)
    }

    private fun getResult(appointmentProvider: AppointmentProvider?) {
        appointmentProvider?.let {
                videoCallReconnectingActivity.isVisible = false
                videoCallReconnectingActivity.dialogImgCallVideo.stopRippleAnimation()
                when (it.videoCallProvider) {
                    "TWILIO" -> {
                        router.openVideoFragment(this)
                    }
                    "JITSI_WEB" -> {
                        router.openVideoWebViewFragment(this)
                    }
                }

        }
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
    }
    override fun bindViewModel(): VideoCallVM {
        return ViewModelProvider(this, viewModelFactory).get(VideoCallVM::class.java)
    }

    override fun enableBackButton(): Boolean = true

    override fun bindToolbar(): Toolbar? = null

    override fun getUiBinding(): ActivityVideoCallBinding {
        return ActivityVideoCallBinding.inflate(layoutInflater)
    }

    override fun onFirstLaunch(savedInstanceState: Bundle?) {
        videoCallReconnectingActivity.isVisible = true
        viewBinding?.run {
            videoCallReconnectingActivity.dialogImgCallVideo.startRippleAnimation()

        }

        baseViewModel?.bindArgument(
            intent.getIntExtra(APPOINTMENT_ID, -1),
            intent.getStringExtra(EXTRA_ORDER_CODE),
            intent.getStringExtra(TYPE_CALL_METHOD),
            intent.getParcelableExtra(KEY_DATA_PATIENT),
            intent.getParcelableExtra(TYPE_INFO_DETAIL)
        )
        baseViewModel?.doGetAppointmentRoom(baseViewModel?.appoimentId ?: 0)
//        router.openVideoFragment(this)
        listenRxBus()
    }

    override fun initUiListener() {}

    fun showFragment(fragment: Fragment, tag: String) {
        val fm = supportFragmentManager
        var currentFragment: Fragment? = null
        val fragments = fm.fragments
        for (f in fragments) {
            if (f.isVisible) {
                currentFragment = f
                break
            }
        }
        val newFragment = fm.findFragmentByTag(tag)
        if (currentFragment != null && newFragment != null && currentFragment == newFragment) return
        val transaction = fm.beginTransaction()
        if (newFragment == null) {
            transaction.add(R.id.frameContainer, fragment, tag)
        }
        if (currentFragment != null) {
            transaction.hide(currentFragment)
        }
        if (newFragment != null) {
            transaction.show(newFragment)
        }
        transaction.commitNow()
    }

    private fun listenRxBus() {
        RxBus.getEvents().subscribe {
            when (it) {
                is ChatFragment.VideoViewType -> {
                    videoViewType =  it.videoView
                }
            }
        }.disposedBy(disposable)
    }

    override fun onBackPressed() {
        supportFragmentManager.fragments.forEach {
            if (it.isVisible && it is ChatFragment) {
                when(videoViewType){
                    ChatFragment.VideoView.TWILLIO -> {
                        router.openVideoFragment(this)
                    }
                    ChatFragment.VideoView.JITSI ->{
                        router.openVideoWebViewFragment(this)
                    }
                }
                return
            } else if (it.isVisible && it is VideoFragment || it.isVisible && it is VideoFragmentWebView) {
                showDialogBackConfirmationPayment(this, R.string.video_call_close_confirmation) {
                    baseViewModel?.onAcceptEndVideo()
                }
                return
            }
        }
        super.onBackPressed()
    }

    override fun handleFailure(failure: Failure?) {
        when (failure) {
            is Failure.NetworkConnection -> (this@VideoCallActivity as BaseActivity<*>).showErrorSnackbar(
                getString(R.string.error_disconnect)
            )
            is Failure.ServerError ->{
                (this@VideoCallActivity as BaseActivity<*>).showErrorSnackbar(failure.message)
                Sentry.captureMessage(failure.message ?: "-")
            }
            is Failure.ExpiredSession -> {
                showToast(getString(R.string.session_expired_error_toast))
                startActivity(
                    LoginActivityRouter.createIntent(this)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                )
                this@VideoCallActivity.finish()
            }
            else -> {
                (this@VideoCallActivity as BaseActivity<*>).showErrorSnackbar(getString(R.string.error_default))
                Sentry.captureMessage(failure.toString() ?: "-")
            }
        }
    }
}
