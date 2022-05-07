package id.altea.care.view.password

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import com.jakewharton.rxbinding3.widget.textChanges
import id.altea.care.R
import id.altea.care.core.base.BaseActivityVM
import id.altea.care.core.domain.model.RegisterInfo
import id.altea.care.core.ext.changeStateButton
import id.altea.care.core.ext.disposedBy
import id.altea.care.core.ext.observe
import id.altea.care.core.ext.onSingleClick
import id.altea.care.databinding.ActivityPasswordBinding

/**
 * Created by trileksono on 05/03/21.
 */
class PasswordActivity : BaseActivityVM<ActivityPasswordBinding, PasswordVM>() {

    private val registerInfo by lazy { intent.getParcelableExtra<RegisterInfo>(EXTRA_INFO)!! }
    private val activityType by lazy { intent.getSerializableExtra(EXTRA_TYPE)!! as PasswordType }
    private val token by lazy { intent.getStringExtra(EXTRA_TOKEN) }
    private val router = PasswordRouter()

    override fun enableBackButton(): Boolean = true

    override fun bindToolbar(): Toolbar? = viewBinding?.appbar?.toolbar

    override fun getUiBinding(): ActivityPasswordBinding {
        return ActivityPasswordBinding.inflate(layoutInflater)
    }

    override fun onFirstLaunch(savedInstanceState: Bundle?) {
        viewBinding?.run {
            when (activityType) {
                PasswordType.REGISTER -> {
                    appbar.txtToolbarTitle.text = getString(R.string.create_password)
                }
                PasswordType.FORGOT_PASSWORD -> {
                    appbar.txtToolbarTitle.text = getString(R.string.recreate_password)
                    passwordTxtPage.visibility = View.INVISIBLE
                    passwordTxtTitle.text = getString(R.string.recreate_new_password)
                    passwordBtnNext.text = getString(R.string.recreate_password)
                }
                PasswordType.CHANGE_PASSWORD ->{
                    passwordTxtPage.visibility = View.INVISIBLE
                    appbar.txtToolbarTitle.text = getString(R.string.str_change_password)
                }
            }
        }
    }

    override fun initUiListener() {
        viewBinding?.run {
            passwordEdtxPassword.textChanges()
                .doOnNext {
                    linearValidation.isVisible = passwordEdtxPassword.isFocused
                    baseViewModel?.validatePassword(it.toString())
                }
                .subscribe {
                    baseViewModel?.isPasswordAndRePasswordValid(
                        passwordEdtxPassword.text.toString(),
                        passwordEdtxRePassword.text.toString()
                    )
                }
                .disposedBy(disposable)

            passwordEdtxRePassword.textChanges()
                .filter { viewBinding!!.passwordEdtxRePassword.isFocused }
                .map {
                    baseViewModel?.isPasswordAndRePasswordValid(
                        passwordEdtxPassword.text.toString(),
                        passwordEdtxRePassword.text.toString()
                    )
                    baseViewModel?.isPasswordEquals?.value!!
                }
                .subscribe { viewBinding!!.passwordTxtErrorPassword.isVisible = !it }
                .disposedBy(disposable)

            passwordBtnNext.onSingleClick()
                .subscribe { onBtnClicked() }
                .disposedBy(disposable)
        }
    }

    private fun onBtnClicked() {
        if (activityType == PasswordType.REGISTER) {
            router.openTermCondition(this@PasswordActivity, registerInfo.apply {
                password = viewBinding?.passwordEdtxPassword?.text.toString()
                passwordConfirm = viewBinding?.passwordEdtxRePassword?.text.toString()
            })
        } else if (activityType == PasswordType.FORGOT_PASSWORD) {
            viewBinding?.run {
                baseViewModel?.doChangeForgotPassword(
                    token.orEmpty(),
                    passwordEdtxPassword.text.toString(),
                    passwordEdtxRePassword.text.toString(),

                )
            }
        }else{
            viewBinding?.run {
                baseViewModel?.doChangePassword(
                    passwordEdtxPassword.text.toString(),
                    passwordEdtxRePassword.text.toString()
                )
            }
        }
    }

    override fun observeViewModel(viewModel: PasswordVM) {
        viewBinding?.let { view ->
            observe(viewModel.failureLiveData, ::handleFailure)
            observe(viewModel.isLoadingLiveData, ::handleLoading)
            observe(viewModel.isLengtValid,
                { view.passwordValidLength.isChecked = it ?: false })
            observe(viewModel.isContainsLowerText,
                { view.passwordValidLowerCase.isChecked = it ?: false })
            observe(viewModel.isContainsNumber,
                { view.passwordValidNumber.isChecked = it ?: false })
            observe(viewModel.isContainsUpperText,
                { view.passwordValidUpperCase.isChecked = it ?: false })
            observe(viewModel.isPasswordEquals,
                { view.passwordBtnNext.changeStateButton(it ?: false) })
            observe(viewModel.isChangePasswordSuccess, { afterChangePasswordSuccess() })
        }
    }

    private fun afterChangePasswordSuccess() {
        showSuccessSnackbar(getString(R.string.change_password_success)) {
            if (activityType == PasswordType.FORGOT_PASSWORD) {
                router.openLogin(this)
            }else{
                showSuccessSnackbar("Password berhasil diubah"){
                    finish()
                }
            }
        }
    }

    override fun bindViewModel(): PasswordVM {
        return ViewModelProvider(this, viewModelFactory)[PasswordVM::class.java]
    }

    companion object {
        private const val EXTRA_INFO = "RegisterPassword.info"
        private const val EXTRA_TYPE = "RegisterPassword.type"
        private const val EXTRA_TOKEN = "RegisterPassword.token"

        fun createIntent(
            caller: Context,
            type: PasswordType,
            registerInfo: RegisterInfo?,
            token: String? = null
        ): Intent {
            return Intent(caller, PasswordActivity::class.java).apply {
                putExtra(EXTRA_INFO, registerInfo)
                putExtra(EXTRA_TYPE, type)
                token?.let { putExtra(EXTRA_TOKEN, it) }
            }
        }
    }

}
