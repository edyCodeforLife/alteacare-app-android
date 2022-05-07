package id.altea.care.view.register.personalinfo

import id.altea.care.core.base.BaseViewModel
import id.altea.care.core.domain.model.Country
import id.altea.care.core.domain.usecase.GetCountriesUseCase
import id.altea.care.core.ext.TagInjection
import id.altea.care.core.ext.disposedBy
import id.altea.care.core.ext.getGeneralErrorServer
import id.altea.care.core.helper.NetworkHandler
import id.altea.care.core.helper.SingleLiveEvent
import id.altea.care.core.helper.util.ConstantQueryParam
import io.reactivex.Scheduler
import javax.inject.Inject
import javax.inject.Named

class RegisterPersonalInfoVM @Inject constructor(
    @Named(TagInjection.UI_Scheduler) uiSchedulers: Scheduler,
    @Named(TagInjection.IO_Scheduler) ioScheduler: Scheduler,
    networkHandler: NetworkHandler,
    private val getCountriesUseCase: GetCountriesUseCase
) : BaseViewModel(uiSchedulers, ioScheduler, networkHandler) {

    val registerEvent =  SingleLiveEvent<List<Country>>()

    fun getCountries(query: String){
        val queryParam = mapOf(Pair(ConstantQueryParam.QUERY_KEYWORD, query))
        getCountriesUseCase.getCountries(queryParam)
            .compose(applySchedulers())
            .doOnSubscribe { isLoadingLiveData.postValue( true) }
            .doOnTerminate { isLoadingLiveData.postValue( false) }
            .subscribe({
                registerEvent.value = it
            },{
                handleFailure(it.getGeneralErrorServer())
            }).disposedBy(disposable)

    }
}
