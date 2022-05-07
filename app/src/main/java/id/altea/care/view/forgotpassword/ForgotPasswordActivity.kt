package id.altea.care.view.forgotpassword

import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import id.altea.care.R
import id.altea.care.core.base.BaseActivityVM
import id.altea.care.core.domain.model.ForgotPassword
import id.altea.care.core.ext.*
import id.altea.care.databinding.ActivityForgotPasswordBinding

/**
 * Created by trileksono on 10/03/21.
 */
class ForgotPasswordActivity : BaseActivityVM<ActivityForgotPasswordBinding, ForgotPasswordVM>() {

    private val router = ForgotPasswordRouter()

    override fun observeViewModel(viewModel: ForgotPasswordVM) {
        observe(viewModel.failureLiveData, ::handleFailure)
        observe(viewModel.isLoadingLiveData, ::handleLoading)
        observe(viewModel.resultRequestOtp, ::handleResultRequestOtp)
    }

    private fun handleResultRequestOtp(forgotPassword: ForgotPassword?) {
        if (forgotPassword != null) {
            if (forgotPassword.type.equals("PHONE")) router.openSmsOtp(this@ForgotPasswordActivity, viewBinding?.emailOrSmsEditText?.text.toString())
            else router.openEmailOtp(this@ForgotPasswordActivity, viewBinding?.emailOrSmsEditText?.text.toString())
        } else {
            return
        }
    }

    override fun bindViewModel(): ForgotPasswordVM {
        return ViewModelProvider(this, viewModelFactory)[ForgotPasswordVM::class.java]
    }

    override fun enableBackButton(): Boolean = true

    override fun bindToolbar(): Toolbar? = viewBinding?.appbar?.toolbar

    override fun getUiBinding(): ActivityForgotPasswordBinding {
        return ActivityForgotPasswordBinding.inflate(layoutInflater)
    }

    override fun onFirstLaunch(savedInstanceState: Bundle?) {
        viewBinding?.run {
            appbar.txtToolbarTitle.text = getString(R.string.reset_password)
        }
    }

    override fun initUiListener() {
        viewBinding?.run {
            emailOrSmsEditText.emailOrPhoneNumberValidator { view, b ->
                if (view.isFocused) forgotPasswordTxtErrorEmail.isVisible = !b
            }
                .subscribe { forgotPasswordBtnReset.changeStateButton(it) }
                .disposedBy(disposable)

            forgotPasswordBtnReset.onSingleClick()
                .subscribe {
                    baseViewModel?.requestEmailOtp(emailOrSmsEditText.text.toString())
                }
                .disposedBy(disposable)
        }
    }
}
