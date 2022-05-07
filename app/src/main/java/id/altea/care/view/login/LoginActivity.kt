package id.altea.care.view.login

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import com.jakewharton.rxbinding3.widget.textChanges
import id.altea.care.BuildConfig
import id.altea.care.R
import id.altea.care.core.base.BaseActivityVM
import id.altea.care.core.ext.*
import id.altea.care.core.helper.validator.EmailValidator
import id.altea.care.core.helper.validator.PhoneValidator
import id.altea.care.databinding.ActivityLoginBinding
import id.altea.care.view.login.model.LoginHadleModel
import io.reactivex.Observable
import kotlinx.android.synthetic.main.activity_login.*
import java.util.*

/**
 * Created by trileksono on 02/03/21.
 */
class LoginActivity : BaseActivityVM<ActivityLoginBinding, LoginViewModel>() {

    private val viewModel by viewModels<LoginViewModel> { viewModelFactory }
    private val router = LoginActivityRouter()

    override fun onFirstLaunch(savedInstanceState: Bundle?) {
        viewBinding?.loginTxtVersion?.text =
            String.format(getString(R.string.version), BuildConfig.VERSION_NAME)
    }

    override fun initUiListener() {
        Observable.combineLatest(
            emailOrPhoneNumberValidationObservable(),
            passwordValidationObservable(), { t1, t2 -> t1 && t2 })
            .subscribe { btnLogin.changeStateButton(it) }
            .disposedBy(disposable)

        viewBinding?.run {
            btnLogin.onSingleClick().subscribe {
                viewModel.doLogin(emailOrPhoneNumberEditText.text.toString(), edtxLoginPassword.text.toString())
            }.disposedBy(disposable)

            txtLoginRegister.onSingleClick()
                .subscribe { router.openRegisterActivity(this@LoginActivity) }
                .disposedBy(disposable)

            txtLoginForgotPassword.onSingleClick().subscribe {
                router.openForgotPassword(this@LoginActivity)
            }.disposedBy(disposable)

            txtLoginCallCenter.onSingleClick().subscribe {
                router.openContact(this@LoginActivity)
            }.disposedBy(disposable)
        }
    }

    private fun emailOrPhoneNumberValidationObservable(): Observable<Boolean>? {
        return emailOrPhoneNumberEditText.textChanges()
            .map { it.isNotEmpty() && EmailValidator().isValidEmail(it.toString()) || PhoneValidator.isPhoneNumberValid(it.toString()) }
            .doOnNext { if (emailOrPhoneNumberEditText.isFocused) emailOrPhoneNumberTextViewError.isVisible = !it }
    }

    private fun passwordValidationObservable(): Observable<Boolean> {
        return edtxLoginPassword.textChanges()
            .map { it.isNotEmpty() }
            .doOnNext { if (edtxLoginPassword.isFocused) txtLoginErrorPassword.isVisible = !it }
    }

    override fun enableBackButton(): Boolean = false

    override fun bindToolbar(): Toolbar? = null

    override fun bindViewModel(): LoginViewModel = viewModel

    override fun getUiBinding(): ActivityLoginBinding {
        return ActivityLoginBinding.inflate(layoutInflater)
    }

    override fun observeViewModel(viewModel: LoginViewModel) {
        observe(viewModel.failureLiveData, ::handleFailure)
        observe(viewModel.isLoadingLiveData, ::handleLoading)
        observe(viewModel.loginResult, { resultLogin() })
        observe(viewModel.userUnverifiedEvent, { unverifiedUserHandle(it) })
    }

    private fun unverifiedUserHandle(loginHadleModel: LoginHadleModel?) {
        when (loginHadleModel) {
            is LoginHadleModel.EmailUnverified -> {
                router.openEmailOtp(
                    this,
                    loginHadleModel.email,
                    loginHadleModel.token
                )
            }
            is LoginHadleModel.PhoneUnverified -> {
                router.openSmsOtp(
                    this,
                    loginHadleModel.phone,
                    loginHadleModel.token
                )
            }
            else -> {}
        }
    }

    private fun resultLogin() {
        router.openMainActivity(this)
    }
}
