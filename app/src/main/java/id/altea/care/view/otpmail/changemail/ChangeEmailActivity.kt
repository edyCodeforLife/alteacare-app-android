package id.altea.care.view.otpmail.changemail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import com.jakewharton.rxbinding3.widget.textChanges
import id.altea.care.R
import id.altea.care.core.base.BaseActivityVM
import id.altea.care.core.ext.changeStateButton
import id.altea.care.core.ext.disposedBy
import id.altea.care.core.ext.observe
import id.altea.care.core.ext.onSingleClick
import id.altea.care.core.helper.util.ConstantExtra
import id.altea.care.core.helper.validator.EmailValidator
import id.altea.care.databinding.ActivityChangeEmailBinding
import io.reactivex.android.schedulers.AndroidSchedulers
import java.util.concurrent.TimeUnit

/**
 * Created by trileksono on 10/03/21.
 */
class ChangeEmailActivity : BaseActivityVM<ActivityChangeEmailBinding, ChangeEmailVM>() {

    private val oldEmail by lazy { intent.getStringExtra(EXTRA_EMAIL) }
    private val tokenRegister by lazy { intent.getStringExtra(EXTRA_TOKEN) }
    private val router = ChangeEmailRouter()

    override fun enableBackButton(): Boolean = true

    override fun bindToolbar(): Toolbar? = viewBinding?.appbar?.toolbar

    override fun getUiBinding(): ActivityChangeEmailBinding {
        return ActivityChangeEmailBinding.inflate(layoutInflater)
    }

    override fun onFirstLaunch(savedInstanceState: Bundle?) {
        viewBinding?.run {
            appbar.txtToolbarTitle.text = getString(R.string.email_verification)
        }
    }

    override fun initUiListener() {
        viewBinding?.run {
            changeEmailEdtxEmail.textChanges()
                .debounce(300, TimeUnit.MILLISECONDS)
                .map {
                    it.isNotEmpty() && !it.toString().equals(oldEmail, true)
                            && EmailValidator().isValidEmail(it.toString())
                }
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext {
                    if (changeEmailEdtxEmail.isFocused) changeEmailTxtErrorEmail.isVisible = !it
                }
                .subscribe {
                    if (changeEmailEdtxEmail.text.toString().equals(oldEmail, true)) {
                        changeEmailTxtErrorEmail.text = getString(R.string.error_duplicate_email)
                    } else {
                        changeEmailTxtErrorEmail.text = getString(R.string.error_invalid_email)
                    }
                    changeEmailBtnVerification.changeStateButton(it)
                }.disposedBy(disposable)

            if (!oldEmail.isNullOrEmpty() && !tokenRegister.isNullOrEmpty()) {
                changeEmailBtnVerification.onSingleClick().subscribe {
                        baseViewModel?.doChangeEmail(
                            changeEmailEdtxEmail.text.toString(),
                            tokenRegister!!
                        )
                    }.disposedBy(disposable)
            } else {
                changeEmailBtnVerification.onSingleClick().subscribe {
                    onGetResult()
                }.disposedBy(disposable)
            }
        }
    }

    private fun createResult() {
        val intent = Intent().putExtra(
            ConstantExtra.EXTRA_CHANGE_EMAIL, viewBinding?.changeEmailEdtxEmail?.text.toString()
        )
        setResult(RESULT_OK, intent)
        finish()
    }

    override fun observeViewModel(viewModel: ChangeEmailVM) {
        observe(viewModel.failureLiveData, ::handleFailure)
        observe(viewModel.isLoadingLiveData, ::handleLoading)
        observe(viewModel.changeEmailState, { createResult() })
    }

    private fun onGetResult() {
        router.openEmailOtp(this@ChangeEmailActivity, oldEmail!!, viewBinding?.changeEmailEdtxEmail?.text.toString(),"")
    }


    override fun bindViewModel(): ChangeEmailVM {
        return ViewModelProvider(this, viewModelFactory)[ChangeEmailVM::class.java]
    }

    companion object {
        private const val EXTRA_EMAIL = "ChangeEmail.email"
        private const val EXTRA_TOKEN = "ChangeEmail.tokenRegist"
        fun createIntent(caller: Context, email: String, token: String): Intent {
            return Intent(caller, ChangeEmailActivity::class.java)
                .putExtra(EXTRA_EMAIL, email)
                .putExtra(EXTRA_TOKEN, token)
        }
    }
}
