package id.altea.care.view.otpmail

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
import id.altea.care.core.domain.model.CountDownState.onFinish
import id.altea.care.core.domain.model.CountDownState.onTick
import id.altea.care.core.domain.model.RegisterInfo
import id.altea.care.core.exception.Failure
import id.altea.care.core.ext.disposedBy
import id.altea.care.core.ext.observe
import id.altea.care.core.ext.onSingleClick
import id.altea.care.core.helper.util.ConstantExtra
import id.altea.care.databinding.ActivityEmailOtpBinding
import id.altea.care.view.smsotp.SmsOtpType
import kotlinx.android.synthetic.main.activity_email_otp.*

/**
 * Created by trileksono on 08/03/21.
 */

@SuppressLint("SetTextI18n")
class EmailOtpActivity : BaseActivityVM<ActivityEmailOtpBinding, EmailOtpVM>() {

    private val typeActivity by lazy { intent.getSerializableExtra(EXTRA_TYPE) as EmailOtpType }
    private val token by lazy { intent.getStringExtra(EXTRA_TOKEN) }
    private var newEmail: String? = null
    private val router = EmailOtpRouter()
    private val oldEmail by lazy { intent.getStringExtra(EXTRA_OLD_EMAIL) }
    private val phoneNumber by lazy { intent.getStringExtra(EXTRA_PHONE_NUMBER) }

    private var flagOnBackPressed: Boolean? = false

    override fun enableBackButton(): Boolean = false

    override fun bindToolbar(): Toolbar? = viewBinding?.appbar?.toolbar

    override fun getUiBinding(): ActivityEmailOtpBinding =
        ActivityEmailOtpBinding.inflate(layoutInflater)

    override fun onFirstLaunch(savedInstanceState: Bundle?) {
        newEmail = intent.getStringExtra(EXTRA_NEW_EMAIL)

        if (newEmail?.isEmpty() == true) {
            newEmail = oldEmail
        }
        baseViewModel?.onFirstLaunch(typeActivity, newEmail ?: "", token)


        viewBinding?.run {
            when(typeActivity){
                EmailOtpType.REGISTER -> {
                    flagOnBackPressed = true
                }
                EmailOtpType.FORGOT_PASSWORD -> {
                    flagOnBackPressed = false
                    smsVerificationTextView.visibility = View.GONE
                }
                EmailOtpType.ACCOUNT -> {
                    flagOnBackPressed = true
                    viewBinding?.emailOtpTxtChangeEmail?.visibility = View.GONE
                    viewBinding?.smsVerificationTextView?.visibility = View.GONE
                }
            }
        }
    }

    override fun initUiListener() {
        viewBinding?.run {
            emailOtpTxtResend.onSingleClick()
                .subscribe { baseViewModel?.resendEmail(newEmail ?: "") }
                .disposedBy(disposable)

            emailOtpEdtxOtp.textChanges()
                .filter { it.length == 6 }
                .subscribe { validateOtp(it.toString()) }
                .disposedBy(disposable)

            if (phoneNumber.isNullOrEmpty()) {
                smsVerificationTextView.visibility = View.GONE
            } else {
                smsVerificationTextView.onSingleClick()
                    .subscribe {
                        router.openSmsOtp(
                            this@EmailOtpActivity,
                            phoneNumber ?: "",
                            token ?: ""
                        )
                    }.disposedBy(disposable)
            }

            emailOtpTxtChangeEmail.onSingleClick()
                .subscribe {
                    router.openChangeEmail(
                        this@EmailOtpActivity,
                        newEmail ?: "",
                        token ?: "",
                        resultChangeEmail
                    )
                }.disposedBy(disposable)
        }
    }

