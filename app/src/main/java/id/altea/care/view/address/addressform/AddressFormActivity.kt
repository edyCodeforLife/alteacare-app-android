package id.altea.care.view.address.addressform

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.ViewModelProvider
import id.altea.care.R
import id.altea.care.core.base.BaseActivityVM
import id.altea.care.core.domain.model.*
import id.altea.care.core.exception.Failure
import id.altea.care.core.ext.disposedBy
import id.altea.care.core.ext.observe
import id.altea.care.core.ext.onSingleClick
import id.altea.care.databinding.ActivityAddressFormBinding
import id.altea.care.view.address.addressform.bottomsheet.BottomSheetType
import id.altea.care.view.address.failure.AddressFailure
import timber.log.Timber


class AddressFormActivity : BaseActivityVM<ActivityAddressFormBinding, AddressFormVM>() {

    private val router by lazy { AddressFormRouter() }
    private val formType by lazy { intent.getSerializableExtra(EXTRA_FORM_TYPE) as AddressFormType }
    private val userAddress by lazy { intent.getParcelableExtra<UserAddress>(EXTRA_USER_ADDRESS) }

    override fun enableBackButton(): Boolean = true

    override fun bindToolbar(): Toolbar? = viewBinding?.appbar?.toolbar

    override fun getUiBinding(): ActivityAddressFormBinding {
        return ActivityAddressFormBinding.inflate(layoutInflater)
    }

    override fun onFirstLaunch(savedInstanceState: Bundle?) {
        viewBinding?.run {
            when (formType) {
                AddressFormType.CREATE -> {
                    appbar.txtToolbarTitle.text = getString(R.string.toolbar_add_address)
                    addressFormBtnSave.text = getString(R.string.str_save)
                }
                AddressFormType.UPDATE -> {
                    appbar.txtToolbarTitle.text = getString(R.string.toolbar_edit_address)
                    addressFormBtnSave.text = getString(R.string.str_update)
                }
            }
        }
    }

    override fun initUiListener() {
        viewBinding?.run {
            addressFormTvCountry.onSingleClick(1_000)
                .subscribe { baseViewModel?.doClickCountry() }
                .disposedBy(disposable)

            addressFormTvProvince.onSingleClick()
                .subscribe { baseViewModel?.doClickProvince() }
                .disposedBy(disposable)

            addressFormTvCity.onSingleClick()
                .subscribe { baseViewModel?.doClickCity() }
                .disposedBy(disposable)

            addressFormTvDistrict.onSingleClick()
                .subscribe { baseViewModel?.doClickDistrict() }
                .disposedBy(disposable)

            addressFormTvSubDistrict.onSingleClick()
                .subscribe { baseViewModel?.doClickSubDistrict() }
                .disposedBy(disposable)

            addressFormBtnSave.onSingleClick()
                .subscribe {
                    if (formType == AddressFormType.CREATE) {
                        baseViewModel?.createAddress(
                            addressFormEtAddress.text.toString(),
                            addressFormEtRtRw.text.toString()
                        )
                    } else {
                        userAddress?.let {
                            baseViewModel?.updateAddress(
                                it.id.orEmpty(),
                                addressFormEtAddress.text.toString(),
                                addressFormEtRtRw.text.toString()
                            )
                        }
                    }
                }
                .disposedBy(disposable)


            addressFormEtAddress.doOnTextChanged { text, _, _, _ ->
                baseViewModel?.validateStreet(text.toString())
            }

            addressFormTvCountry.doOnTextChanged { _, _, _, _ ->
                baseViewModel?.validateCountry()
            }

            addressFormTvProvince.doOnTextChanged { _, _, _, _ ->
                baseViewModel?.validateProvince()
            }

            addressFormTvCity.doOnTextChanged { _, _, _, _ ->
                baseViewModel?.validateCity()
            }

            addressFormTvDistrict.doOnTextChanged { _, _, _, _ ->
                baseViewModel?.validateDistrict()
            }

            addressFormTvSubDistrict.doOnTextChanged { _, _, _, _ ->
                baseViewModel?.validateSubDistrict()
            }

            addressFormEtRtRw.addTextChangedListener(object : TextWatcher {
                private val SLASH_CHAR = '/'
                var deleteAction = false
                private var lastPosition = 0
                private var currentPosition = 0

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {

                }

                override fun onTextChanged(
                    s: CharSequence?,
                    start: Int,
                    before: Int,
                    count: Int
                ) {
                    lastPosition = start
                    deleteAction = count == 0
                }

                override fun afterTextChanged(s: Editable?) {
                    try {
                        val cleanText = s.toString()
                            .replace("[/]+".toRegex(), "") //replace / from text 001/002 = 001002
                        val formatTextWithSlash =
                            cleanText.replace(
                                ".{3}(?!$)".toRegex(),
                                "$0$SLASH_CHAR"
                            ) //grouping text and add slash after group

                        if (s?.isNotBlank() == true) {
                            if (deleteAction) {
                                currentPosition = if (s[lastPosition - 1] == SLASH_CHAR) {
                                    lastPosition - 1
                                } else {
                                    lastPosition
                                }
                            } else {
                                if (s[lastPosition] == SLASH_CHAR) {
                                    s.delete(lastPosition - 1, lastPosition)
                                }

                                currentPosition =
                                    if (formatTextWithSlash[lastPosition] == SLASH_CHAR) {
                                        lastPosition + 2
                                    } else {
                                        lastPosition + 1
                                    }
                            }
                        }
                        addressFormEtRtRw.removeTextChangedListener(this)
                        addressFormEtRtRw.setText(formatTextWithSlash)
                        if (!TextUtils.isEmpty(formatTextWithSlash)) {
                            addressFormEtRtRw.setSelection(currentPosition)
                        }
                        addressFormEtRtRw.addTextChangedListener(this)
                        baseViewModel?.validateRtRw(formatTextWithSlash)
                    } catch (e: Exception) {
                        Timber.e(e)
                    }
                }
            })
        }

        baseViewModel?.checkValidationForm()

        // set value address after add all listener, so all form will be validated
        userAddress?.let {
            baseViewModel?.run {
                countrySelected = it.country
                provinceSelected = it.province
                citySelected = it.city
                districtSelected = it.district
                subDistrictSelected = it.subDistrict
            }
            viewBinding?.run {
                addressFormEtAddress.setText(it.street)
                addressFormTvCountry.text = it.country?.name
                addressFormTvProvince.text = it.province?.name
                addressFormTvCity.text = it.city?.name
                addressFormTvDistrict.text = it.district?.name
                addressFormTvSubDistrict.text = it.subDistrict?.name
                addressFormEtRtRw.setText(it.rtRw)
            }
        }
    }

