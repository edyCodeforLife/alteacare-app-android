package id.altea.care.view.register.personalinfo

import android.app.Activity
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import id.altea.care.R
import id.altea.care.core.base.BaseActivityVM
import id.altea.care.core.domain.event.AppointmentStatusUpdateEvent
import id.altea.care.core.domain.event.CallBackEvent
import id.altea.care.core.domain.model.Country
import id.altea.care.core.domain.model.RegisterInfo
import id.altea.care.core.domain.model.ResultFilter
import id.altea.care.core.ext.*
import id.altea.care.core.helper.RxBus
import id.altea.care.databinding.ActivityRegisterPersonalInfoBinding
import id.altea.care.view.common.enums.Gender
import id.altea.care.view.common.ui.searchselectable.SearchSelectableActivity
import io.reactivex.Observable
import kotlinx.android.synthetic.main.activity_register_personal_info.*
import timber.log.Timber
import java.util.*

class RegisterPersonalInfoActivity :
    BaseActivityVM<ActivityRegisterPersonalInfoBinding, RegisterPersonalInfoVM>() {

    // this is just for flag error text validation
    private var birthDateHasClicked = false
    private var genderHasClicked = false
    private var countryHasClicked = false
    private val router = RegisterPersonalInfoRouter()
    private val viewModel by viewModels<RegisterPersonalInfoVM> { viewModelFactory }
    private var resultData: ResultFilter? = null

    private lateinit var registerInfo: RegisterInfo

    override fun enableBackButton(): Boolean = true

    override fun bindToolbar(): Toolbar? = viewBinding?.appbar?.toolbar

    override fun getUiBinding(): ActivityRegisterPersonalInfoBinding {
        return ActivityRegisterPersonalInfoBinding.inflate(layoutInflater)
    }

    override fun onFirstLaunch(savedInstanceState: Bundle?) {
        viewBinding?.run {
            appbar.txtToolbarTitle.text = getString(R.string.register)
        }

        intent?.getParcelableExtra<RegisterInfo>(EXTRA_INFO)?.let { newRegisterInfo ->
            registerInfo = newRegisterInfo
        }
    }

    override fun initUiListener() {
        viewModel.getCountries("indonesia")
        viewBinding?.run {
            Observable.combineLatest(
                firstNameValidationObservable(),
                birthDateValidationObservable(),
                countryValidationObservable(),
                placeValidationObservable(),
                genderValidationObservable(), { t1, t3, t4, t5,t6 -> t1 && t3 && t4 && t5 && t6 })
                .subscribe { registerPersonalBtnNext.changeStateButton(it) }
                .disposedBy(disposable)

            registerPersonalTxtBirthDate.onSingleClick()
                .subscribe { showDatePicker() }
                .disposedBy(disposable)

            registerPersonalTxtGender.onSingleClick()
                .subscribe {
                    genderHasClicked = true
                    router.openGenderBottomSheet(this@RegisterPersonalInfoActivity,
                        submitCallback = { registerPersonalTxtGender.text = it },
                        dismissCallback = {
                            registerPersonalTxtErrorGender.isVisible =
                                registerPersonalTxtGender.text.isEmpty()
                        })
                }
                .disposedBy(disposable)

            registerPersonaltxtBirthCountry.onSingleClick()
                .subscribe {
                    countryHasClicked = true
                    resultFilter.launch(
                        SearchSelectableActivity.createIntent(
                            this@RegisterPersonalInfoActivity,
                            getString(R.string.str_country),
                            listOf("first", "two", "three")
                        )
                    )

                }.disposedBy(disposable)

            registerPersonalBtnNext.onSingleClick()
                .subscribe {
                    val registerInfo = RegisterInfo(
                        registerPersonalEdtxFirstName.text.toString(),
                        registerPersonalEdtxLastName.text.toString(),
                        registerPersonalTxtBirthDate.text.toString(),
                        if (registerPersonalTxtGender.text.toString() == getString(R.string.gender_male)) {
                            Gender.MALE
                        } else {
                            Gender.FEMALE
                        },
                        resultData,
                        registerPersonalEdtxBirthPlace.text.toString(),
                        this@RegisterPersonalInfoActivity.registerInfo.phoneNumber,
                        this@RegisterPersonalInfoActivity.registerInfo.email
                    )
                    router.openConfirmationBottomSheet(this@RegisterPersonalInfoActivity,
                        registerInfo)
                }
        }

        listenRxBus()


    }

    private fun listenRxBus() {
        RxBus.getEvents().subscribe {
            when (it) {
                is CallBackEvent -> router.openRegisterPassword(this@RegisterPersonalInfoActivity, it.regiterInfo)
            }
        }.disposedBy(disposable)
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        birthDateHasClicked = true
        showDatePickerDialog(
            this@RegisterPersonalInfoActivity,
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH),
            onDateSelected = { registerPersonalTxtBirthDate.text = it },
            onDismissListener = {
                registerPersonalTxtErrorBirthDate.isVisible =
                    registerPersonalTxtBirthDate.text.isEmpty()
            })
    }

    private val resultFilter =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                val result = it.data?.getParcelableArrayListExtra<ResultFilter>("data")
                result?.let { resultFilters ->
                    Timber.e(resultFilters.get(0).id)
                    if (resultFilters.size != 0) {
                        viewBinding?.registerPersonaltxtBirthCountry?.text =
                            resultFilters.get(0).text
                        resultData = resultFilters.get(0)
                    }

                }
            }
        }

    private fun countryValidationObservable(): Observable<Boolean> {
        return registerPersonaltxtBirthCountry.emptyViewValidator { view, isValid ->
            if (view.isFocused)
                registerPersonalTxtErrorBirthCountry.isVisible = !isValid
        }
    }

    private fun placeValidationObservable(): Observable<Boolean>{
        return registerPersonalEdtxBirthPlace.emptyViewValidator { view, isValid ->
            if (view.isFocused)
                registerPersonalTxtErrorBirthPlace.isVisible = !isValid
        }
    }

    private fun firstNameValidationObservable(): Observable<Boolean> {
        return registerPersonalEdtxFirstName.emptyViewValidator { view, isValid ->
            if (view.isFocused)
                registerPersonalTxtErrorFirstName.isVisible = !isValid
        }
    }

    private fun lastNameValidationObservable(): Observable<Boolean> {
        return registerPersonalEdtxLastName.emptyViewValidator { view, isValid ->
            if (view.isFocused) registerPersonalTxtErrorLastName.isVisible = !isValid
        }
    }

    private fun birthDateValidationObservable(): Observable<Boolean> {
        return registerPersonalTxtBirthDate.emptyViewValidator { _, isValid ->
            if (birthDateHasClicked) registerPersonalTxtErrorBirthDate.isVisible = !isValid
        }
    }

    private fun genderValidationObservable(): Observable<Boolean> {
        return registerPersonalTxtGender.emptyViewValidator { _, isValid ->
            if (genderHasClicked) registerPersonalTxtErrorGender.isVisible = !isValid
        }
    }

    override fun observeViewModel(viewModel: RegisterPersonalInfoVM) {
        observe(viewModel.registerEvent, ::getCountries)
    }

    private fun getCountries(countries: List<Country>?) {
        if (countries?.size != 0) {
            viewBinding?.registerPersonaltxtBirthCountry?.text = countries?.get(0)?.name
            resultData = ResultFilter(countries?.get(0)?.countryId, countries?.get(0)?.name,"")
        }
    }

    override fun bindViewModel(): RegisterPersonalInfoVM {
        return viewModel
    }

    companion object {
        const val EXTRA_INFO = "RegisterPersonalInfoActivity.registerInfo"
    }

}
