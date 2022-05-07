package id.altea.care.view.family.formfamily

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import com.jakewharton.rxbinding3.widget.textChanges
import id.altea.care.R
import id.altea.care.core.base.BaseActivityVM
import id.altea.care.core.domain.model.*
import id.altea.care.core.ext.*
import id.altea.care.core.helper.validator.KtpNumberValidator.Companion.isKtpNumberValidatorValid
import id.altea.care.core.helper.validator.LastNameValidator.Companion.isLastNameValidatorValid
import id.altea.care.databinding.ActivityAddFamilyBinding
import id.altea.care.view.address.addresslist.AddressListActivity
import id.altea.care.view.common.enums.Gender
import id.altea.care.view.family.FamilyRouter
import id.altea.care.view.family.detailfamily.DetailFamilyActivity
import id.altea.care.view.family.detailfamily.DetailFamilyActivity.Companion.PATIENT_EXTRA_ID
import io.reactivex.Observable
import kotlinx.android.synthetic.main.activity_add_family.*
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_register_personal_info.*
import java.util.*


class FormFamilyActivity : BaseActivityVM<ActivityAddFamilyBinding, FamilyVM>() {
    private val viewModel by viewModels<FamilyVM> { viewModelFactory }
    private var familyContacts: List<FamilyContact>? = null
    private var router = FamilyRouter()
    private var countryHasClicked = false
    private var genderHasClicked = false
    private var familyContactHasClicked = false
    private var birthDateHasClicked = false
    private var isAddressHasClicked = false
    private var userAddress: UserAddress? = null
    private var familyContact: FamilyContact? = null

    private val detailFamily by lazy { intent.getParcelableExtra<DetailPatient>(EXTRA_DETAIL_PATIENT) }

    private val formType by lazy { intent.getSerializableExtra(EXTRA_FORM_TYPE) as FamilyFormType }


    override fun observeViewModel(viewModel: FamilyVM) {
        observe(viewModel.failureLiveData, ::handleFailure)
        observe(viewModel.isLoadingLiveData, ::handleLoading)
        observe(viewModel.familyContact, ::getFamilyContact)
        observe(viewModel.statusAddUpdateFamilyEvent, ::onFamilyEventStatus)
        observe(viewModel.countryEvent, ::handleCountryEvent)
    }

    override fun bindViewModel(): FamilyVM = viewModel

    override fun enableBackButton(): Boolean = true

    override fun bindToolbar(): Toolbar? = viewBinding?.appbar?.toolbar

    override fun getUiBinding(): ActivityAddFamilyBinding = ActivityAddFamilyBinding.inflate(layoutInflater)

    override fun onFirstLaunch(savedInstanceState: Bundle?) {
        viewBinding?.run {
            when (formType) {
                FamilyFormType.CREATE -> {
                    appbar.txtToolbarTitle.text = getString(R.string.toolbar_add_family)
                    addFamilyBtnNext.text = getString(R.string.str_save)
                }
                FamilyFormType.UPDATE -> {
                    appbar.txtToolbarTitle.text = getString(R.string.toolbar_change_family)
                    addFamilyBtnNext.text = getString(R.string.str_update)
                }
            }
        }

        val validationList = arrayOf(
                firstNameValidationObservable(),
                contactFamilyValidationObservable(),
                lastNameValidationObservable(),
                genderValidationObservable(),
                birthDateValidationObservable(),
                countryValidationObservable(),
                citizenshipValidationObservable(),
                placeValidationObservable(),
                ktpNumberValidataionObservable(),
                addressValidationObservable())

        Observable.combineLatest(validationList) { booleans ->
            booleans.none { !(it as Boolean) }
        }.subscribe {
            addFamilyBtnNext.changeStateButton(it)
        }.disposedBy(disposable)


    }