    override fun observeViewModel(viewModel: AddressFormVM) {
        observe(viewModel.isLoadingLiveData, ::handleLoading)
        observe(viewModel.failureLiveData, ::handleFailure)
        observe(viewModel.countryEvent, ::openCountryBottomSheet)
        observe(viewModel.provinceEvent, ::openProvinceBottomSheet)
        observe(viewModel.cityEvent, ::openCityBottomSheet)
        observe(viewModel.districtEvent, ::openDistrictBottomSheet)
        observe(viewModel.subDistrictEvent, ::openSubDistrictBottomSheet)
        observe(viewModel.createUpdateEvent, { doAfterCreateOrUpdate() })
        observe(viewModel.showErrorRwEvent, ::showErrorRtRw)
        observe(viewModel.showErrorCountryEvent, ::showErrorCountry)
        observe(viewModel.showErrorProvinceEvent, ::showErrorProvince)
        observe(viewModel.showErrorCityEvent, ::showErrorCity)
        observe(viewModel.showErrorDistrictEvent, ::showErrorDistrict)
        observe(viewModel.showErrorSubDistrictEvent, ::showErrorSubDistrict)
        observe(viewModel.showErrorStreetEvent, ::showErrorStreet)
        observe(viewModel.isAllFormValid, ::doAfterValidation)
    }

    private fun showErrorCountry(showError: Boolean?) {
        viewBinding?.addressFormTvErrorCountry?.isVisible = showError == true
    }

    private fun showErrorProvince(showError: Boolean?) {
        viewBinding?.addressFormTvErrorProvince?.isVisible = showError == true
    }

    private fun showErrorCity(showError: Boolean?) {
        viewBinding?.addressFormTvErrorCity?.isVisible = showError == true
    }

    private fun showErrorDistrict(showError: Boolean?) {
        viewBinding?.addressFormTvErrorDistrict?.isVisible = showError == true
    }

    private fun showErrorSubDistrict(showError: Boolean?) {
        viewBinding?.addressFormTvErrorSubDistrict?.isVisible = showError == true
    }

    private fun showErrorRtRw(showError: Boolean?) {
        viewBinding?.addressFormTvErrorRtRw?.isVisible = showError == true
    }

    private fun showErrorStreet(showError: Boolean?) {
        viewBinding?.addressFormTvErrorAddress?.isVisible = showError == true
    }

    private fun doAfterValidation(isFormValid: Boolean?) {
        viewBinding?.addressFormBtnSave?.isEnabled = isFormValid == true
    }

    private fun doAfterCreateOrUpdate() {
        showSuccessSnackbar(
            if (formType == AddressFormType.CREATE) {
                getString(R.string.str_message_success_add_address)
            } else {
                getString(R.string.str_message_success_update_address)
            }
        ) {
            setResult(RESULT_OK)
            finish()
        }
    }

