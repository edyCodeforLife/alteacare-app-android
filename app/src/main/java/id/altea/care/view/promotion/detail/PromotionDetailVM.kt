package id.altea.care.view.promotion.detail

import id.altea.care.core.base.BaseViewModel
import id.altea.care.core.domain.model.PromotionDetail
import id.altea.care.core.domain.usecase.GetPromotionDetailUseCase
import id.altea.care.core.domain.usecase.PromotionListGroupUseCase
import id.altea.care.core.ext.TagInjection
import id.altea.care.core.ext.disposedBy
import id.altea.care.core.ext.getGeneralErrorServer
import id.altea.care.core.helper.NetworkHandler
import id.altea.care.core.helper.SingleLiveEvent
import io.reactivex.Scheduler
import javax.inject.Inject
import javax.inject.Named

class PromotionDetailVM @Inject constructor(
    @Named(TagInjection.UI_Scheduler) uiSchedulers: Scheduler,
    @Named(TagInjection.IO_Scheduler) ioScheduler: Scheduler,
    networkHandler: NetworkHandler,
    private val getPromotionDetailUseCase: GetPromotionDetailUseCase
) : BaseViewModel(uiSchedulers, ioScheduler, networkHandler) {

    val promotionDetailEvent = SingleLiveEvent<PromotionDetail>()

    fun getPromotionDetail(id : Int) {
        getPromotionDetailUseCase
            .getPromotionDetail(id)
            .compose(applySchedulers())
            .doOnSubscribe { isLoadingLiveData.value = true }
            .doOnTerminate { isLoadingLiveData.value = false }
            .subscribe({ promotionDetail ->
                promotionDetailEvent.value = promotionDetail
            },{
                handleFailure(it.getGeneralErrorServer())
            }).disposedBy(disposable)
    }

}