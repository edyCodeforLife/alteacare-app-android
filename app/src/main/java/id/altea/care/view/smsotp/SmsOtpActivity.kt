package id.altea.care.view.smsotp

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.Toolbar
import androidx.core.text.parseAsHtml
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import com.jakewharton.rxbinding3.widget.textChanges
import id.altea.care.R
import id.altea.care.core.base.BaseActivityVM
import id.altea.care.core.domain.model.Auth
import id.altea.care.core.domain.model.CountDownState
import id.altea.care.core.domain.model.RegisterInfo
import id.altea.care.core.exception.Failure
import id.altea.care.core.ext.disposedBy
import id.altea.care.core.ext.observe
import id.altea.care.core.ext.onSingleClick
import id.altea.care.core.helper.util.ConstantExtra
import id.altea.care.databinding.ActivitySmsOtpBinding

@SuppressLint("SetTextI18n")
class SmsOtpActivity : BaseActivityVM<ActivitySmsOtpBinding, SmsOtpVM>() {

    private val typeActivity by lazy { intent.getSerializableExtra(EXTRA_TYPE) as SmsOtpType }
    private val token by lazy { intent.getStringExtra(EXTRA_TOKEN) }
    private val oldPhoneNumber by lazy { intent.getStringExtra(EXTRA_OLD_PHONE_NUMBER) }
    private val email by lazy { intent.getStringExtra(EXTRA_EMAIL) }

    private var flagOnBackPressed: Boolean? = false

    private var newPhoneNumber: String? = null

    override fun enableBackButton(): Boolean = false

    override fun bindToolbar(): Toolbar? = viewBinding?.appbar?.toolbar

    override fun getUiBinding(): ActivitySmsOtpBinding = ActivitySmsOtpBinding.inflate(layoutInflater)

    override fun onFirstLaunch(savedInstanceState: Bundle?) {
        newPhoneNumber = intent.getStringExtra(EXTRA_NEW_PHONE_NUMBER)
        if (newPhoneNumber?.isEmpty() == true) newPhoneNumber = oldPhoneNumber

        baseViewModel?.onFirstLaunch(typeActivity, newPhoneNumber, token)

        viewBinding?.run {
            when(typeActivity) {
                SmsOtpType.REGISTER -> {
                    flagOnBackPressed = true
                }
                SmsOtpType.FORGOT_PASSWORD -> {
                    flagOnBackPressed = false
                    emailVerificationTextView.visibility = View.GONE
                }
                SmsOtpType.ACCOUNT -> {
                    flagOnBackPressed = true
                    changePhoneNumberTextView.visibility = View.GONE
                    emailVerificationTextView.visibility = View.GONE
                }
            }
        }
    }

    override fun initUiListener() {
        viewBinding?.run {
            resendSmsOtpTextView.onSingleClick()
                .subscribe { baseViewModel?.resendSmsOtp(newPhoneNumber, token) }
                .disposedBy(disposable)

            smsOtpEditText.textChanges()
                .filter { charSequence ->
                    charSequence.length == 6
                }
                .subscribe { otp ->
                    validateOtp(otp.toString())
                }
                .disposedBy(disposable)

            changePhoneNumberTextView.onSingleClick()
                .subscribe { SmsOtpRouter.openChangePhoneNumber(
                    this@SmsOtpActivity, oldPhoneNumber,
                    token ?: "",
                    resultChangePhoneNumber)
                }.disposedBy(disposable)

            if (email.isNullOrEmpty()) {
                emailVerificationTextView.visibility = View.GONE
            } else {
                emailVerificationTextView.onSingleClick()
                    .subscribe { SmsOtpRouter.openEmailOtp(this@SmsOtpActivity, email ?: "", oldPhoneNumber,token ?: "") }
                    .disposedBy(disposable)
            }
        }
    }

