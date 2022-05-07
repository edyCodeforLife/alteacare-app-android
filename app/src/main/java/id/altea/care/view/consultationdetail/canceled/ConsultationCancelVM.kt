package id.altea.care.view.consultationdetail.canceled

import id.altea.care.core.base.BaseViewModel
import id.altea.care.core.domain.model.ConsultationDetail
import id.altea.care.core.domain.usecase.GetConsultationDetailUseCase
import id.altea.care.core.ext.TagInjection
import id.altea.care.core.ext.disposedBy
import id.altea.care.core.ext.getGeneralErrorServer
import id.altea.care.core.helper.NetworkHandler
import id.altea.care.core.helper.SingleLiveEvent
import io.reactivex.Scheduler
import javax.inject.Inject
import javax.inject.Named

class ConsultationCancelVM @Inject constructor(
    @Named(TagInjection.UI_Scheduler) uiSchedulers: Scheduler,
    @Named(TagInjection.IO_Scheduler) ioScheduler: Scheduler,
    networkHandler: NetworkHandler,
    private val consultationDetailUseCase: GetConsultationDetailUseCase
) : BaseViewModel(uiSchedulers, ioScheduler, networkHandler) {

    val detailAppointmentEvent = SingleLiveEvent<ConsultationDetail>()

    fun getConsultationDetail(appointmentId: Int) {
        consultationDetailUseCase.doRequestConsultationDetaiL(appointmentId)
            .compose(applySchedulers())
            .doOnSubscribe { isLoadingLiveData.value = true }
            .doOnTerminate { isLoadingLiveData.value = false }
            .subscribe({ result ->
                detailAppointmentEvent.value = result
            }, {
                handleFailure(it.getGeneralErrorServer())
            }).disposedBy(disposable)
    }
}