    override fun initUiListener() {
        viewBinding?.run {
            addFamilyTxtFamilyContact.onSingleClick().subscribe {
                viewModel.getFamilyContact()
                familyContactHasClicked = true
            }.disposedBy(disposable)

            addFamilytxtBirthCountry.onSingleClick().subscribe {
                baseViewModel?.doClickCountry(CountrySelectedType.BIRTHCOUTRY)
            }.disposedBy(disposable)

            addFamilyTxtGender.onSingleClick().subscribe {
                genderHasClicked = true
                router.openGenderBottomSheet(this@FormFamilyActivity,
                        submitCallback = { addFamilyTxtGender.text = it },
                        dismissCallback = {
                            addFamilyTxtErrorGender.isVisible = addFamilyTxtGender.text.isEmpty()
                        })
            }.disposedBy(disposable)

            addFamilyEdtxCitizenship.onSingleClick().subscribe {
                baseViewModel?.doClickCountry(CountrySelectedType.CARDIDCOUNTRY)
            }.disposedBy(disposable)

            addFamilyTxtBirthDate.onSingleClick()
                    .subscribe { showDatePicker() }
                    .disposedBy(disposable)

            addFamilyTxtAddress.onSingleClick().subscribe {
                isAddressHasClicked = true
                resultFilter.launch(AddressListActivity.createIntent(this@FormFamilyActivity, 2))
            }.disposedBy(disposable)


            addFamilyBtnNext.onSingleClick().subscribe {
                onFamilyAdded()
            }.disposedBy(disposable)


            detailFamily?.let {
                familyContact = FamilyContact(it.familyRelationType?.id, it.familyRelationType?.name)
                addFamilyTxtFamilyContact.text = it.familyRelationType?.name
                addFamilyEdtxFirstName.setText(it.firstName)
                addFamilyEdtxLastName.setText(it.lastName)
                addFamilyEdtxKtpNumber.setText(it.cardId)
                val gender = if (it.gender == Gender.MALE.name) getString(R.string.gender_male) else getString(R.string.gender_female)
                addFamilyTxtGender.setText(gender)
                addFamilyTxtBirthDate.setText(it.birthDate?.toNewFormat("yyyy-MM-dd", "dd/MM/yyyy"))
                addFamilyEdtxBirthPlace.setText(it.birthPlace)
                addFamilytxtBirthCountry.text = it.birthCountry?.name
                addFamilyEdtxCitizenship.text = it.nationality?.name
                addFamilyTxtAddress.text = it.completeAddress()
                baseViewModel?.birthCountrySelected = Country(it.birthCountry?.code,it.birthCountry?.id,it.birthCountry?.name)
                baseViewModel?.cardCountrySelected = Country(it.nationality?.code,it.nationality?.id,it.nationality?.name)
                userAddress = userAddress?.apply {
                    this.id = it.addressId
                }
            }

        }

    }

    private fun onFamilyAdded() {
        val familyInfo = FamilyInfo(
                familyContact?.id,
                viewBinding?.addFamilyTxtFamilyContact?.text.toString(),
                viewBinding?.addFamilyEdtxFirstName?.text.toString(),
                viewBinding?.addFamilyEdtxLastName?.text.toString().orEmpty(),
                viewBinding?.addFamilyEdtxKtpNumber?.text.toString(),
                if (viewBinding?.addFamilyTxtGender?.text.toString() == getString(R.string.gender_male)) {
                    Gender.MALE
                } else {
                    Gender.FEMALE
                },
                viewBinding?.addFamilyTxtBirthDate?.text.toString(),
                viewBinding?.addFamilyEdtxBirthPlace?.text.toString(),
                baseViewModel?.birthCountrySelected?.countryId,
                baseViewModel?.birthCountrySelected?.name,
                baseViewModel?.cardCountrySelected?.countryId,
                baseViewModel?.cardCountrySelected?.name,
                userAddress?.id,
                "",
                ""
        )

        when (formType) {
            FamilyFormType.CREATE -> {
                router.openDecissionBottomSheet(this,submitAsAccount = {
                    router.openRegisterContactInfo(this,familyInfo,resultFilter)
                },submitAsFamilyProfile = {
                    router.openConfirmationBottomSheet(this@FormFamilyActivity, familyInfo, submitCallback = {
                        if (formType == FamilyFormType.CREATE) {
                            viewModel.addFamilyMember(familyInfo)
                        }
                    })
                })
            }
            FamilyFormType.UPDATE -> {
                router.openConfirmationBottomSheet(this@FormFamilyActivity, familyInfo, submitCallback = {
                    if (formType == FamilyFormType.UPDATE) {
                        viewModel.updateFamilyMember(familyInfo, detailFamily?.id.orEmpty())
                    }
                })
            }
        }


    }

    private fun openBottomSheet(listSelectedModel: List<SelectedModel>?,
                                selectedModel: SelectedModel?){
        router.openFormBottomSheet(
            supportFragmentManager,
            selectedModel,
            listSelectedModel,{
                handleCallbackBottomSheet(it)
            }
        )
    }

    private fun handleCountryEvent(countries : List<Country>?) {
        countries?.let {
            when(baseViewModel?.countrySelectedType){
                CountrySelectedType.BIRTHCOUTRY ->{
                    openBottomSheet(
                        it,
                        baseViewModel?.birthCountrySelected
                    )
                }
                CountrySelectedType.CARDIDCOUNTRY ->{
                    openBottomSheet(
                        it,
                        baseViewModel?.cardCountrySelected
                    )
                }
            }

        }
    }

    private fun handleCallbackBottomSheet(pair  : SelectedModel) {
        viewBinding?.run {
            when(baseViewModel?.countrySelectedType){
                CountrySelectedType.BIRTHCOUTRY ->{
                    baseViewModel?.birthCountrySelected = pair as Country
                    addFamilytxtBirthCountry.text = pair.getTitle()
                }
                CountrySelectedType.CARDIDCOUNTRY ->{
                    baseViewModel?.cardCountrySelected = pair as Country
                    addFamilyEdtxCitizenship.text = pair.getTitle()
                }
            }

        }
    }

    private fun getFamilyContact(familyContacts: List<FamilyContact>?) {
        viewBinding?.run {
            router.openFamilyContactBottomSheet(this@FormFamilyActivity, familyContacts, {
                familyContact = it
                addFamilyTxtFamilyContact.text = it.name
            }, {
                addFamilyTxtErrorFamilyContact.isVisible = addFamilyTxtFamilyContact.text.isEmpty()
            })
        }
    }

