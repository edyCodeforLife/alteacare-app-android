package id.altea.care.view.payment.paymentinformation

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

class PaymentInfoVM @Inject constructor(
    @Named(TagInjection.UI_Scheduler) uiSchedulers: Scheduler,
    @Named(TagInjection.IO_Scheduler) ioScheduler: Scheduler,
    networkHandler: NetworkHandler,
    private val getConsultationDetailUseCase: GetConsultationDetailUseCase
) : BaseViewModel(uiSchedulers, ioScheduler, networkHandler) {

    val detailAppointmentEvent = SingleLiveEvent<ConsultationDetail>()

    fun getPaymentCheck(appointmentId: Int) {
        executeJob {
            getConsultationDetailUseCase.doRequestConsultationDetaiL(appointmentId)
                .compose(applySchedulers())
                .doOnSubscribe { isLoadingLiveData.postValue(true) }
                .doOnTerminate { isLoadingLiveData.postValue(false) }
                .subscribe({
                    detailAppointmentEvent.postValue(it)
                }, {
                    handleFailure(it.getGeneralErrorServer())
                })
                .disposedBy(disposable)
        }
    }

}