    private val resultChangePhoneNumber = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            handleResultChangePhoneNumber(result.data?.getStringExtra(ConstantExtra.EXTRA_CHANGE_PHONE_NUMBER))
        }
    }

    private fun handleResultChangePhoneNumber(phoneNumber: String?) {
        this.newPhoneNumber = phoneNumber
        viewBinding?.smsOtpTextView?.text = "<b> ${getString(R.string.phone_number_verification_desc)} </b> 0$phoneNumber".parseAsHtml()
        baseViewModel?.resendSmsOtp(phoneNumber, token ?: "")
    }

    private fun validateOtp(otp: String) {
        when(typeActivity) {
            SmsOtpType.REGISTER -> {
                baseViewModel?.doValidationSmsOtpRegistration(otp, newPhoneNumber ?: "", token ?: "")
            }
            SmsOtpType.FORGOT_PASSWORD -> {
                baseViewModel?.doValidateOtpForgot(newPhoneNumber ?: "", otp)
            }
            SmsOtpType.ACCOUNT -> {
                baseViewModel?.doChangePhoneNumberAccountOtp(newPhoneNumber ?: "", otp)
            }
        }
    }

    override fun bindViewModel(): SmsOtpVM = ViewModelProvider(this, viewModelFactory)[SmsOtpVM::class.java]

    override fun observeViewModel(viewModel: SmsOtpVM) {
        viewModel.run {
            observe(viewModel.registerType, { handleRegisterView() })
            observe(viewModel.forgotPassType, { handleForgotPassView() })
            observe(viewModel.accountType, { handleAccountView() })
            observe(viewModel.otpRequest, {
                showInfoSnackbar(getString(R.string.phone_verification_send))
                showCountDown()
            })
            observe(viewModel.otpRequestVerify, ::onOtpRegisterValid)
            observe(viewModel.otpForgotPassValidation, ::onOtpForgotPassValid)
            observe(viewModel.otpAccountValidation, ::onOtpAccountValid)
            observe(viewModel.countDownTimerState, ::handleCountDown)
            observe(viewModel.isLoadingLiveData, ::handleLoading)
            observe(viewModel.failureLiveData, ::handleFailure)
        }
    }

    private fun handleRegisterView() {
        viewBinding?.run {
            smsOtpTextView.text = "<b> ${getString(R.string.verifikasi_otp_desc)} </b> $newPhoneNumber".parseAsHtml()
            appbar.txtToolbarTitle.text = getString(R.string.phone_number_verification)
        }
    }

    private fun handleForgotPassView() {
        viewBinding?.run {
            smsOtpTextView.text = "<b> ${getString(R.string.phone_number_verification_desc)} </b> $newPhoneNumber".parseAsHtml()
            appbar.txtToolbarTitle.text = getString(R.string.reset_password)
            changePhoneNumberTextView.isVisible = false

            showInfoSnackbar(getString(R.string.phone_verification_send))
            showCountDown()
        }
    }

    private fun handleAccountView() {
        viewBinding?.run {
            smsOtpTextView.text = "<b> ${getString(R.string.sms_verification_description)} </b> $newPhoneNumber".parseAsHtml()
            appbar.txtToolbarTitle.text = getString(R.string.phone_number_verification)
            showInfoSnackbar(getString(R.string.phone_verification_send))
            showCountDown()
        }
    }

    private fun showCountDown() {
        viewBinding?.run {
            resendSmsOtpGroup.isVisible = false
            countDownTimerTextView.isVisible = true
        }
    }

    private fun onOtpRegisterValid(token: String?) {
        token?.let { tokenLet ->
            showSuccessSnackbar(getString(R.string.registration_success)) { SmsOtpRouter.openSuccessRegister(this, tokenLet) }
        }
    }

    private fun onOtpForgotPassValid(auth: Auth?) {
        auth?.let { authLet ->
            SmsOtpRouter.openPassword(this, RegisterInfo("", "", ""), authLet.accessToken ?: "")
        }
    }

    private fun onOtpAccountValid(result : Boolean?){
        if (result == true){
            showSuccessSnackbar(getString(R.string.valid_account_otp)) { finish() }
        }
    }

    private fun handleCountDown(countDown: CountDownState?) {
        countDown?.let { countDownState ->
            when (countDownState) {
                is CountDownState.onFinish -> { showResendSmsOtp() }
                is CountDownState.onTick -> viewBinding?.countDownTimerTextView?.text = countDownState.time
            }
        }
    }

    override fun handleFailure(failure: Failure?) {
        when(failure) {
            is Failure.ServerError -> { stateErrorView(failure.message) }
            else -> super.handleFailure(failure)
        }
    }

    private fun stateErrorView(failure: String) {
            showErrorSnackbar(failure)
    }

    private fun showResendSmsOtp() {
        viewBinding?.run {
            resendSmsOtpGroup.isVisible = true
            countDownTimerTextView.isVisible = false
        }
    }

    override fun onBackPressed() {
        if (flagOnBackPressed == false){
            super.onBackPressed()
        }
    }

    companion object {
        private const val EXTRA_TYPE = "SmsOtp.type"
        private const val EXTRA_OLD_PHONE_NUMBER = "SmsOtp.oldPhoneNumber"
        private const val EXTRA_NEW_PHONE_NUMBER  = "SmsOtp.newPhoneNumber"
        private const val EXTRA_EMAIL  = "SmsOtp.email"
        private const val EXTRA_TOKEN = "SmsOtp.token"
        fun createIntent(
            caller: Context,
            type: SmsOtpType,
            oldPhoneNumber: String,
            newPhoneNumber: String,
            email: String,
            token: String? = null
        ): Intent {
            return Intent(caller, SmsOtpActivity::class.java).apply {
                putExtra(EXTRA_TYPE, type)
                putExtra(EXTRA_OLD_PHONE_NUMBER, oldPhoneNumber)
                putExtra(EXTRA_NEW_PHONE_NUMBER, newPhoneNumber)
                putExtra(EXTRA_EMAIL, email)
                token?.let { token ->
                    putExtra(EXTRA_TOKEN, token)
                }
            }
        }
    }

}