    private val resultChangeEmail = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            handleResultChangeEmail(
                result.data?.getStringExtra(ConstantExtra.EXTRA_CHANGE_EMAIL) ?: ""
            )
        }
    }

    override fun observeViewModel(viewModel: EmailOtpVM) {
        viewModel.run {
            observe(viewModel.failureLiveData, ::handleFailure)
            observe(viewModel.isLoadingLiveData, ::handleLoading)
            observe(viewModel.countDownState, ::handleCountDown)
            observe(viewModel.otpRequest, {
                it?.let {
                    showInfoSnackbar(getString(R.string.email_verification_send))
                    showCountDown()
                }
            })
            observe(viewModel.registerType, { handleRegisterView() })
            observe(viewModel.forgotPassType, { handleForgotPassView() })
            observe(viewModel.accountType, { handleAccountView() })
            observe(viewModel.otpRegisterValidation, ::onOtpRegisterValid)
            observe(viewModel.otpForgotPassValidation, ::onOtpForgotPassValid)
            observe(viewModel.otpAccountValidation, ::onOtpAccountValid)
        }
    }

    private fun handleAccountView() {
        viewBinding?.run {
            emailOtpTxtEmail.text = "<b> ${getString(R.string.email_verification_desc)} </b> $newEmail".parseAsHtml()
            appbar.txtToolbarTitle.text = getString(R.string.email_verification)

            showInfoSnackbar(getString(R.string.email_verification_send))
            showCountDown()
        }
    }

    private fun handleRegisterView() {
        viewBinding?.run {
            emailOtpTxtEmail.text = "<b> ${getString(R.string.email_verification_desc)} </b> $newEmail".parseAsHtml()
            appbar.txtToolbarTitle.text = getString(R.string.email_verification)
        }
    }

    private fun handleForgotPassView() {
        viewBinding?.run {
            emailOtpTxtEmail.text = "<b> ${getString(R.string.email_verification_desc2)} </b> $newEmail".parseAsHtml()
            appbar.txtToolbarTitle.text = getString(R.string.reset_password)
            emailOtpTxtChangeEmail.isVisible = false

            showInfoSnackbar(getString(R.string.email_verification_send))
            showCountDown()
        }
    }

    private fun validateOtp(otp: String) {
        when (typeActivity) {
            EmailOtpType.FORGOT_PASSWORD -> {
                baseViewModel?.doValidateOtpForgot(otp)
            }
            EmailOtpType.REGISTER -> {
                baseViewModel?.doValidateOtpCode(otp)
            }
            else -> {
                baseViewModel?.doChangeEmailAccountOtp(otp)
            }
        }
    }

    private fun handleResultChangeEmail(newEmail: String) {
        this.newEmail = newEmail
        viewBinding?.emailOtpTxtEmail?.text = "<b> ${getString(R.string.email_verification_desc)} </b> $newEmail".parseAsHtml()
        baseViewModel?.resendEmail(newEmail)
    }

    private fun handleCountDown(countDown: CountDownState?) {
        countDown?.let { countDownState ->
            when (countDownState) {
                is onFinish -> showResendEmail()
                is onTick -> emailOtpTxtCountdown.text = countDownState.time
            }
        }
    }

    private fun showCountDown() {
        viewBinding.run {
            emailOtpGroupResend.isVisible = false
            emailOtpTxtCountdown.isVisible = true
        }
    }

    private fun showResendEmail() {
        viewBinding?.run {
            emailOtpGroupResend.isVisible = true
            emailOtpTxtCountdown.isVisible = false
        }
    }

    private fun onOtpRegisterValid(token: String?) {
        token?.let { tokenLet ->
            showSuccessSnackbar(getString(R.string.registration_success)) {
                router.openSuccessRegister(
                    this,
                    tokenLet
                )
            }
        }
    }

    private fun onOtpAccountValid(result: Boolean?) {
        if (result == true) {
            showSuccessSnackbar(getString(R.string.valid_account_otp)) { finish() }
        }
    }

    private fun onOtpForgotPassValid(auth: Auth?) {
        auth?.let { authLet ->
            router.openPassword(this, RegisterInfo("", "", ""), authLet.accessToken ?: "")
        }
    }

    override fun bindViewModel(): EmailOtpVM {
        return ViewModelProvider(this, viewModelFactory)[EmailOtpVM::class.java]
    }

    override fun handleFailure(failure: Failure?) {
        when (failure) {
            is Failure.ServerError -> {
                stateErrorView(failure.message)
            }
            else -> super.handleFailure(failure)
        }
    }

    private fun stateErrorView(failure: String) {
        showErrorSnackbar(failure)
    }

    override fun onBackPressed() {
        if (flagOnBackPressed == false){
            super.onBackPressed()
        }
    }

    companion object {
        private const val EXTRA_OLD_EMAIL = "EmailOtp.oldEmail"
        private const val EXTRA_NEW_EMAIL = "EmailOtp.newEmail"
        private const val EXTRA_PHONE_NUMBER = "EmailOtp.phoneNumber"
        private const val EXTRA_TYPE = "EmailOtp.type"
        private const val EXTRA_TOKEN = "EmailOtp.token"
        fun createIntent(
            caller: Context,
            type: EmailOtpType,
            oldEmail: String,
            newEmail: String,
            phoneNumber: String? = null,
            token: String? = null
        ): Intent {
            return Intent(caller, EmailOtpActivity::class.java).apply {
                putExtra(EXTRA_OLD_EMAIL, oldEmail)
                putExtra(EXTRA_NEW_EMAIL, newEmail)
                putExtra(EXTRA_PHONE_NUMBER, phoneNumber)
                putExtra(EXTRA_TYPE, type)
                token?.let { putExtra(EXTRA_TOKEN, it) }
            }
        }
    }
}
