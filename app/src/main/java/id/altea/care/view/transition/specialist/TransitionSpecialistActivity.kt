package id.altea.care.view.transition.specialist

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import id.altea.care.R
import id.altea.care.core.base.BaseActivityVM
import id.altea.care.core.domain.model.ConsultationDoctor
import id.altea.care.core.domain.model.InfoDetail
import id.altea.care.core.ext.loadImage
import id.altea.care.core.ext.observe
import id.altea.care.core.ext.showPermissionSettingDialog
import id.altea.care.core.ext.showRationaleDialog
import id.altea.care.databinding.ActivityTransitionSpecialistCallBinding
import id.altea.care.view.login.LoginViewModel
import permissions.dispatcher.*

@RuntimePermissions
class TransitionSpecialistActivity :
    BaseActivityVM<ActivityTransitionSpecialistCallBinding, TransitionSpecialistVM>() {

    private val viewModel by viewModels<TransitionSpecialistVM> { viewModelFactory }

    private val router = TransitionSpecialistRouter()
    private val appointmentId: Int by lazy { intent.getIntExtra(EXTRA_APPOINTMENT_ID, 0) }
    private val doctor by lazy {
        intent.getParcelableExtra<ConsultationDoctor>(EXTRA_DOCTOR)
    }
    private val infoDetail by lazy {
        intent.getParcelableExtra<InfoDetail>(EXTRA_INFO_DETAIL)
    }

    override fun observeViewModel(viewModel: TransitionSpecialistVM) {
        observe(viewModel.answeredSocket, { handleSocketAnswered() })
        observe(viewModel.isSocketConnectedEvent, ::handleIsSocketConnected)
    }

    override fun bindViewModel(): TransitionSpecialistVM {
        return ViewModelProvider(this, viewModelFactory)[TransitionSpecialistVM::class.java]
    }

    override fun enableBackButton(): Boolean = false

    override fun bindToolbar(): Toolbar? = null

    override fun getUiBinding(): ActivityTransitionSpecialistCallBinding =
        ActivityTransitionSpecialistCallBinding.inflate(layoutInflater)

    override fun onStart() {
        super.onStart()
        connectSocketWithPermissionCheck()
    }

    override fun onStop() {
        baseViewModel?.disconnect()
        super.onStop()
    }

    override fun onFirstLaunch(savedInstanceState: Bundle?) {
        initViews()
        viewModel.trackingLastDoctorChatName(doctor)
    }

    private fun initViews() {
        viewBinding?.run {
            doctor?.let {
                textNameDoctorMedicalSpesialis.text = it.name
                texTitleDoctorMedicalSpesialis.text = it.specialist?.name
                imgDoctorMedicalSpesialis.loadImage(it.photo!!)
            }
        }
    }

    override fun initUiListener() {}

    private fun handleSocketAnswered() {
        router.openVideoCallActivity(this, appointmentId,infoDetail)
    }

    private fun handleIsSocketConnected(isConnected: Boolean?) {
        if (isConnected == true) {
            viewBinding?.dialogImgCallSpecialist?.startRippleAnimation()
        } else {
            showErrorSnackbar(getString(R.string.error_default)) { finish() }
        }
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

    override fun onDestroy() {
        baseViewModel?.disconnect()
        super.onDestroy()
    }

    companion object {
        private const val EXTRA_APPOINTMENT_ID = "TransitionExtra.AppointmentId"
        private const val EXTRA_DOCTOR = "TransitionExtra.doctor"
        private const val EXTRA_INFO_DETAIL = "TransitionExtra.InfoDetail"
        fun createIntent(caller: Context, appointmentId: Int, doctor: ConsultationDoctor?,infoDetail: InfoDetail?): Intent {
            return Intent(caller, TransitionSpecialistActivity::class.java)
                .putExtra(EXTRA_APPOINTMENT_ID, appointmentId)
                .putExtra(EXTRA_DOCTOR, doctor)
                .putExtra(EXTRA_INFO_DETAIL,infoDetail)

        }
    }


}