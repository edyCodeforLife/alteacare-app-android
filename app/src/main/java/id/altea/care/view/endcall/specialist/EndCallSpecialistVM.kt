package id.altea.care.view.endcall.specialist

import id.altea.care.core.analytics.AnalyticManager
import id.altea.care.core.analytics.payload.DoctorCallPayloadBuilder
import id.altea.care.core.base.BaseViewModel
import id.altea.care.core.domain.model.ConsultationDetail
import id.altea.care.core.domain.model.ConsultationDoctor
import id.altea.care.core.domain.usecase.GetConsultationDetailUseCase
import id.altea.care.core.exception.NotFoundFailure
import id.altea.care.core.ext.TagInjection
import id.altea.care.core.ext.disposedBy
import id.altea.care.core.ext.getGeneralErrorServer
import id.altea.care.core.helper.NetworkHandler
import id.altea.care.core.helper.SingleLiveEvent
import io.reactivex.Scheduler
import retrofit2.HttpException
import javax.inject.Inject
import javax.inject.Named

class EndCallSpecialistVM @Inject constructor(
    @Named(TagInjection.UI_Scheduler) uiSchedulers: Scheduler,
    @Named(TagInjection.IO_Scheduler) ioScheduler: Scheduler,
    networkHandler: NetworkHandler,
    private val consultationDetailUseCase: GetConsultationDetailUseCase,
    private val analyticManager: AnalyticManager
) : BaseViewModel(uiSchedulers, ioScheduler, networkHandler) {
    val doctorLiveData = SingleLiveEvent<ConsultationDetail>()

    fun getConsultationDetail(consultationId: Int) {
        consultationDetailUseCase.doRequestConsultationDetaiL(consultationId)
            .compose(applySchedulers())
            .doOnSubscribe { isLoadingLiveData.value = true }
            .doOnTerminate { isLoadingLiveData.value = false }
            .subscribe({ consultationDetail ->
                doctorLiveData.value = consultationDetail
                sendEventDoctorCallToAnalytics(consultationDetail)
            }, {
                if (it is HttpException && it.code() == 404) {
                    handleFailure(NotFoundFailure.DataNotExist())
                } else {
                    handleFailure(it.getGeneralErrorServer())
                }
            }).disposedBy(disposable)
    }

    private fun sendEventDoctorCallToAnalytics(consultationDetail: ConsultationDetail) {
        analyticManager.trackingEventDoctorCall(
            DoctorCallPayloadBuilder(
                consultationDetail.doctorId,
                consultationDetail.doctorName,
                consultationDetail.doctorSpecialist,
                consultationDetail.hospitalId,
                consultationDetail.hospitalName,
                consultationDetail.schedule?.endDateTimeOfTeleconsultation,
            )
        )
    }

}