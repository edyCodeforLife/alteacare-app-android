package id.altea.care.view.register.contactinfo

import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import com.jakewharton.rxbinding3.widget.textChanges
import id.altea.care.R
import id.altea.care.core.base.BaseActivityVM
import id.altea.care.core.data.request.AddCheckUserRequest
import id.altea.care.core.domain.model.CheckUser
import id.altea.care.core.domain.model.FamilyInfo
import id.altea.care.core.domain.model.RegisterInfo
import id.altea.care.core.ext.*
import id.altea.care.core.helper.validator.PhoneValidator
import id.altea.care.databinding.ActivityRegisterContactBinding
import id.altea.care.view.family.detailfamily.DetailFamilyActivity
import io.reactivex.Observable
import kotlinx.android.synthetic.main.activity_register_contact.*
import timber.log.Timber

/**
 * Created by trileksono on 05/03/21.
 */
class RegisterContactActivity :
    BaseActivityVM<ActivityRegisterContactBinding, RegisterContactVM>() {

    private lateinit var mEmail: String
    private lateinit var mPhone: String

    private var registerInfo: RegisterInfo? = null

    private val registerContactState by lazy {
        intent.getSerializableExtra(EXTRA_STATE) as RegisterContactState
    }
    private val patientId by lazy {
        intent.getStringExtra(EXTRA_PATIENT_ID)
    }

    private var familyInfo: FamilyInfo? = null
    private var registerContactRouter = RegisterContactRouter()

    override fun enableBackButton(): Boolean = true

    override fun bindToolbar(): Toolbar? = viewBinding?.appbar?.toolbar

    override fun getUiBinding(): ActivityRegisterContactBinding {
        return ActivityRegisterContactBinding.inflate(layoutInflater)
    }

    override fun onFirstLaunch(savedInstanceState: Bundle?) {

        intent?.getParcelableExtra<RegisterInfo>(EXTRA_INFO)?.let {
            registerInfo = it
        }

        intent.getParcelableExtra<FamilyInfo>(EXTRA_FAMILY_INFO).let {
            familyInfo = it
        }

        registerContactState.let {
            when (it) {
                RegisterContactState.PAGE_FAMILY -> {
                    viewBinding?.registerContactTextDesc?.isVisible = true
                    viewBinding?.registerContactTextPage?.isVisible = false
                    viewBinding?.appbar?.txtToolbarTitle?.text =
                        getString(R.string.str_register_account)
                }
                RegisterContactState.PAGE_REGISTER -> {
                    viewBinding?.appbar?.txtToolbarTitle?.text = getString(R.string.register)
                    viewBinding?.registerContactTextPage?.text = "1/3"
                    viewBinding?.registerContactTextDesc?.isVisible = false
                }
                else -> {
                    viewBinding?.registerContactTextDesc?.isVisible = true
                    viewBinding?.registerContactTextPage?.isVisible = false
                    viewBinding?.appbar?.txtToolbarTitle?.text =
                        getString(R.string.str_register_account)
                }
            }
        }
    }

    override fun initUiListener() {
        viewBinding?.run {
            Observable.combineLatest(
                emailValidationObservable(),
                phoneValidationObservable(),
                { t1, t2 -> t1 && t2 }
            ).subscribe {
                registerContactBtnNext.changeStateButton(it)
            }.disposedBy(disposable)

            registerContactBtnNext.onSingleClick().subscribe {
                checkUser()
            }.disposedBy(disposable)
        }
    }

    private fun checkUser() {
        viewBinding?.run {
            mEmail = registerContactEdtxEmail.text.toString()
            mPhone = registerContactEdtxPhone.text.toString()
            baseViewModel?.checkUser(AddCheckUserRequest(email = mEmail, phone = mPhone))
        }
    }

    private fun onButtonRegisterClicked(checkUser: CheckUser) {
        viewBinding?.run {
            registerContactState.let {
                when (it) {
                    RegisterContactState.PAGE_FAMILY -> {
                        familyInfo?.apply {
                            email = registerContactEdtxEmail.text.toString()
                            phone = registerContactEdtxPhone.text.toString()
                        }
                        familyInfo?.let { baseViewModel?.addFamilyRegisterMemberAccount(it) }
                    }
                    RegisterContactState.PAGE_FAMILY_EXISTING -> {
                        baseViewModel?.addFamilyExistingAccountMember(
                            patientId.orEmpty(),
                            registerContactEdtxEmail.text.toString(),
                            registerContactEdtxPhone.text.toString()
                        )
                    }
                    RegisterContactState.PAGE_REGISTER -> {
                        registerContactRouter.openRegisterPersonalInfoActivity(
                            this@RegisterContactActivity, RegisterInfo(
                                "", "", "",
                                phoneNumber = mPhone,
                                email = mEmail
                            )
                        )
                    }
                }
            }
        }
    }

    private fun emailValidationObservable(): Observable<Boolean> {
        return viewBinding!!.registerContactEdtxEmail.emailValidator { view, b ->
            if (view.isFocused) viewBinding!!.registerContactTxtErrorEmail.isVisible = !b
        }
    }

    private fun phoneValidationObservable(): Observable<Boolean> {
        return viewBinding!!.registerContactEdtxPhone.textChanges()
            .doOnNext {
                if (it.startsWith("0"))
                    viewBinding!!.registerContactEdtxPhone.setText(it.substring(1))
            }
            .map { it.isNotEmpty() && PhoneValidator.isPhoneNumberValid(it.toString()) }
            .doOnNext {
                if (viewBinding!!.registerContactEdtxPhone.isFocused) {
                    viewBinding!!.registerContactTxtErrorPhone.isVisible = !it
                }
            }
    }

    companion object {
        const val EXTRA_INFO = "RegisterContact.registerInfo"
        const val EXTRA_STATE = "RegisterContact.registerState"
        const val EXTRA_FAMILY_INFO = "RegisterContact.familyInfo"
        const val EXTRA_PATIENT_ID = "RegisterContact.patientId"
    }

    override fun observeViewModel(viewModel: RegisterContactVM) {
        observe(viewModel.failureLiveData, ::handleFailure)
        observe(viewModel.isLoadingLiveData, ::handleLoading)
        observe(viewModel.statusAddUpdateFamilyEvent, ::onFamilyEventStatus)
        observe(viewModel.addFamilyMemberExistingEvent, { onFamilyExistingEvent() })
        viewModel.checkUserEvent.observe(this, { checkUser ->
            handleCheckUserEvent(checkUser)
        })
    }

    private fun handleCheckUserEvent(checkUser: CheckUser) {
        Timber.d("hai -> $checkUser")
        if (checkUser.isEmailAvailable == false && checkUser.isPhoneAvailable == false) {
            showErrorSnackbar("${checkUser.email?.error}\n${checkUser.phone?.error}")
        } else if (checkUser.email?.isAvailable == false) {
            showErrorSnackbar(checkUser.email.error ?: "")
        } else if (checkUser.phone?.isAvailable == false) {
            showErrorSnackbar(checkUser.phone.error ?: "")
        } else {
            onButtonRegisterClicked(checkUser)
        }
    }

    private fun onFamilyExistingEvent() {
        showInfoSnackbar(getString(R.string.str_email_success)) {
            setResult(DetailFamilyActivity.RESULT_UPDATE_USER)
            finish()
        }
    }

    override fun bindViewModel(): RegisterContactVM {
        return  ViewModelProvider(this, viewModelFactory)[RegisterContactVM::class.java]
    }


    private fun onFamilyEventStatus(result: Boolean?) {
        if (result == true) {
           showInfoSnackbar(getString(R.string.str_email_success)){
               setResult(DetailFamilyActivity.RESULT_UPDATE_USER)
               finish()
           }
        }
    }

}

enum class RegisterContactState {
    PAGE_REGISTER, PAGE_FAMILY, PAGE_FAMILY_EXISTING
}
