package id.altea.care.view.promotion.group

import id.altea.care.core.base.BaseViewModel
import id.altea.care.core.domain.model.PromotionListGroup
import id.altea.care.core.domain.usecase.PromotionListGroupUseCase
import id.altea.care.core.ext.TagInjection
import id.altea.care.core.ext.disposedBy
import id.altea.care.core.ext.getGeneralErrorServer
import id.altea.care.core.helper.NetworkHandler
import id.altea.care.core.helper.SingleLiveEvent
import io.reactivex.Scheduler
import javax.inject.Inject
import javax.inject.Named

class PromotionVM @Inject constructor(
    @Named(TagInjection.UI_Scheduler) uiSchedulers: Scheduler,
    @Named(TagInjection.IO_Scheduler) ioScheduler: Scheduler,
    networkHandler: NetworkHandler,
    private val promotionListGroupUseCase: PromotionListGroupUseCase
) : BaseViewModel(uiSchedulers, ioScheduler, networkHandler) {

    val promotionListGroupEvent = SingleLiveEvent<List<PromotionListGroup>>()

    fun getPromotionListGroup() {
        executeJob {
            promotionListGroupUseCase.getPromotionListGroup()
                .compose(applySchedulers())
                .doOnSubscribe { isLoadingLiveData.value = true }
                .doOnTerminate { isLoadingLiveData.value = false }
                .subscribe({ promotionListGroup ->
                    promotionListGroupEvent.value = promotionListGroup
                },{ throwable ->
                    throwable.printStackTrace()
                    handleFailure(throwable.getGeneralErrorServer())
                }).disposedBy(disposable)
        }
    }
}