package id.altea.care.view.transition

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.util.Log
import androidx.appcompat.widget.Toolbar
import androidx.core.text.HtmlCompat
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.adapters.ItemAdapter
import id.altea.care.R
import id.altea.care.core.base.BaseActivityVM
import id.altea.care.core.domain.model.*
import id.altea.care.core.exception.Failure
import id.altea.care.core.exception.NotFoundFailure
import id.altea.care.core.ext.*
import id.altea.care.databinding.ActivityTransitionVideoCallBinding
import id.altea.care.view.common.item.AppointmentEmptyItem
import id.altea.care.view.common.item.ItemInformationVideoCall
import id.altea.care.view.consultation.ConsultationFragment.Companion.KEY_DATA_PATIENT
import id.altea.care.view.home.failure.HomeFailure
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.activity_email_otp.*
import kotlinx.android.synthetic.main.bottomsheet_confirmantion_gp.*
import permissions.dispatcher.*
import timber.log.Timber
import java.util.concurrent.TimeUnit

@RuntimePermissions
class TransitionActivity : BaseActivityVM<ActivityTransitionVideoCallBinding, TransitionVM>() {

    private val router = TransitionRouter()
    private val appointmentId: Int by lazy { intent.getIntExtra(EXTRA_APPOINTMENT_ID, 0) }
    private val itemAdapter = ItemAdapter<ItemInformationVideoCall>()
    private val fastAdapter = FastAdapter.with(itemAdapter)

    private val orderCode by lazy { intent?.getStringExtra(EXTRA_ORDER_CODE) ?: "" }
    private val userProfile by lazy { intent.getParcelableExtra(KEY_DATA_PATIENT) as UserProfile? }
    private val infoDetail by  lazy { intent.getParcelableExtra(EXTRA_INFO_DETAIL) as InfoDetail? }

    override fun enableBackButton(): Boolean = false

    override fun bindToolbar(): Toolbar? = null

    override fun getUiBinding(): ActivityTransitionVideoCallBinding {
        return ActivityTransitionVideoCallBinding.inflate(layoutInflater)
    }

    override fun onFirstLaunch(savedInstanceState: Bundle?) {
        itemAdapter.add(
            ItemInformationVideoCall(InformationVideoCall(R.drawable.ic_pesonal_id,"Pada telekonsultasi pertama, <br><b>Anda wajib menyiapkan KTP</b> <br>untuk validasi identitas")),
            ItemInformationVideoCall(InformationVideoCall(R.drawable.ic_document,"Siapkan <b>dokumen penunjang <br>medis digital</b> yang diperlukan <br>(hasil lab, radiologi, dsb)")),
            ItemInformationVideoCall(InformationVideoCall(R.drawable.ic_phone_ringing,"Pastikan <b>koneksi internet stabil, <br>baterai cukup dan masuk ke <br>ruangan yang nyaman</b> agar <br>telekonsultasi berjalan lancar"))
        )
        viewBinding?.transtionVideoCallRecyrl?.apply {
            this.layoutManager = LinearLayoutManager(this@TransitionActivity)
            this.adapter = fastAdapter
        }
    }


    override fun onStart() {
        super.onStart()
        baseViewModel?.getCountDown(CountDownState.onConnect)

    }

