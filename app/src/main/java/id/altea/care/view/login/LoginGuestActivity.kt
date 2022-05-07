package id.altea.care.view.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import com.jakewharton.rxbinding3.widget.textChanges
import id.altea.care.core.base.BaseActivityVM
import id.altea.care.core.domain.model.Doctor
import id.altea.care.core.ext.*
import id.altea.care.core.helper.validator.EmailValidator
import id.altea.care.core.helper.validator.PhoneValidator
import id.altea.care.databinding.ActivityLoginGuestBinding
import id.altea.care.view.login.model.LoginHadleModel
import io.reactivex.Observable
import kotlinx.android.synthetic.main.activity_login_guest.*

class LoginGuestActivity : BaseActivityVM<ActivityLoginGuestBinding, LoginViewModel>() {

    private val viewModel by viewModels<LoginViewModel> { viewModelFactory }
    private val router = LoginActivityRouter()
    private val doctor: Doctor? by lazy { intent.getParcelableExtra(EXTRA_DOCTOR) as Doctor? }

    override fun onFirstLaunch(savedInstanceState: Bundle?) {
        doctor?.let {
            viewBinding?.run {
                imgLoginGuestDoctor.loadImage(it.photo.orEmpty())
                txtLoginGuestDoctorName.text = it.doctorName
            }
        }
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
                .subscribe { router.openRegisterActivity(this@LoginGuestActivity) }
                .disposedBy(disposable)

            txtLoginForgotPassword.onSingleClick().subscribe {
                router.openForgotPassword(this@LoginGuestActivity)
            }.disposedBy(disposable)

            txtLoginCallCenter.onSingleClick().subscribe {
                router.openContact(this@LoginGuestActivity)
            }.disposedBy(disposable)
        }
    }

    private fun emailOrPhoneNumberValidationObservable(): Observable<Boolean>? {
        return emailOrPhoneNumberEditText.textChanges()
            .map { it.isNotEmpty() && EmailValidator().isValidEmail(it.toString()) || PhoneValidator.isPhoneNumberValid(it.toString()) }
            .doOnNext { if (emailOrPhoneNumberEditText.isFocused) txtLoginErrorEmail.isVisible = !it }
    }

    private fun passwordValidationObservable(): Observable<Boolean> {
        return edtxLoginPassword.textChanges()
            .map { it.isNotEmpty() }
            .doOnNext { if (edtxLoginPassword.isFocused) txtLoginErrorPassword.isVisible = !it }
    }

    override fun enableBackButton(): Boolean = false

    override fun bindToolbar(): Toolbar? = null

    override fun bindViewModel(): LoginViewModel = viewModel

    override fun getUiBinding(): ActivityLoginGuestBinding {
        return ActivityLoginGuestBinding.inflate(layoutInflater)
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
            else -> {
            }
        }
    }

    private fun resultLogin() {
        finish()
    }


    companion object {
        private const val EXTRA_DOCTOR = "LoginGuestActivity.doctor"
        fun createIntent(caller: Context, doctor: Doctor): Intent {
            return Intent(caller, LoginGuestActivity::class.java).putExtra(EXTRA_DOCTOR, doctor)
        }
    }
}