    private fun onFamilyEventStatus(result: Boolean?) {

        if (result == true) {
            when (formType) {
                FamilyFormType.CREATE -> {
                    setResult(RESULT_OK)
                    finish()
                }
                FamilyFormType.UPDATE -> {
                    val intent = Intent()
                    intent.putExtra(PATIENT_EXTRA_ID,detailFamily)
                    setResult(RESULT_OK,intent)
                    finish()
                }
            }
        }
    }

    private val resultFilter =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == 2) {
                    val result = it.data?.getParcelableExtra<UserAddress>("address")
                    viewBinding?.addFamilyTxtAddress?.text = result?.getCompleteAddress(this)
                    userAddress = result
                }else {
                    setResult(DetailFamilyActivity.RESULT_UPDATE_USER)
                    finish()
                }
            }

    private fun contactFamilyValidationObservable(): Observable<Boolean> {
        return addFamilyTxtFamilyContact.emptyViewValidator { _, isValid ->
            if (familyContactHasClicked) viewBinding?.addFamilyTxtErrorFamilyContact?.isVisible = !isValid
        }
    }

    private fun firstNameValidationObservable(): Observable<Boolean> {
        return addFamilyEdtxFirstName.emptyViewValidator { view, isValid ->
            if (view.isFocused)
                addFamilyTxtErrorFirstName.isVisible = !isValid
        }
    }

    private fun lastNameValidationObservable(): Observable<Boolean> {
        return addFamilyEdtxLastName.textChanges().map {
            isLastNameValidatorValid(it.toString())
        }.doOnNext {
            if (viewBinding!!.addFamilyEdtxLastName.isFocused) {
                viewBinding!!.addFamilyTxtErrorLastName.isVisible = !it
            }
        }
    }

    private fun genderValidationObservable(): Observable<Boolean> {
        return addFamilyTxtGender.emptyViewValidator { _, isValid ->
            if (genderHasClicked) addFamilyTxtErrorGender.isVisible = !isValid
        }
    }

    private fun birthDateValidationObservable(): Observable<Boolean> {
        return addFamilyTxtBirthDate.emptyViewValidator { _, isValid ->
            if (birthDateHasClicked) addFamilyTxtErrorBirthDate.isVisible = !isValid
        }
    }

    private fun countryValidationObservable(): Observable<Boolean> {
        return addFamilytxtBirthCountry.emptyViewValidator { view, isValid ->
            if (view.isFocused)
                addFamilyTxtErrorBirthCountry.isVisible = !isValid
        }
    }

    private fun placeValidationObservable(): Observable<Boolean> {
        return addFamilyEdtxBirthPlace.emptyViewValidator { view, isValid ->
            if (view.isFocused)
                addFamilyTxtErrorBirthPlace.isVisible = !isValid
        }
    }

    private fun citizenshipValidationObservable(): Observable<Boolean> {
        return addFamilyEdtxCitizenship.emptyViewValidator { view, isValid ->
            if (view.isFocused)
                addFamilyTxtErrorCitizenship.isVisible = !isValid
        }
    }

    private fun addressValidationObservable(): Observable<Boolean> {
        return addFamilyTxtAddress.emptyViewValidator { _, isValid ->
            if (isAddressHasClicked)
                addFamilyTxtErrorAddress.isVisible = !isValid
        }
    }


    private fun ktpNumberValidataionObservable(): Observable<Boolean> {
        return addFamilyEdtxKtpNumber.textChanges().map {
            it.isNotEmpty() && isKtpNumberValidatorValid(it.toString())
        }.doOnNext {
            if (viewBinding!!.addFamilyEdtxKtpNumber.isFocused) {
                viewBinding!!.addFamilyTxtErrorKtpNumber.isVisible = !it
                viewBinding?.addFamilyTxtNoteKtpNumber?.isVisible = it
            }
        }
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        birthDateHasClicked = true
        showDatePickerDialog(
                this,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH),
                onDateSelected = { addFamilyTxtBirthDate.text = it },
                onDismissListener = {
                    addFamilyTxtErrorBirthDate.isVisible =
                            addFamilyTxtBirthDate.text.isEmpty()
                })
    }


    companion object {
        private const val EXTRA_DETAIL_PATIENT = "FamilyForm.UserAddress"
        private const val EXTRA_FORM_TYPE = "FamilyForm.FormType"
        fun createIntent(caller: Context, detailPatient: DetailPatient?, familyFormType: FamilyFormType): Intent {
            return Intent(caller, FormFamilyActivity::class.java).apply {
                putExtra(EXTRA_DETAIL_PATIENT, detailPatient)
                putExtra(EXTRA_FORM_TYPE, familyFormType)
            }
        }
    }

}

enum class FamilyFormType {
    CREATE, UPDATE
}

enum class CountrySelectedType{
    BIRTHCOUTRY,CARDIDCOUNTRY
}