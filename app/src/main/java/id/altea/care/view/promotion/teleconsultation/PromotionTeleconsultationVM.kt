package id.altea.care.view.promotion.teleconsultation

import id.altea.care.core.base.BaseViewModel
import id.altea.care.core.domain.model.PromotionList
import id.altea.care.core.domain.model.PromotionListGroup
import id.altea.care.core.domain.usecase.GetPromotionListUseCase
import id.altea.care.core.domain.usecase.PromotionListGroupUseCase
import id.altea.care.core.ext.TagInjection
import id.altea.care.core.ext.disposedBy
import id.altea.care.core.ext.getGeneralErrorServer
import id.altea.care.core.helper.NetworkHandler
import id.altea.care.core.helper.SingleLiveEvent
import id.altea.care.core.helper.util.ConstantQueryParam
import io.reactivex.Scheduler
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Named

class PromotionTeleconsultationVM @Inject constructor(
    @Named(TagInjection.UI_Scheduler) uiSchedulers: Scheduler,
    @Named(TagInjection.IO_Scheduler) ioScheduler: Scheduler,
    networkHandler: NetworkHandler,
    private val promotionListUseCase: GetPromotionListUseCase
) : BaseViewModel(uiSchedulers, ioScheduler, networkHandler) {

    val promotionListEvent = SingleLiveEvent<List<PromotionList>>()
    val isNextPageAvailableEvent = SingleLiveEvent<Boolean>()
    val isLoadingMoreEvent = SingleLiveEvent<Boolean>()
    private val bodyParam = HashMap<String, Any>()


    fun onFirstLaunch(promotionType : String){
        bodyParam.apply {
            put(ConstantQueryParam.QUERY_PROMOTION_TYPE,promotionType)
        }
    }

    fun getPromotionList(page : Int) {
        val isFirstPage = page == 1
        executeJob {
            promotionListUseCase.getPromotionList(page,bodyParam)
                .retryWhen { it.take(2).delay(1, TimeUnit.SECONDS) }
                .compose(applySchedulers())
                .doOnSubscribe {
                    if (isFirstPage) {
                        isLoadingLiveData.value = true
                    }else{
                        isLoadingMoreEvent.value = true
                    }
                }
                .doOnTerminate {
                    if (isFirstPage) {
                        isLoadingLiveData.value = false
                    }else{
                        isLoadingMoreEvent.value = false
                    }
                }
                .subscribe({ dataPromotion ->
                    promotionListEvent.value = dataPromotion
                    isNextPageAvailableEvent.value = dataPromotion.isNotEmpty()
                }, { throwable ->
                    isNextPageAvailableEvent.value = false
                    throwable.printStackTrace()
                    handleFailure(throwable.getGeneralErrorServer())
                }).disposedBy(disposable)
        }
    }


}