    private fun openCountryBottomSheet(countryList: List<Country>?) {
        openBottomSheet(
            BottomSheetType.COUNTRY,
            countryList,
            baseViewModel?.countrySelected
        )
    }

    private fun openProvinceBottomSheet(provinceList: List<Province>?) {
        if (!provinceList.isNullOrEmpty())
            openBottomSheet(
                BottomSheetType.PROVINCE,
                provinceList,
                baseViewModel?.provinceSelected
            )
    }

    private fun openCityBottomSheet(list: List<City>?) {
        if (!list.isNullOrEmpty())
            openBottomSheet(BottomSheetType.CITY, list, baseViewModel?.citySelected)
    }

    private fun openDistrictBottomSheet(list: List<District>?) {
        if (!list.isNullOrEmpty())
            openBottomSheet(
                BottomSheetType.DISTRICT,
                list,
                baseViewModel?.districtSelected
            )

    }

    private fun openSubDistrictBottomSheet(list: List<SubDistrict>?) {
        if (!list.isNullOrEmpty())
            openBottomSheet(
                BottomSheetType.SUB_DISTRICT,
                list,
                baseViewModel?.subDistrictSelected
            )
    }

    private fun openBottomSheet(
        bottomSheetType: BottomSheetType,
        listSelectedModel: List<SelectedModel>?,
        selectedModel: SelectedModel?
    ) {
        router.openBottomSheet(
            supportFragmentManager,
            bottomSheetType,
            selectedModel,
            listSelectedModel.orEmpty(),
            { handleCallbackBottomSheet(it) }
        )
    }

    private fun handleCallbackBottomSheet(pair: Pair<SelectedModel, BottomSheetType>) {
        viewBinding?.run {
            when (pair.second) {
                BottomSheetType.COUNTRY -> {
                    baseViewModel?.countrySelected = pair.first as Country
                    addressFormTvCountry.text = pair.first.getTitle()
                    clearProvince()
                }
                BottomSheetType.PROVINCE -> {
                    baseViewModel?.provinceSelected = pair.first as Province
                    addressFormTvProvince.text = pair.first.getTitle()
                    clearCity()
                }
                BottomSheetType.CITY -> {
                    baseViewModel?.citySelected = pair.first as City
                    addressFormTvCity.text = pair.first.getTitle()
                    clearDistrict()
                }
                BottomSheetType.DISTRICT -> {
                    baseViewModel?.districtSelected = pair.first as District
                    addressFormTvDistrict.text = pair.first.getTitle()
                    clearSubDistrict()
                }
                else -> {
                    baseViewModel?.subDistrictSelected = pair.first as SubDistrict
                    addressFormTvSubDistrict.text = pair.first.getTitle()
                }
            }
        }
    }

    private fun clearProvince() {
        baseViewModel?.clearProvince()
        viewBinding?.addressFormTvProvince?.text = null
        clearCity()
    }

    private fun clearCity() {
        baseViewModel?.clearCity()
        viewBinding?.addressFormTvCity?.text = null
        clearDistrict()
    }

    private fun clearDistrict() {
        baseViewModel?.clearDistrict()
        viewBinding?.addressFormTvDistrict?.text = null
        clearSubDistrict()
    }

    private fun clearSubDistrict() {
        baseViewModel?.clearSubDistrict()
        viewBinding?.addressFormTvSubDistrict?.text = null
    }

    override fun handleFailure(failure: Failure?) {
        when (failure) {
            is AddressFailure.ParameterRequireFailure -> {
                when (failure.bottomSheetType) {
                    BottomSheetType.COUNTRY -> {
                        showErrorSnackbar(getString(R.string.error_country_parameter_required))
                    }
                    BottomSheetType.PROVINCE -> {
                        showErrorSnackbar(getString(R.string.error_province_parameter_required))
                    }
                    BottomSheetType.CITY -> {
                        showErrorSnackbar(getString(R.string.error_city_parameter_required))
                    }
                    else -> {
                        showErrorSnackbar(getString(R.string.error_district_paramter_required))
                    }
                }
            }
            else -> super.handleFailure(failure)
        }
    }

    override fun bindViewModel(): AddressFormVM {
        return ViewModelProvider(this, viewModelFactory)[AddressFormVM::class.java]
    }

    companion object {
        private const val EXTRA_USER_ADDRESS = "AddressForm.UserAddress"
        private const val EXTRA_FORM_TYPE = "AddressForm.FormType"
        fun createIntent(
            caller: Context,
            addressFormType: AddressFormType,
            userAddress: UserAddress?
        ): Intent {
            return Intent(caller, AddressFormActivity::class.java).apply {
                putExtra(EXTRA_FORM_TYPE, addressFormType)
                putExtra(EXTRA_USER_ADDRESS, userAddress)
            }
        }
    }

}

enum class AddressFormType {
    CREATE, UPDATE
}
