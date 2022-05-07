package id.altea.care.view.consultation.confirm

import com.moe.pushlibrary.MoEHelper
import id.altea.care.AlteaApps
import id.altea.care.core.base.BaseViewModel
import id.altea.care.core.data.request.AppointmentRequest
import id.altea.care.core.domain.model.Appointment
import id.altea.care.core.domain.model.General
import id.altea.care.core.domain.model.OperationalHourMA
import id.altea.care.core.domain.usecase.AppointmentUseCase
import id.altea.care.core.domain.usecase.GetOperationalHourMaUseCase
import id.altea.care.core.domain.usecase.LogoutUseCase
import id.altea.care.core.ext.TagInjection
import id.altea.care.core.ext.disposedBy
import id.altea.care.core.ext.getGeneralErrorServer
import id.altea.care.core.helper.NetworkHandler
import id.altea.care.core.helper.SingleLiveEvent
import io.reactivex.Scheduler
import javax.inject.Inject
import javax.inject.Named

class ConfirmationFragmentVM @Inject constructor(
    @Named(TagInjection.UI_Scheduler) uiSchedulers: Scheduler,
    @Named(TagInjection.IO_Scheduler) ioScheduler: Scheduler,
    networkHandler: NetworkHandler,
    private val appointmentUseCase: AppointmentUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val operationalHourMaUseCase: GetOperationalHourMaUseCase,
    private val application : AlteaApps
) : BaseViewModel(uiSchedulers, ioScheduler, networkHandler){

    val appointmentResult  =  SingleLiveEvent<Appointment>()
    val generalEvent = SingleLiveEvent<General>()
    val operationalHourMaEvent = SingleLiveEvent<Pair<OperationalHourMA,Appointment?>>()

    fun createAppointment(appointmentRequest: AppointmentRequest)  {
        executeJob {
            appointmentUseCase
                .createAppoiment(appointmentRequest)
                .subscribeOn(ioScheduler)
                .observeOn(uiSchedulers)
                .doOnSubscribe { isLoadingLiveData.value = true }
                .doOnTerminate { isLoadingLiveData.value = false }
                .subscribe({
                    appointmentResult.value  = it
                },{
                    handleFailure(it.getGeneralErrorServer())
                }).disposedBy(disposable)
        }
    }

    fun doLogout(){
        logoutUseCase.doLogout()
            .compose(applySchedulers())
            .doOnSubscribe { isLoadingLiveData.value = true }
            .doOnTerminate { isLoadingLiveData.value = false }
            .doAfterSuccess { MoEHelper.getInstance(application).logoutUser() }
            .subscribe({
                generalEvent.value = it
            },{
                handleFailure(it.getGeneralErrorServer())
            }).disposedBy(disposable)
    }

    fun getOperationalHourMA(appointmentRequest: Appointment?) {
        executeJob {
            operationalHourMaUseCase.getOperationalHourMA()
                .compose(applySchedulers())
                .doOnSubscribe { isLoadingLiveData.value = true }
                .doOnTerminate { isLoadingLiveData.value = false }
                .subscribe({ dataOperationalHourMA ->
                    operationalHourMaEvent.value = dataOperationalHourMA to appointmentRequest
                },{ throwable ->
                    throwable.printStackTrace()
                    handleFailure(throwable.getGeneralErrorServer())
                }).disposedBy(disposable)
        }
    }

}