    @Suppress("DEPRECATION")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        onRequestPermissionsResult(requestCode, grantResults)
    }

    override fun onStop() {
        baseViewModel?.disconnect()
        super.onStop()
    }

    @NeedsPermission(
        Manifest.permission.CAMERA,
        Manifest.permission.RECORD_AUDIO,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )
    fun connectSocket() {
        baseViewModel?.connectSocket(appointmentId)
    }

    @OnShowRationale(
        Manifest.permission.CAMERA,
        Manifest.permission.RECORD_AUDIO,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )
    fun showRationale(request: PermissionRequest) {
        showRationaleDialog(this, R.string.permission_call_deny, request)
    }

    @OnPermissionDenied(
        Manifest.permission.CAMERA,
        Manifest.permission.RECORD_AUDIO,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )
    fun onPermissionDenied() {
        connectSocketWithPermissionCheck()
    }

    @OnNeverAskAgain(
        Manifest.permission.CAMERA,
        Manifest.permission.RECORD_AUDIO,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )
    fun onCameraAndStorageNeverAskAgain() {
        showPermissionSettingDialog(this, R.string.permission_call_deny)
    }

    override fun initUiListener() {
        viewBinding?.run {
            trasitionBtnCancelCall.onSingleClick().subscribe {
                callBottomSheetCancel()
            }.disposedBy(disposable)
        }
    }

    override fun observeViewModel(viewModel: TransitionVM) {
        observe(viewModel.isLoadingLiveData, ::handleLoading)
        observe(viewModel.failureLiveData, ::handleFailure)
        observe(viewModel.answeredSocket, ::handleSocketAnswered)
        observe(viewModel.isSocketConnectedEvent, ::handleIsSocketConnected)
        observe(viewModel.connectEvent , ::handleConnected)
        observe(viewModel.firstcountDownState, ::handleCountDown)
        observe(viewModel.delayCountDownState, ::handleDelayCountDownState)
        observe(viewModel.resultEvent, { router.openCancelStatus(this,it) })
        observe(viewModel.isSocketMaBusyEvent, ::handleMaBusy)
    }

    private fun handleMaBusy(isMaAvaible : Boolean?) {
        if (isMaAvaible != true){
            showViewMaBusy()
        }else{
            showViewTransitionCall()
        }
    }

    private fun showViewTransitionCall() {
        viewBinding?.run {
            viewMaBusy.root.isVisible = false
            transitionVideoCallImg.isVisible = true
            textMedicalAdvisor.isVisible = true
            transitionTextDescriptionCountDown.isVisible = false
            dialogImgCallVideo.startRippleAnimation()
            viewDescriptionDocument(true)
            transitionTextWaiting.isVisible = true
        }
    }

    private fun showViewMaBusy() {
        viewBinding?.run {
            dialogImgCallVideo.stopRippleAnimation()
            viewMaBusy.root.isVisible = true
            transitionVideoCallImg.isVisible = false
            textMedicalAdvisor.isVisible = false
            transitionTextDescriptionCountDown.isVisible = false
            viewDescriptionDocument(false)
            transitionTextWaiting.isVisible = false
        }
    }

    private fun handleDelayCountDownState(countDownState: Pair<CountDownState,CountDownState>?) {
        countDownState?.let {
            when(it.second){
                is CountDownState.onTick -> viewBinding?.transitionTextCountDown?.text = (it.second as CountDownState.onTick).time
                is CountDownState.onFinish -> {
                    when (it.first){
                        CountDownState.onConnect ->{
                            handleConnected(true)
                        }
                        else -> null
                    }
                }
                else -> null
            }
        }
    }

    private fun handleCountDown(countDown: CountDownState?) {
        countDown?.let { countDownState ->
            when (countDownState) {
                is CountDownState.onTick -> viewBinding?.transitionTextCountDown?.text = countDownState.time
                is CountDownState.onFinish -> handleConnected(true)
            }
        }
    }

    private fun handleConnected(value : Boolean?) {
        viewBinding?.run {
            if (value == true) {
                connectSocketWithPermissionCheck()
                transitionTextCountDown.isVisible = false
                transitionTextWaiting.isVisible = true
                viewDescriptionDocument(true)
                transitionTextDescriptionCountDown.isVisible = false
            }
        }
    }

    private fun handleIsSocketConnected(isConnected: Boolean?) {
        if (isConnected == true) {
            viewBinding?.dialogImgCallVideo?.startRippleAnimation()
        } else {
            showErrorSnackbar(getString(R.string.error_default)) { finish() }
        }
    }

    private fun handleSocketAnswered(empty: Empty?) {
        userProfile?.let { userProfile ->
            router.openVideoCallActivity(
                this,
                appointmentId,
                orderCode,
                userProfile,
                infoDetail
            )
        }
    }

    override fun onBackPressed() {
        callBottomSheetCancel()

    }

    fun callBottomSheetCancel(){
        viewBinding?.run {
            router.openCancelCallBottomsheet(this@TransitionActivity.supportFragmentManager,{submitCallBack ->
                if (submitCallBack){
                    router.openOptionCallBottomSheet(this@TransitionActivity.supportFragmentManager,{ submitCallBack ->
                        baseViewModel?.setCancelReasonOrder(appointmentId,submitCallBack.toString())
                    },{dissmissCallback ->

                    })
                }
            },{ dissmissCallBack ->
                if (dissmissCallBack){
                    dialogImgCallVideo.stopRippleAnimation()
                    transitionTextCountDown.isVisible = true
                    transitionVideoCallImg.isVisible = true
                    viewMaBusy.root.isVisible = false
                    transitionTextWaiting.isVisible =false
                    viewDescriptionDocument(false)
                    transitionTextDescriptionCountDown.isVisible = true
                    baseViewModel?.getCountDown(CountDownState.onConnect)
                }
            })
            dialogImgCallVideo.stopRippleAnimation()
            baseViewModel?.pauseCountDownTimer()
            baseViewModel?.disconnect()
        }

    }
    override fun onDestroy() {
        baseViewModel?.disconnect()
        super.onDestroy()
    }

    override fun bindViewModel(): TransitionVM {
        return ViewModelProvider(this, viewModelFactory)[TransitionVM::class.java]
    }

    override fun handleFailure(failure: Failure?) {
        when (failure) {
            is Failure.ServerError -> router.openCancelStatus(this,false)
            is Failure.NetworkConnection -> router.openCancelStatus(this,false)
            else -> super.handleFailure(failure)
        }
    }

    fun viewDescriptionDocument(isvisible : Boolean ){
        viewBinding?.transtionVideoCallRecyrl?.isVisible = isvisible
    }
    companion object {
        private const val EXTRA_APPOINTMENT_ID = "TransitionExtra.AppointmentId"
        private const val EXTRA_ORDER_CODE = "TransitionExtra.OrderCode"
        private const val EXTRA_INFO_DETAIL = "TransitionExtra.InfoDetail"
        fun createIntent(
            caller: Context,
            appointmentId: Int,
            orderCode: String?,
            profile: UserProfile,
            infoDetail: InfoDetail?
        ): Intent {
            return Intent(caller, TransitionActivity::class.java)
                .putExtra(EXTRA_APPOINTMENT_ID, appointmentId)
                .putExtra(EXTRA_ORDER_CODE, orderCode)
                .putExtra(KEY_DATA_PATIENT, profile)
                .putExtra(EXTRA_INFO_DETAIL,infoDetail)
        }
    }
}