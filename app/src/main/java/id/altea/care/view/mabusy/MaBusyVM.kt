package id.altea.care.view.mabusy

import com.google.gson.Gson
import id.altea.care.core.analytics.AnalyticManager
import id.altea.care.core.base.BaseViewModel
import id.altea.care.core.data.response.GeneralErrorResponse
import id.altea.care.core.domain.usecase.CancelReasonOrderUseCase
import id.altea.care.core.domain.usecase.LoginUseCase
import id.altea.care.core.ext.TagInjection
import id.altea.care.core.ext.disposedBy
import id.altea.care.core.ext.getGeneralErrorServer
import id.altea.care.core.helper.NetworkHandler
import id.altea.care.core.helper.SingleLiveEvent
import io.reactivex.Scheduler
import retrofit2.HttpException
import javax.inject.Inject
import javax.inject.Named

class MaBusyVM @Inject constructor(
    @Named(TagInjection.UI_Scheduler) uiSchedulers: Scheduler,
    @Named(TagInjection.IO_Scheduler) ioScheduler: Scheduler,
    networkHandler: NetworkHandler,
    private val cancelReasonOrderUseCase: CancelReasonOrderUseCase
) : BaseViewModel(uiSchedulers, ioScheduler, networkHandler) {

    val resultEvent = SingleLiveEvent<Boolean>()

    fun setCancelReasonOrder(appointmentId: Int, note: String) {
        cancelReasonOrderUseCase
            .setCancelReasonOrder(appointmentId, note)
            .compose(applySchedulers())
            .doOnSubscribe { isLoadingLiveData.value = true }
            .doOnTerminate { isLoadingLiveData.value = false }
            .subscribe({
                resultEvent.value = it
            }, {
                if (it is HttpException && it.code() == 400) {
                    val response = Gson().fromJson(
                        it.response()?.errorBody()?.charStream(),
                        GeneralErrorResponse::class.java
                    )
                    resultEvent.value = response.status ?: false
                    return@subscribe
                }
                handleFailure(it.getGeneralErrorServer())
            }).disposedBy(disposable)
    }


}
