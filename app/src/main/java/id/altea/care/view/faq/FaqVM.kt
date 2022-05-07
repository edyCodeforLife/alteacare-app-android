package id.altea.care.view.faq

import id.altea.care.core.base.BaseViewModel
import id.altea.care.core.domain.model.Faq
import id.altea.care.core.domain.usecase.GetFaqUseCase
import id.altea.care.core.ext.TagInjection
import id.altea.care.core.ext.disposedBy
import id.altea.care.core.ext.getGeneralErrorServer
import id.altea.care.core.helper.NetworkHandler
import id.altea.care.core.helper.SingleLiveEvent
import io.reactivex.Scheduler
import javax.inject.Inject
import javax.inject.Named

class FaqVM @Inject constructor(
    @Named(TagInjection.UI_Scheduler) uiSchedulers: Scheduler,
    @Named(TagInjection.IO_Scheduler) ioScheduler: Scheduler,
    networkHandler: NetworkHandler,
    private val getFaqUseCase: GetFaqUseCase
) : BaseViewModel(uiSchedulers, ioScheduler, networkHandler) {
    val faqEvent = SingleLiveEvent<List<Faq>>()
    fun getFaq(){
        getFaqUseCase.getFaq()
            .compose(applySchedulers())
            .doOnSubscribe { isLoadingLiveData.value = true }
            .doOnTerminate { isLoadingLiveData.value = false }
            .subscribe({
              faqEvent.value = it
            },{
                handleFailure(it.getGeneralErrorServer())
            }).disposedBy(disposable)
    }
}