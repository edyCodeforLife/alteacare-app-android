package id.altea.care.view.consultation

import id.altea.care.core.base.BaseViewModel
import id.altea.care.core.domain.usecase.LogoutUseCase
import id.altea.care.core.ext.TagInjection
import id.altea.care.core.ext.disposedBy
import id.altea.care.core.ext.getGeneralErrorServer
import id.altea.care.core.helper.NetworkHandler
import id.altea.care.core.helper.SingleLiveEvent
import io.reactivex.Scheduler
import javax.inject.Inject
import javax.inject.Named
import id.altea.care.core.domain.model.*
import id.altea.care.core.domain.usecase.GetDoctorDetailUseCase
import id.altea.care.core.domain.usecase.GetFamilyDetailUseCase
import id.altea.care.core.mappers.Mapper.mapToConsultation

class ConsultationFragmentVM @Inject constructor(
    @Named(TagInjection.UI_Scheduler) uiSchedulers: Scheduler,
    @Named(TagInjection.IO_Scheduler) ioScheduler: Scheduler,
    networkHandler: NetworkHandler,
    private val getFamilyDetailUseCase: GetFamilyDetailUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val getDoctorDetailUseCase: GetDoctorDetailUseCase
) : BaseViewModel(uiSchedulers, ioScheduler, networkHandler) {

    val detailPatientEvent = SingleLiveEvent<DetailPatient>()
    val generalEvent = SingleLiveEvent<General>()
    val doctorDetailEvent = SingleLiveEvent<Consultation>()

    fun getFamilyDetail(patientId: String) {
        getFamilyDetailUseCase.getFamilyDetail(patientId)
            .compose(applySchedulers())
            .doOnSubscribe { isLoadingLiveData.value = true }
            .doOnTerminate { isLoadingLiveData.value = false }
            .subscribe({ dataPatient ->
                detailPatientEvent.value = dataPatient
            }, {throwable ->
                handleFailure(throwable.getGeneralErrorServer())
            }).disposedBy(disposable)
    }

    fun doLogout() {
        logoutUseCase.doLogout()
            .compose(applySchedulers())
            .doOnSubscribe { isLoadingLiveData.value = true }
            .doOnTerminate { isLoadingLiveData.value = false }
            .subscribe({ general ->
                generalEvent.value = general
            }, { throwable ->
                handleFailure(throwable.getGeneralErrorServer())
            }).disposedBy(disposable)
    }


    fun doRequestDoctorDetail(doctorId: String,doctorSchedule: DoctorSchedule) {
        executeJob {
            getDoctorDetailUseCase.doRequestDoctorDetail(doctorId)
                .compose(applySchedulers())
                .doOnSubscribe { isLoadingLiveData.value = true }
                .doOnTerminate { isLoadingLiveData.value = false }
                .subscribe({ dataDoctor ->
                    doctorDetailEvent.value = dataDoctor.mapToConsultation(doctorSchedule)
                }, { throwable ->
                    throwable.printStackTrace()
                    handleFailure(throwable.getGeneralErrorServer())
                })
                .disposedBy(disposable)
        }

    }

}
