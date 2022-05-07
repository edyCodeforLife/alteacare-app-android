package id.altea.care.view.consultation.voucher

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import com.jakewharton.rxbinding3.widget.textChanges
import id.altea.care.R
import id.altea.care.core.base.BaseActivityVM
import id.altea.care.core.domain.model.Voucher
import id.altea.care.core.exception.Failure
import id.altea.care.core.ext.changeStateButton
import id.altea.care.core.ext.disposedBy
import id.altea.care.core.ext.observe
import id.altea.care.core.ext.onSingleClick
import id.altea.care.core.helper.util.Constant.TYPE_OF_SERVICE_TELEKONSULTASI
import id.altea.care.databinding.ActivityVoucherBinding
import id.altea.care.view.login.LoginActivityRouter
import java.util.*

class VoucherActivity : BaseActivityVM<ActivityVoucherBinding, VoucherVm>() {

    private val appointmentId by lazy {
        intent.getStringExtra(EXTRA_APPOINTMENT_ID)
    }

    override fun enableBackButton(): Boolean = false

    override fun bindToolbar(): Toolbar? = null

    override fun getUiBinding(): ActivityVoucherBinding =
        ActivityVoucherBinding.inflate(layoutInflater)

    override fun onFirstLaunch(savedInstanceState: Bundle?) {
        viewBinding?.run {
            useVocherEdtxVoucher.textChanges()
                .map { voucherText ->
                    voucherText.isNotEmpty()
                }
                .subscribe { state ->
                    useVoucherBtn.changeStateButton(state)
                }.disposedBy(disposable)
        }
    }

    override fun initUiListener() {
        viewBinding?.run {
            voucherBtnClose.onSingleClick().subscribe {
                finish()
            }.disposedBy(disposable)

            useVoucherBtn.onSingleClick().subscribe {
                baseViewModel?.getVoucher(
                    useVocherEdtxVoucher.text.toString(),
                    appointmentId,
                    TYPE_OF_SERVICE_TELEKONSULTASI
                )
            }.disposedBy(disposable)
        }
    }

    override fun observeViewModel(viewModel: VoucherVm) {
        observe(viewModel.failureLiveData, ::handleFailure)
        observe(viewModel.isLoadingLiveData, ::handleLoading)
        observe(viewModel.voucherEvent, ::handleVoucherEvent)

    }

    private fun handleVoucherEvent(voucher: Voucher?) {
        voucher?.let {
            viewBinding?.useVoucherTextMessage?.text = getString(R.string.str_success_voucher)
            viewBinding?.useVoucherTextMessage?.setTextColor(getColor(R.color.green))
            Handler(Looper.getMainLooper()).postDelayed({
                setResult(Activity.RESULT_OK, intent.putExtra("voucher", voucher))
                finish()
            }, 1000)
        }
    }

    override fun handleFailure(failure: Failure?) {
        when (failure) {
            is Failure.NetworkConnection -> this.showErrorSnackbar(
                getString(R.string.error_disconnect)
            )
            is Failure.ServerError -> {
                viewBinding?.useVoucherTextMessage?.text = failure.message
                viewBinding?.useVoucherTextMessage?.setTextColor(getColor(R.color.redLight))
            }
            is Failure.ExpiredSession -> {
                showToast(getString(R.string.session_expired_error_toast))
                startActivity(
                    LoginActivityRouter.createIntent(this)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                )
                finish()
            }
            else -> this.showErrorSnackbar(getString(R.string.error_default))
        }
    }

    override fun bindViewModel(): VoucherVm {
        return ViewModelProvider(this, viewModelFactory)[VoucherVm::class.java]
    }

    companion object {
        private const val EXTRA_APPOINTMENT_ID = "Appointment.ID"

        fun createIntent(
            caller: Context,
            appointmentId: String?
        ): Intent {
            return Intent(caller, VoucherActivity::class.java).apply {
                putExtra(EXTRA_APPOINTMENT_ID, appointmentId)
            }
        }
    }
}