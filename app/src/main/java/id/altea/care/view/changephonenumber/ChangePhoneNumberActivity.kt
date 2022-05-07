package id.altea.care.view.changephonenumber

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import com.jakewharton.rxbinding3.widget.textChanges
import id.altea.care.R
import id.altea.care.core.base.BaseActivityVM
import id.altea.care.core.ext.changeStateButton
import id.altea.care.core.ext.disposedBy
import id.altea.care.core.ext.observe
import id.altea.care.core.ext.onSingleClick
import id.altea.care.core.helper.util.ConstantExtra
import id.altea.care.core.helper.validator.PhoneValidator
import id.altea.care.databinding.ActivityChangePhoneNumberBinding
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.activity_change_phone_number.*
import java.util.concurrent.TimeUnit

class ChangePhoneNumberActivity  : BaseActivityVM<ActivityChangePhoneNumberBinding, ChangePhoneNumberVM>(){

    private val viewModel by viewModels<ChangePhoneNumberVM> { viewModelFactory }
    private val router = ChangePhoneNumberRouter()
    private val oldPhoneNumber by lazy { intent.getStringExtra(EXTRA_PHONE_NUMBER) }
    private val tokenRegister by lazy { intent.getStringExtra(EXTRA_TOKEN) }

    override fun observeViewModel(viewModel: ChangePhoneNumberVM) {
        observe(viewModel.isLoadingLiveData, ::handleLoading)
        observe(viewModel.changePhonerNumberState, { createResult() })
        observe(viewModel.failureLiveData, ::handleFailure)
    }

    private fun createResult() {
        val intent = Intent().putExtra(
            ConstantExtra.EXTRA_CHANGE_PHONE_NUMBER, viewBinding?.changeContactEdtxPhone?.text.toString()
        )
        setResult(RESULT_OK, intent)
        finish()
    }

    override fun bindViewModel(): ChangePhoneNumberVM = viewModel

    override fun enableBackButton(): Boolean = true

    override fun bindToolbar(): Toolbar? = viewBinding?.appbar?.toolbar

    override fun getUiBinding(): ActivityChangePhoneNumberBinding = ActivityChangePhoneNumberBinding.inflate(layoutInflater)

    override fun onFirstLaunch(savedInstanceState: Bundle?) {
        viewBinding?.run {
            appbar.txtToolbarTitle.text = getString(R.string.verification_phone_number)
        }
    }

    override fun initUiListener() {
        viewBinding?.run {
            changeContactEdtxPhone
                .textChanges().debounce(300,TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext {
                    if (it.startsWith("0"))
                        viewBinding!!.changeContactEdtxPhone.setText(it.substring(1))
                }
                .map {
                    it.isNotEmpty() && PhoneValidator.isPhoneNumberValid(it.toString()) && viewBinding?.changeContactEdtxPhone?.text.toString() != oldPhoneNumber?.substring(2)
                }.doOnNext {
                    if (viewBinding!!.changeContactEdtxPhone.isFocused) {
                        viewBinding!!.changeContactTxtErrorPhone.isVisible = !it
                    }
                }.subscribe {
                    viewBinding.run {
                        if (changeContactEdtxPhone.text.toString() != oldPhoneNumber?.substring(2)) {
                            changeContactTxtErrorPhone.text = getString(R.string.error_invalid_phone)
                        } else {
                            changeContactTxtErrorPhone.text = getString(R.string.str_duplcicate_phone_number)
                        }
                        changeContactBtnVerification.changeStateButton(it)
                    }
                }.disposedBy(disposable)

            if (!oldPhoneNumber.isNullOrEmpty() && !tokenRegister.isNullOrEmpty()) {
                changeContactBtnVerification.onSingleClick().subscribe {
                    baseViewModel?.doChangePhoneNumberRegister(changeContactEdtxPhone.text.toString(), tokenRegister!!)
                }.disposedBy(disposable)
            } else {
                changeContactBtnVerification.onSingleClick().subscribe {
                    finish()
                    router.openSmsOtp(this@ChangePhoneNumberActivity, oldPhoneNumber.orEmpty(), changeContactEdtxPhone.text.toString(), "")
                }
            }
        }
    }

    companion object {
        private const val EXTRA_PHONE_NUMBER = "ChangePhoneNumber.phonenumber"
        private const val EXTRA_TOKEN = "ChangeEmail.tokenRegist"
        fun createIntent(
            caller: Context,
            phoneNumber : String?,
            token: String
        ): Intent {
            return Intent(caller, ChangePhoneNumberActivity::class.java)
                .putExtra(EXTRA_PHONE_NUMBER, phoneNumber)
                .putExtra(EXTRA_TOKEN, token)
        }
    }

}