package id.altea.care.view.family.formfamily

import id.altea.care.core.base.BaseViewModel
import id.altea.care.core.domain.model.Country
import id.altea.care.core.domain.model.FamilyContact
import id.altea.care.core.domain.model.FamilyInfo
import id.altea.care.core.domain.usecase.AddFamilyMemberUseCase
import id.altea.care.core.domain.usecase.GetContactFamilyUseCase
import id.altea.care.core.domain.usecase.GetCountriesUseCase
import id.altea.care.core.domain.usecase.UpdateFamilyUseCase
import id.altea.care.core.ext.TagInjection
import id.altea.care.core.ext.disposedBy
import id.altea.care.core.ext.getGeneralErrorServer
import id.altea.care.core.helper.NetworkHandler
import id.altea.care.core.helper.SingleLiveEvent
import id.altea.care.core.helper.util.ConstantQueryParam
import io.reactivex.Scheduler
import javax.inject.Inject
import javax.inject.Named

class FamilyVM @Inject constructor(
    @Named(TagInjection.UI_Scheduler) uiSchedulers: Scheduler,
    @Named(TagInjection.IO_Scheduler) ioScheduler: Scheduler,
    networkHandler: NetworkHandler,
    private val  getContactFamilyUseCase: GetContactFamilyUseCase,
    private val  getCountriesUseCase: GetCountriesUseCase,
    private val  addFamilyMemberUseCase: AddFamilyMemberUseCase,
    private val  updateFamilyUseCase: UpdateFamilyUseCase
) : BaseViewModel(uiSchedulers, ioScheduler, networkHandler) {

    var countrySelectedType : CountrySelectedType? =null
    val familyContact =  SingleLiveEvent<List<FamilyContact>>()
    val statusAddUpdateFamilyEvent = SingleLiveEvent<Boolean>()
    val countryEvent = SingleLiveEvent<List<Country>>()
    var birthCountrySelected: Country? = null
    var cardCountrySelected:Country? = null

    fun getFamilyContact(){
        if (familyContact.value.isNullOrEmpty()){
            getContact()
        }else{
            familyContact.value = familyContact.value
        }
    }

    fun getContact(){
        getContactFamilyUseCase.getFamilyCotact()
                .compose(applySchedulers())
                .doOnSubscribe { isLoadingLiveData.value = true  }
                .doOnTerminate { isLoadingLiveData.value = false }
                .subscribe({
                    familyContact.value = it
                },{
                    handleFailure(it.getGeneralErrorServer())
                }).disposedBy(disposable)
    }

    fun addFamilyMember(params : FamilyInfo){
        addFamilyMemberUseCase
                .addFamilyMember(params)
                .compose(applySchedulers())
                .doOnSubscribe { isLoadingLiveData.value = true }
                .doOnTerminate { isLoadingLiveData.value = false }
                .subscribe({
                    statusAddUpdateFamilyEvent.value = it
                },{
                    handleFailure(it.getGeneralErrorServer())
                }).disposedBy(disposable)
    }


    fun updateFamilyMember(params: FamilyInfo,patientId :String){
        updateFamilyUseCase
                .updateFamilyMember(params,patientId)
                .compose(applySchedulers())
                .doOnSubscribe { isLoadingLiveData.value = true }
                .doOnTerminate { isLoadingLiveData.value = false }
                .subscribe({
                    statusAddUpdateFamilyEvent.value = it
                },{
                    handleFailure(it.getGeneralErrorServer())
                }).disposedBy(disposable)
    }

    fun doClickCountry(countrySelectedType: CountrySelectedType) {
        this.countrySelectedType = countrySelectedType
        if (countryEvent.value.isNullOrEmpty()) {
            getCountry()
        } else {
            countryEvent.value = countryEvent.value
        }
    }

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

}