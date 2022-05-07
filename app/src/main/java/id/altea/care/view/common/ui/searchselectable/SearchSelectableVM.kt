package id.altea.care.view.common.ui.searchselectable

import id.altea.care.core.base.BaseViewModel
import id.altea.care.core.domain.model.Country
import id.altea.care.core.domain.model.HospitalResult
import id.altea.care.core.domain.model.Specialization
import id.altea.care.core.domain.model.TypeMessage
import id.altea.care.core.domain.usecase.*
import id.altea.care.core.ext.TagInjection
import id.altea.care.core.ext.disposedBy
import id.altea.care.core.ext.getGeneralErrorServer
import id.altea.care.core.helper.NetworkHandler
import id.altea.care.core.helper.SingleLiveEvent
import id.altea.care.core.helper.util.ConstantQueryParam
import io.reactivex.Scheduler
import javax.inject.Inject
import javax.inject.Named

class SearchSelectableVM  @Inject constructor(
    @Named(TagInjection.UI_Scheduler) uiSchedulers: Scheduler,
    @Named(TagInjection.IO_Scheduler) ioScheduler: Scheduler,
    networkHandler: NetworkHandler,
    private val getSpecialistUseCase: GetSpecialistUseCase,
    private val getHospitalUseCase : GetHospitalUseCase,
    private val getCountriesUseCase: GetCountriesUseCase,
    private val getMessageTypeUseCase: GetMessageTypeUseCase
) : BaseViewModel(uiSchedulers, ioScheduler, networkHandler) {

    val specialistEvent = SingleLiveEvent<List<Specialization>>()
    val hospitalEvent = SingleLiveEvent<List<HospitalResult>>()
    val registerEvent = SingleLiveEvent<List<Country>>()
    val typeMessage = SingleLiveEvent<List<TypeMessage>>()


    fun getMessageType(){
        executeJob {
            getMessageTypeUseCase.getMessageType()
                .compose(applySchedulers())
                .doOnSubscribe { isLoadingLiveData.value = true }
                .doOnTerminate { isLoadingLiveData.value = false }
                .subscribe({
                    typeMessage.value = it
                },{
                    handleFailure(it.getGeneralErrorServer())
                }).disposedBy(disposable)
        }
    }

     fun getSpecialist() {
        getSpecialistUseCase.getSpecialist()
            .compose(applySchedulers())
            .doOnSubscribe { isLoadingLiveData.value = true }
            .doOnTerminate { isLoadingLiveData.value = false }
            .subscribe(
                { specialistEvent.value = it },
                { handleFailure(it.getGeneralErrorServer()) })
            .disposedBy(disposable)
    }

    fun searchSpecialist(query : String){
        val queryParam = mapOf(Pair(ConstantQueryParam.QUERY_SEARCH, query))
        getSpecialistUseCase.searchSpecialist(queryParam)
            .compose(applySchedulers())
            .doOnSubscribe { isLoadingLiveData.postValue(true) }
            .doOnTerminate { isLoadingLiveData.postValue(false) }
            .subscribe(
                { specialistEvent.postValue(it) },
                { handleFailure(it.getGeneralErrorServer()) })
            .disposedBy(disposable)

    }

    fun getCountries(query: String){
        val queryParam = mapOf(Pair(ConstantQueryParam.QUERY_KEYWORD, query))
        getCountriesUseCase.getCountries(queryParam)
            .compose(applySchedulers())
            .doOnSubscribe { isLoadingLiveData.postValue( true) }
            .doOnTerminate { isLoadingLiveData.postValue( false) }
            .subscribe({
                registerEvent.postValue(it)
            },{
                handleFailure(it.getGeneralErrorServer())
            }).disposedBy(disposable)

    }

    fun getHospital(){
        getHospitalUseCase.getHospital()
            .compose(applySchedulers())
            .doOnSubscribe { isLoadingLiveData.value = true }
            .doOnTerminate { isLoadingLiveData.value= false }
            .subscribe(
                { hospitalEvent.value = it },
                { handleFailure(it.getGeneralErrorServer()) })
            .disposedBy(disposable)
    }

    fun searchHospital(query: String){
        val queryParam = mapOf(Pair(ConstantQueryParam.QUERY_SEARCH, query))
        getHospitalUseCase.searchHospital(queryParam)
            .compose(applySchedulers())
            .doOnSubscribe { isLoadingLiveData.postValue(true)}
            .doOnTerminate { isLoadingLiveData.postValue(false) }
            .subscribe(
                { hospitalEvent.postValue(it) },
                { handleFailure(it.getGeneralErrorServer()) })
            .disposedBy(disposable)

    }

}