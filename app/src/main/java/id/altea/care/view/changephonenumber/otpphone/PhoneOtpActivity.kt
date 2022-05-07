package id.altea.care.view.changephonenumber.otpphone

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.widget.Toolbar
import androidx.core.text.parseAsHtml
import androidx.core.view.isVisible
import com.jakewharton.rxbinding3.widget.textChanges
import id.altea.care.R
import id.altea.care.core.base.BaseActivityVM
import id.altea.care.core.domain.model.CountDownState
import id.altea.care.core.ext.disposedBy
import id.altea.care.core.ext.observe
import id.altea.care.core.ext.onSingleClick
import id.altea.care.databinding.ActivityPhoneOtpBinding
import kotlinx.android.synthetic.main.activity_email_otp.*


class PhoneOtpActivity : BaseActivityVM<ActivityPhoneOtpBinding, PhoneOtpVM>() {

    private val viewModel by viewModels<PhoneOtpVM> { viewModelFactory }
    private val phoneNumber by lazy {
        intent.getStringExtra(EXTRA_PHONE_NEW)
    }

    private val oldPhoneNumber by lazy {
        intent.getStringExtra(EXTRA_PHONE_OLD)
    }

    private val router  =  PhoneOtpRouter()

    override fun observeViewModel(viewModel: PhoneOtpVM) {
        observe(viewModel.failureLiveData, ::handleFailure)
        observe(viewModel.isLoadingLiveData, ::handleLoading)
        observe(viewModel.countDownState, ::handleCountDown)
        observe(viewModel.phoneNumberType,{ handlePhoneView() })
        observe(viewModel.otpRequest, {
            it?.let {
                showInfoSnackbar(getString(R.string.phone_verification_send))
                showCountDown()
            }
        })
        observe(viewModel.otpPhoneValidation, :: onOtpPhoneNumberValid)

    }

    private fun handlePhoneView() {
        viewBinding?.run {
            phoneOtpTxtPhone.text =
                "<b> ${getString(R.string.verifikasi_otp_desc)} </b> ${oldPhoneNumber.toString()}".parseAsHtml()
            appbar.txtToolbarTitle.text = getString(R.string.phone_verification)

            showInfoSnackbar(getString(R.string.phone_verification_send))
            showCountDown()
        }
    }

    private fun onOtpPhoneNumberValid(result : Boolean?) {
        if (result == true){
            showSuccessSnackbar(getString(R.string.valid_account_otp)){
             finish()
            }
        }
    }

    override fun bindViewModel(): PhoneOtpVM = viewModel

    override fun enableBackButton(): Boolean = true

    override fun bindToolbar(): Toolbar? = viewBinding?.appbar?.toolbar

    override fun getUiBinding(): ActivityPhoneOtpBinding =
        ActivityPhoneOtpBinding.inflate(layoutInflater)

    override fun onFirstLaunch(savedInstanceState: Bundle?) {
        viewModel.onFirstLaunch(phoneNumber ?: "")
    }

    override fun initUiListener() {
        viewBinding?.run {
            phoneOtpEdtxPhone
                .textChanges().filter { it.length == 6 }
                .subscribe { validateOtp(it.toString()) }
                .disposedBy(disposable)

            phoneOtpTxtResend.onSingleClick().subscribe {
                viewModel.getRequestChangePhoneNumber(phoneNumber?: "")
            }.disposedBy(disposable)


            phoneOtpTxtChangePhone.onSingleClick().subscribe {
                router.openPhoneNumberActivity(this@PhoneOtpActivity,oldPhoneNumber ?: "")
            }.disposedBy(disposable)
        }
    }

    private fun validateOtp(otp : String) {
        viewModel.doChangePhoneNumberAccountOtp(otp)
    }


    private fun handleCountDown(countDown: CountDownState?) {
        countDown?.let {
            when (it) {
                is CountDownState.onFinish -> showResendPhone()
                is CountDownState.onTick -> viewBinding?.phoneOtpTxtCountdown?.text = it.time
            }
        }
    }

    private fun showCountDown() {
        viewBinding?.run {
            phoneOtpGroupResend.isVisible = false
            phoneOtpTxtCountdown.isVisible = true
        }
    }

    private fun showResendPhone() {
        viewBinding?.run {
            phoneOtpGroupResend.isVisible = true
            phoneOtpTxtCountdown.isVisible = false
        }
    }


    companion object {
        private const val EXTRA_PHONE_NEW = "PhoneOtp.phonenew"
        private const val EXTRA_PHONE_OLD = "PhoneOtp.phoneold"

        fun createIntent(
            caller: Context,
            newPhoneNumber: String?,
            oldPhoneNumber : String?
        ): Intent {
            return Intent(caller, PhoneOtpActivity::class.java).apply {
                putExtra(EXTRA_PHONE_NEW, newPhoneNumber)
                putExtra(EXTRA_PHONE_OLD,oldPhoneNumber)
            }
        }
    }

}