package id.altea.care.view.myconsultation.ongoing

import id.altea.care.core.base.BaseViewModel
import id.altea.care.core.domain.model.AppointmentData
import id.altea.care.core.domain.model.OperationalHourMA
import id.altea.care.core.domain.model.PatientFamily
import id.altea.care.core.domain.model.Profile
import id.altea.care.core.domain.usecase.GetAuthUseCase
import id.altea.care.core.domain.usecase.GetOnGoingAppointmentUseCase
import id.altea.care.core.domain.usecase.GetOperationalHourMaUseCase
import id.altea.care.core.domain.usecase.GetProfileCacheUseCase
import id.altea.care.core.exception.NotFoundFailure
import id.altea.care.core.ext.TagInjection
import id.altea.care.core.ext.disposedBy
import id.altea.care.core.ext.getGeneralErrorServer
import id.altea.care.core.helper.NetworkHandler
import id.altea.care.core.helper.SingleLiveEvent
import id.altea.care.core.helper.util.ConstantSortType
import io.reactivex.Scheduler
import io.reactivex.disposables.Disposable
import retrofit2.HttpException
import javax.inject.Inject
import javax.inject.Named

class OnGoingFragmentVM @Inject constructor(
    @Named(TagInjection.UI_Scheduler) uiSchedulers: Scheduler,
    @Named(TagInjection.IO_Scheduler) ioScheduler: Scheduler,
    networkHandler: NetworkHandler,
    private val getOnGoingUseCase: GetOnGoingAppointmentUseCase,
    private val authUseCase: GetAuthUseCase,
    private val getUserProfileCacheUseCase: GetProfileCacheUseCase,
    private val getOperationalHourMaUseCase: GetOperationalHourMaUseCase
) : BaseViewModel(uiSchedulers, ioScheduler, networkHandler) {

    var patientFamily: PatientFamily? = null
    private var startDate: String? = null
    private var endDate: String? = null
    private var singleDisposable: Disposable? = null

    val appointmentListLiveEvent = SingleLiveEvent<List<AppointmentData>>()
    val appointmentListLoadMoreEvent = SingleLiveEvent<List<AppointmentData>>()
    val isLoadingLoadMore = SingleLiveEvent<Boolean>()
    val operationalHourMA = SingleLiveEvent<OperationalHourMA>()

    fun getOnGoingSchedule(page: Int) {
        singleDisposable?.dispose()
        executeJob {
            val isFirstPage = page == 1
            singleDisposable = getOnGoingUseCase.doRequestOnGoingAppointment(
                GetOnGoingAppointmentUseCase.Param(
                    page,
                    startDate = this.startDate,
                    endDate = this.endDate,
                    sort = "createdAt",
                    sortType = ConstantSortType.SORT_ASC,
                    patientId = patientFamily?.id
                )
            )
                .compose(applySchedulers())
                .doOnSubscribe {
                    if (isFirstPage) {
                        isLoadingLiveData.postValue(true)
                    } else {
                        isLoadingLoadMore.postValue(true)
                    }
                }
                .doOnTerminate {
                    if (isFirstPage) {
                        isLoadingLiveData.postValue(false)
                    } else {
                        isLoadingLoadMore.postValue(false)
                    }
                }
                .subscribe({ appointmentDataList ->
                    if (isFirstPage) {
                        appointmentListLiveEvent.postValue(appointmentDataList)
                    } else {
                        appointmentListLoadMoreEvent.postValue(appointmentDataList)
                    }
                }, {
                    if (it is HttpException && it.code() == 404) {
                        if (isFirstPage) {
                            handleFailure(NotFoundFailure.DataNotExist())
                        } else {
                            handleFailure(NotFoundFailure.EmptyListLoadMore())
                        }
                    } else {
                        handleFailure(it.getGeneralErrorServer())
                    }
                })
        }
    }

    fun getRedirectUrl(redirectUrl: String?): String? {
        return redirectUrl?.replace(
            "{{REPLACE_THIS_TO_BEARER_USER}}",
            authUseCase.getToken()
        )
    }

    fun setFilterDate(startDate: String?, endDate: String?) {
        this.startDate = startDate
        this.endDate = endDate
    }

    fun setFilterPatient(patientFamily: PatientFamily?) {
        this.patientFamily = patientFamily
    }

    fun getOperationalHourMA() {
        executeJob {
            getOperationalHourMaUseCase
                .getOperationalHourMA()
                .compose(applySchedulers())
                .subscribe({ dataOperationalHour ->
                    operationalHourMA.value = dataOperationalHour
                }, { throwable ->
                    throwable.printStackTrace()
                    handleFailure(throwable.getGeneralErrorServer())
                })
                .disposedBy(disposable)
        }
    }
}
