package id.altea.care.view.address.addressform

import id.altea.care.core.base.BaseViewModel
import id.altea.care.core.domain.model.*
import id.altea.care.core.domain.usecase.*
import id.altea.care.core.ext.TagInjection
import id.altea.care.core.ext.disposedBy
import id.altea.care.core.ext.getGeneralErrorServer
import id.altea.care.core.helper.NetworkHandler
import id.altea.care.core.helper.SingleLiveEvent
import id.altea.care.core.helper.util.ConstantQueryParam
import id.altea.care.view.address.addressform.bottomsheet.BottomSheetType
import id.altea.care.view.address.failure.AddressFailure
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.subjects.PublishSubject
import javax.inject.Inject
import javax.inject.Named

class AddressFormVM @Inject constructor(
    @Named(TagInjection.UI_Scheduler) uiSchedulers: Scheduler,
    @Named(TagInjection.IO_Scheduler) ioScheduler: Scheduler,
    networkHandler: NetworkHandler,
    private val getCountriesUseCase: GetCountriesUseCase,
    private val getProvinceUseCase: GetProvinceUseCase,
    private val getCityUseCase: GetCityUseCase,
    private val getDistrictUseCase: GetDistrictUseCase,
    private val getSubDistrictUseCase: GetSubDistrictUseCase,
    private val createAddressUseCase: CreateAddressUseCase,
    private val updateAddressUseCase: UpdateAddressUseCase
) : BaseViewModel(uiSchedulers, ioScheduler, networkHandler) {

    val countryEvent = SingleLiveEvent<List<Country>>()
    val provinceEvent = SingleLiveEvent<List<Province>>()
    val cityEvent = SingleLiveEvent<List<City>>()
    val districtEvent = SingleLiveEvent<List<District>>()
    val subDistrictEvent = SingleLiveEvent<List<SubDistrict>>()
    val createUpdateEvent = SingleLiveEvent<Empty>()
    val isAllFormValid = SingleLiveEvent<Boolean>()

    val showErrorRwEvent = SingleLiveEvent<Boolean>()
    val showErrorStreetEvent = SingleLiveEvent<Boolean>()
    val showErrorCountryEvent = SingleLiveEvent<Boolean>()
    val showErrorProvinceEvent = SingleLiveEvent<Boolean>()
    val showErrorCityEvent = SingleLiveEvent<Boolean>()
    val showErrorDistrictEvent = SingleLiveEvent<Boolean>()
    val showErrorSubDistrictEvent = SingleLiveEvent<Boolean>()


    var countrySelected: Country? = null
    var provinceSelected: Province? = null
    var citySelected: City? = null
    var districtSelected: District? = null
    var subDistrictSelected: SubDistrict? = null

    private val isStreetValidSubject = PublishSubject.create<Boolean>()
    private val isRtRwValidSubject = PublishSubject.create<Boolean>()
    private val isCountryValidSubject = PublishSubject.create<Boolean>()
    private val isProvinceValidSubject = PublishSubject.create<Boolean>()
    private val isCityValidSubject = PublishSubject.create<Boolean>()
    private val isDistrictValidSubject = PublishSubject.create<Boolean>()
    private val isSubDistrictValidSubject = PublishSubject.create<Boolean>()

    private val obsIsStreetValid: Observable<Boolean>
        get() = isStreetValidSubject.doOnNext { showErrorStreetEvent.postValue(!it) }

    private val obsIsRtRwValid: Observable<Boolean>
        get() = isRtRwValidSubject.doOnNext { showErrorRwEvent.postValue(!it) }

    private val obsIsCountryValid: Observable<Boolean>
        get() = isCountryValidSubject.doOnNext { showErrorCountryEvent.postValue(!it) }

    private val obsIsProvinceValid: Observable<Boolean>
        get() = isProvinceValidSubject.doOnNext { showErrorProvinceEvent.postValue(!it) }

    private val obsIsCityValid: Observable<Boolean>
        get() = isCityValidSubject.doOnNext { showErrorCityEvent.postValue(!it) }

    private val obsIsDistrictValid: Observable<Boolean>
        get() = isDistrictValidSubject.doOnNext { showErrorDistrictEvent.postValue(!it) }

    private val obsIsSubdistrictValid: Observable<Boolean>
        get() = isSubDistrictValidSubject.doOnNext { showErrorSubDistrictEvent.postValue(!it) }

    private fun getCountry() {
        executeJob {
            getCountriesUseCase.getCountries(mapOf(ConstantQueryParam.QUERY_LIMIT to "2000"))
                .compose(applySchedulers())
                .doOnSubscribe { isLoadingLiveData.value = true }
                .doOnTerminate { isLoadingLiveData.value = false }
                .subscribe(
                    { countryEvent.value = reArrangeIndonesia(it) },
                    { handleFailure(it.getGeneralErrorServer()) }
                )
                .disposedBy(disposable)
        }
    }

    private fun getProvince(countryId: String) {
        executeJob {
            getProvinceUseCase.getProvince(countryId)
                .compose(applySchedulers())
                .doOnSubscribe { isLoadingLiveData.value = true }
                .doOnTerminate { isLoadingLiveData.value = false }
                .subscribe(
                    { provinceEvent.value = it },
                    { handleFailure(it.getGeneralErrorServer()) }
                )
                .disposedBy(disposable)
        }
    }

    private fun getCity(cityId: String) {
        executeJob {
            getCityUseCase.getCity(cityId)
                .compose(applySchedulers())
                .doOnSubscribe { isLoadingLiveData.value = true }
                .doOnTerminate { isLoadingLiveData.value = false }
                .subscribe(
                    { cityEvent.value = it },
                    { handleFailure(it.getGeneralErrorServer()) }
                )
                .disposedBy(disposable)
        }
    }

    private fun getDistrict(cityId: String) {
        executeJob {
            getDistrictUseCase.getDistrict(cityId)
                .compose(applySchedulers())
                .doOnSubscribe { isLoadingLiveData.value = true }
                .doOnTerminate { isLoadingLiveData.value = false }
                .subscribe(
                    { districtEvent.value = it },
                    { handleFailure(it.getGeneralErrorServer()) }
                )
                .disposedBy(disposable)
        }
    }

    private fun getSubDistrict(districtId: String) {
        executeJob {
            getSubDistrictUseCase.getSubDistrict(districtId)
                .compose(applySchedulers())
                .doOnSubscribe { isLoadingLiveData.value = true }
                .doOnTerminate { isLoadingLiveData.value = false }
                .subscribe(
                    { subDistrictEvent.value = it },
                    { handleFailure(it.getGeneralErrorServer()) }
                )
                .disposedBy(disposable)
        }
    }

    fun createAddress(street: String, rtRw: String) {
        executeJob {
            createAddressUseCase.createAddress(
                street,
                countrySelected?.countryId.orEmpty(),
                provinceSelected?.id.orEmpty(),
                citySelected?.id.orEmpty(),
                districtSelected?.id.orEmpty(),
                subDistrictSelected?.id.orEmpty(),
                rtRw
            )
                .subscribeOn(ioScheduler)
                .observeOn(uiSchedulers)
                .doOnSubscribe { isLoadingLiveData.value = true }
                .doOnTerminate { isLoadingLiveData.value = false }
                .subscribe(
                    { createUpdateEvent.value = Empty() },
                    { handleFailure(it.getGeneralErrorServer()) }
                )
                .disposedBy(disposable)
        }
    }

    fun updateAddress(addressId: String, street: String, rtRw: String) {
        executeJob {
            updateAddressUseCase.updateAddress(
                addressId,
                street,
                countrySelected?.countryId.orEmpty(),
                provinceSelected?.id.orEmpty(),
                citySelected?.id.orEmpty(),
                districtSelected?.id.orEmpty(),
                subDistrictSelected?.id.orEmpty(),
                rtRw
            )
                .subscribeOn(ioScheduler)
                .observeOn(uiSchedulers)
                .doOnSubscribe { isLoadingLiveData.value = true }
                .doOnTerminate { isLoadingLiveData.value = false }
                .subscribe(
                    { createUpdateEvent.value = Empty() },
                    { handleFailure(it.getGeneralErrorServer()) }
                )
                .disposedBy(disposable)
        }
    }

    fun doClickCountry() {
        if (countryEvent.value.isNullOrEmpty()) {
            getCountry()
        } else {
            countryEvent.value = countryEvent.value
        }
    }

    fun doClickProvince() {
        if (countrySelected == null) {
            handleFailure(AddressFailure.ParameterRequireFailure(BottomSheetType.COUNTRY))
            return
        }
        if (provinceEvent.value.isNullOrEmpty()) {
            getProvince(countrySelected?.countryId!!)
        } else {
            provinceEvent.value = provinceEvent.value
        }
    }

    fun doClickCity() {
        if (provinceSelected == null) {
            handleFailure(AddressFailure.ParameterRequireFailure(BottomSheetType.PROVINCE))
            return
        }
        if (cityEvent.value.isNullOrEmpty()) {
            getCity(provinceSelected?.id!!)
        } else {
            cityEvent.value = cityEvent.value
        }
    }

    fun doClickDistrict() {
        if (citySelected == null) {
            handleFailure(AddressFailure.ParameterRequireFailure(BottomSheetType.CITY))
            return
        }
        if (districtEvent.value.isNullOrEmpty()) {
            getDistrict(citySelected?.id!!)
        } else {
            districtEvent.value = districtEvent.value
        }
    }

    fun doClickSubDistrict() {
        if (districtSelected == null) {
            handleFailure(AddressFailure.ParameterRequireFailure(BottomSheetType.DISTRICT))
            return
        }
        if (subDistrictEvent.value.isNullOrEmpty()) {
            getSubDistrict(districtSelected?.id!!)
        } else {
            subDistrictEvent.value = subDistrictEvent.value
        }
    }

    fun clearProvince() {
        provinceSelected = null
        provinceEvent.value = emptyList()
    }

    fun clearCity() {
        citySelected = null
        cityEvent.value = emptyList()
    }

    fun clearDistrict() {
        districtSelected = null
        districtEvent.value = emptyList()
    }

    fun clearSubDistrict() {
        subDistrictSelected = null
        subDistrictEvent.value = emptyList()
    }

    fun reArrangeIndonesia(data: List<Country>): List<Country> {
        var index = 0
        for (i in data.indices) {
            if (data[i].name == "Indonesia") {
                index = i
                break
            }
        }
        return if (index > 0) {
            val copy = ArrayList<Country>(data.size)
            copy.add(0, data[index])
            copy.addAll(1, data)
            copy.removeAt(index + 1)
            copy
        } else {
            data
        }
    }

    fun validateStreet(street: String) {
        isStreetValidSubject.onNext(street.isNotBlank())
    }

    fun validateRtRw(rtRw: String) {
        isRtRwValidSubject.onNext(rtRw.isNotBlank() && rtRw.length == 7)
    }

    fun validateCountry() {
        isCountryValidSubject.onNext(countrySelected != null)
    }

    fun validateProvince() {
        isProvinceValidSubject.onNext(provinceSelected != null)
    }

    fun validateCity() {
        isCityValidSubject.onNext(citySelected != null)
    }

    fun validateDistrict() {
        isDistrictValidSubject.onNext(districtSelected != null)
    }

    fun validateSubDistrict() {
        isSubDistrictValidSubject.onNext(subDistrictSelected != null)
    }

    fun checkValidationForm() {
        val listObs = listOf(
            obsIsStreetValid,
            obsIsCountryValid,
            obsIsProvinceValid,
            obsIsCityValid,
            obsIsDistrictValid,
            obsIsSubdistrictValid,
            obsIsRtRwValid
        )
        Observable.combineLatest(listObs) { booleans ->
            booleans.none { !(it as Boolean) }
        }
            .subscribe { isAllFormValid.postValue(it) }
            .disposedBy(disposable)
    }

}
