package id.altea.care.view.contact

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import com.jakewharton.rxbinding3.widget.textChanges
import id.altea.care.R
import id.altea.care.core.base.BaseActivityVM
import id.altea.care.core.domain.model.InformationCentral
import id.altea.care.core.domain.model.Profile
import id.altea.care.core.domain.model.ResultFilter
import id.altea.care.core.ext.*
import id.altea.care.core.helper.validator.PhoneValidator
import id.altea.care.databinding.ActivityContactBinding
import id.altea.care.view.common.ui.searchselectable.SearchSelectableActivity
import io.reactivex.Observable
import kotlinx.android.synthetic.main.activity_contact.*
import kotlinx.android.synthetic.main.activity_register_personal_info.*
import timber.log.Timber


/**
 * Created by trileksono on 11/03/21.
 */
class ContactActivity : BaseActivityVM<ActivityContactBinding, ContactVM>() {

    private val viewModel by viewModels<ContactVM> { viewModelFactory }
    private val router = ContactRouter()
    private var idMessage: String? = null
    private var userId : String? =null
    override fun observeViewModel(viewModel: ContactVM) {
        observe(viewModel.failureLiveData, ::handleFailure)
        observe(viewModel.isLoadingLiveData, ::handleLoading)
        observe(viewModel.informationCentralLivedata, ::getInformationCentral)
        observe(viewModel.resultLiveData, ::getResult)
        observe(viewModel.profile, ::handleProfile)
    }


    private fun getResult(result: Boolean?) {
        viewBinding?.run {
            result?.let {
                if (it) {
                    textContentMessageCallCenter.setText("")
                    textTypeMessageCallCenter.text = ""
                    callCenterEdtxName.setText("")
                    callCenterEdtxPhone.setText("")
                    callCenterEdtxEmail.setText("")
                    showInfoSnackbar(getString(R.string.str_success_send_message)) {
                        router.openAccount(this@ContactActivity)
                    }
                }
            }
        }
    }

    override fun bindViewModel(): ContactVM = viewModel

    override fun enableBackButton(): Boolean = true

    override fun bindToolbar(): Toolbar? = viewBinding?.appbar?.toolbar

    override fun getUiBinding(): ActivityContactBinding {
        return ActivityContactBinding.inflate(layoutInflater)
    }

    override fun onFirstLaunch(savedInstanceState: Bundle?) {
        viewModel.onFirstLaunch()
        viewBinding?.run {
            appbar.txtToolbarTitle.text = getString(R.string.contact_alteacare)
            Observable.combineLatest(
                nameValidationObservable(),
                contentValidationObservable(),
                messageTypeValidationObservable(),
                emailValidationObservable(),
                phoneValidationObservable(),
                { t1,t2,t3,t4,t5 -> t1 && t2 && t3 && t4 && t5  }
            ).subscribe {
                btnSendMessageCallCenter.changeStateButton(it)
            }.disposedBy(disposable)


        }


    }

    private fun messageTypeValidationObservable(): Observable<Boolean> {
        return viewBinding!!.textTypeMessageCallCenter.emptyViewValidator { view, isValid ->
            if (view.isFocused)
                callCenterPersonalTxtErrorMessageCategory.isVisible = !isValid
        }
    }


    private fun contentValidationObservable(): Observable<Boolean> {
        return viewBinding!!.textContentMessageCallCenter.emptyViewValidator { view, isValid ->
            if (view.isFocused)
                callCenterTxtErrorContent.isVisible = !isValid
        }
    }

    private fun nameValidationObservable(): Observable<Boolean> {
        return viewBinding!!.callCenterEdtxName.emptyViewValidator { view, isValid ->
            if (view.isFocused)
                callCenterPersonalTxtErrorName.isVisible = !isValid
        }
    }

    private fun emailValidationObservable(): Observable<Boolean> {
        return viewBinding!!.callCenterEdtxEmail.emailValidator { view, b ->
            if (view.isFocused) viewBinding!!.callCenterContactTxtErrorEmail.isVisible = !b
        }
    }

    private fun phoneValidationObservable(): Observable<Boolean> {
        return viewBinding!!.callCenterEdtxPhone.textChanges()
            .doOnNext {
                if (it.startsWith("0"))
                    viewBinding!!.callCenterEdtxPhone.setText(it.substring(1))
            }
            .map { it.isNotEmpty() && PhoneValidator.isPhoneNumberValid(it.toString()) }
            .doOnNext {
                if (viewBinding!!.callCenterEdtxPhone.isFocused) {
                    viewBinding!!.callCenterContactTxtErrorPhone.isVisible = !it
                }
            }
    }

    override fun initUiListener() {
        viewBinding?.run {
            textTypeMessageCallCenter.onSingleClick().subscribe {
                resultFilter.launch(
                    SearchSelectableActivity.createIntent(
                        this@ContactActivity,
                        getString(R.string.str_message_type),
                        listOf()
                    )
                )
            }.disposedBy(disposable)

            btnSendMessageCallCenter.onSingleClick().subscribe {
                idMessage?.let {
                    viewModel.sendMessage(
                        callCenterEdtxName.text.toString(),
                        callCenterEdtxEmail.text.toString(),
                        callCenterEdtxPhone.text.toString(),
                        userId,
                        it, textContentMessageCallCenter.text.toString()
                    )
                }
            }.disposedBy(disposable)


            callCenterBtnTelphon.onSingleClick().subscribe {
                val i = Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/${textWaDataCallCenter.text}?text=hallo altea care"))
                startActivity(i)
            }.disposedBy(disposable)


            textDataEmailCallCenter.onSingleClick().subscribe{
                intentViewEmail(Uri.parse(textDataEmailCallCenter.text.toString()))
            }
        }

    }

    private val resultFilter =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                val result = it.data?.getParcelableArrayListExtra<ResultFilter>("data")
                result?.let { resultFilters ->
                    Timber.e(resultFilters[0].id)
                    if (resultFilters.size != 0) {
                        viewBinding?.textTypeMessageCallCenter?.text = resultFilters[0].text
                        idMessage = resultFilters[0].id
                    }

                }
            }
        }

    private fun getInformationCentral(informationCentral: InformationCentral?) {
        viewBinding?.run {
            informationCentral?.let {
                textDataEmailCallCenter.text = it.contentResponse?.email
                textWaDataCallCenter.text = it.contentResponse?.phone
                textCentralInformation.text = it.title
            }
        }

    }

    private fun handleProfile(profile: Profile?) {
        profile?.let{
            userId = profile.id
        }
    }


    private fun intentViewEmail(uri : Uri){
        val i = Intent(Intent.ACTION_SEND)
        i.type = "message/rfc822"
        i.putExtra(Intent.EXTRA_EMAIL, arrayOf("$uri"))
        i.putExtra(Intent.EXTRA_SUBJECT, "subject of email")
        i.putExtra(Intent.EXTRA_TEXT, "body of email")
        try {
            startActivity(Intent.createChooser(i, "Send mail..."))
        } catch (ex: ActivityNotFoundException) {
            Toast.makeText(this@ContactActivity, "There are no email clients installed.", Toast.LENGTH_SHORT).show()
        }

    }
}
