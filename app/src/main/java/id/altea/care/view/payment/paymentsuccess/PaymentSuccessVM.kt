package id.altea.care.view.payment.paymentsuccess

import id.altea.care.core.base.BaseViewModel
import id.altea.care.core.domain.model.ConsultationDetail
import id.altea.care.core.domain.usecase.GetConsultationDetailUseCase
import id.altea.care.core.ext.TagInjection
import id.altea.care.core.ext.disposedBy
import id.altea.care.core.helper.NetworkHandler
import id.altea.care.core.helper.SingleLiveEvent
import id.altea.care.view.consultation.failure.PaymentPageFailure
import io.reactivex.Scheduler
import javax.inject.Inject
import javax.inject.Named

class PaymentSuccessVM @Inject constructor(
    @Named(TagInjection.UI_Scheduler) uiSchedulers: Scheduler,
    @Named(TagInjection.IO_Scheduler) ioScheduler: Scheduler,
    networkHandler: NetworkHandler,
    private val getConsultationDetailUseCase: GetConsultationDetailUseCase,
) : BaseViewModel(uiSchedulers, ioScheduler, networkHandler) {

    val appointmentDetailEvent = SingleLiveEvent<ConsultationDetail>()

    fun doRequestDetailAppointment(appointmentId: Int) {
        getConsultationDetailUseCase.doRequestConsultationDetaiL(appointmentId)
            .compose(applySchedulers())
            .doOnSubscribe { isLoadingLiveData.value = true }
            .doOnTerminate { isLoadingLiveData.value = false }
            .subscribe(
                { appointmentDetailEvent.value = it },
                { handleFailure(PaymentPageFailure.DetailAppointmentUnload()) }
            )
            .disposedBy(disposable)
    }

}