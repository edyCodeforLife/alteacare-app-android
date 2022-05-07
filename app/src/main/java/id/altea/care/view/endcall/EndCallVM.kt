package id.altea.care.view.endcall

import id.altea.care.core.analytics.AnalyticManager
import id.altea.care.core.base.BaseViewModel
import id.altea.care.core.domain.model.ConsultationDetail
import id.altea.care.core.domain.usecase.GetConsultationDetailUseCase
import id.altea.care.core.ext.TagInjection
import id.altea.care.core.ext.disposedBy
import id.altea.care.core.ext.getGeneralErrorServer
import id.altea.care.core.helper.NetworkHandler
import id.altea.care.core.helper.SingleLiveEvent
import io.reactivex.Observable
import io.reactivex.Scheduler
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Named

class EndCallVM @Inject constructor(
    @Named(TagInjection.UI_Scheduler) uiSchedulers: Scheduler,
    @Named(TagInjection.IO_Scheduler) ioScheduler: Scheduler,
    networkHandler: NetworkHandler,
    private val getConsultationDetailUseCase: GetConsultationDetailUseCase,
    private val analyticManager: AnalyticManager
) : BaseViewModel(uiSchedulers, ioScheduler, networkHandler) {

    val detailAppointmentEvent = SingleLiveEvent<ConsultationDetail>()

    fun schedulerPaymentCheck(appointmentId: Int) {
        executeJob {
            getConsultationDetailUseCase.doRequestConsultationDetaiL(appointmentId)
                .compose(applySchedulers())
                .doOnSubscribe { isLoadingLiveData.postValue(true) }
                .doOnTerminate { isLoadingLiveData.postValue(false) }
                .subscribe({ consultationDetail ->
                    detailAppointmentEvent.value = consultationDetail
                }, {
                    handleFailure(it.getGeneralErrorServer())
                })
                .disposedBy(disposable)
        }
    }

    fun startScheduler(appointmentId: Int) {
        Observable.interval(15, TimeUnit.SECONDS)
            .subscribe { schedulerPaymentCheck(appointmentId) }
            .disposedBy(disposable)
    }

    fun endScheduler() {
        disposable.clear()
    }

}