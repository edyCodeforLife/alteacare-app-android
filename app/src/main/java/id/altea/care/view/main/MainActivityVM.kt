package id.altea.care.view.main

import id.altea.care.core.base.BaseViewModel
import id.altea.care.core.domain.model.MyConsultationDateEvent
import id.altea.care.core.domain.usecase.GetAuthUseCase
import id.altea.care.core.ext.TagInjection
import id.altea.care.core.helper.NetworkHandler
import id.altea.care.core.helper.RxBus
import id.altea.care.core.helper.SingleLiveEvent
import io.reactivex.Scheduler
import javax.inject.Named

class MainActivityVM @javax.inject.Inject constructor(
    @Named(TagInjection.UI_Scheduler) uiSchedulers: Scheduler,
    @Named(TagInjection.IO_Scheduler) ioScheduler: Scheduler,
    networkHandler: NetworkHandler,
    private val getAuthUseCase: GetAuthUseCase
) : BaseViewModel(uiSchedulers, ioScheduler, networkHandler) {


    fun isUserLoggedIn(): Boolean {
        return getAuthUseCase.getIsLoggedIn()
    }



}
