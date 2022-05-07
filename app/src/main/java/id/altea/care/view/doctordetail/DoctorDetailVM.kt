package id.altea.care.view.doctordetail

import id.altea.care.core.analytics.AnalyticManager
import id.altea.care.core.analytics.payload.DoctorProfilePayloadBuilder
import id.altea.care.core.base.BaseViewModel
import id.altea.care.core.data.request.AppointmentRequest
import id.altea.care.core.data.request.ScheduleRequest
import id.altea.care.core.domain.model.*
import id.altea.care.core.domain.usecase.*
import id.altea.care.core.exception.NotFoundFailure
import id.altea.care.core.ext.TagInjection
import id.altea.care.core.ext.disposedBy
import id.altea.care.core.ext.getGeneralErrorServer
import id.altea.care.core.ext.getLongFromDate
import id.altea.care.core.helper.NetworkHandler
import id.altea.care.core.helper.SingleLiveEvent
import id.altea.care.view.doctordetail.failure.DoctorDetailFailure
import io.reactivex.Scheduler
import io.reactivex.disposables.Disposable
import retrofit2.HttpException
import timber.log.Timber
import java.util.*
import javax.inject.Inject
import javax.inject.Named

/**
 * Created by trileksono on 10/03/21.
 */
class DoctorDetailVM @Inject constructor(
    @Named(TagInjection.UI_Scheduler) uiSchedulers: Scheduler,
    @Named(TagInjection.IO_Scheduler) ioScheduler: Scheduler,
    networkHandler: NetworkHandler,
    private val getDoctorScheduleUseCase: GetDoctorScheduleUseCase,
    private val getAuthUseCase: GetAuthUseCase,
    private val termRefundUseCase: GetTermRefundUseCase,
    private val getDoctorDetailUseCase: GetDoctorDetailUseCase,
    private val userCache: GetProfileCacheUseCase,
    private val appointmentUseCase: AppointmentUseCase,
    private val analyticManager: AnalyticManager,
    private val getOperationalHourMaUseCase: GetOperationalHourMaUseCase
) : BaseViewModel(uiSchedulers, ioScheduler, networkHandler) {

    val isLoadingTimeSlot = SingleLiveEvent<Boolean>()
    val weekListLiveData = SingleLiveEvent<List<Long>>()
    val timeListLiveData = SingleLiveEvent<List<DoctorSchedule>>()
    val txtSetDateEvent = SingleLiveEvent<Boolean>()
    val isUserLoggedIn = SingleLiveEvent<Boolean>()
    val termRefundEvent = SingleLiveEvent<String>()
    val doctorEvent = SingleLiveEvent<Doctor>()
    val appointmentEvent = SingleLiveEvent<Triple<Appointment, String, OperationalHourMA?>>()
    val operationalHourMA = SingleLiveEvent<OperationalHourMA>()

    private var scheduleDisposable: Disposable? = null
    lateinit var profile: Profile

    init {
        getTermRefundCancel()
    }

    // this fun for get schedule doctor based on selected name of day
    fun getScheduleDoctor(doctorId: String, date: String) {
        scheduleDisposable?.dispose()
        scheduleDisposable = getDoctorScheduleUseCase.getDoctorSchedule(doctorId, date)
            .compose(applySchedulers())
            .doOnSubscribe { isLoadingTimeSlot.postValue(true) }
            .subscribe({
                isLoadingTimeSlot.value = false
                timeListLiveData.value = it
            }, {
                isLoadingTimeSlot.value = false
                if ((it is HttpException) && it.code() == 404) {
                    handleFailure(NotFoundFailure.DataNotExist())
                    return@subscribe
                }
                handleFailure(DoctorDetailFailure.ScheduleUnloadFailure)
            })
    }

    // this fun for get schedule doctor based on selected date (show on bottomsheet)
    fun getScheduleDoctorBottomSheet(doctorId: String, date: String) {
        executeJob {
            getDoctorScheduleUseCase.getDoctorSchedule(doctorId, date)
                .compose(applySchedulers())
                .doOnSubscribe { isLoadingLiveData.postValue(true) }
                .doOnTerminate { isLoadingLiveData.postValue(false) }
                .subscribe(
                    { timeListLiveData.value = it },
                    { handleFailure(it.getGeneralErrorServer()) }
                )
                .disposedBy(disposable)
        }
    }

    fun generateDayList(date: String) {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = date.getLongFromDate("yyyy-MM-dd")
        val days = mutableListOf(calendar.timeInMillis) // initialize this day)
        for (i in 0 until 6) {
            calendar.add(Calendar.DATE, 1)
            days.add(calendar.timeInMillis)
        }

        weekListLiveData.postValue(days)
    }

    fun changeStateTxtChangeDate(showDeleteText: Boolean) {
        txtSetDateEvent.value = showDeleteText
    }

    fun nextClicked() {
        isUserLoggedIn.value = getAuthUseCase.getIsLoggedIn()
    }

    private fun getTermRefundCancel() {
        executeJob {
            termRefundUseCase.getTermAndRefund()
                .compose(applySchedulers())
                .subscribe({
                    termRefundEvent.value = if (it.isNotEmpty()) it[0].text else ""
                }, {})
                .disposedBy(disposable)
        }
    }

    fun getDoctorDetail(doctorId: String) {
        executeJob {
            getUserProfileCache()
            getDoctorDetailUseCase.doRequestDoctorDetail(doctorId)
                .compose(applySchedulers())
                .subscribe(
                    { doctorEvent.value = it }, { Timber.e(it) })
                .disposedBy(disposable)
        }
    }

    private fun getUserProfileCache() {
        userCache.getUserProfile()
            .subscribe({ profile = it }, { Timber.e(it) })
            .disposedBy(disposable)
    }

    fun reCreateAppointment(
        doctorId: String,
        patientId: String,
        consultationMethod: String,
        schedule: ScheduleRequest,
        nextStep: String,
        reffAppointmentId: String,
        operationalHourMA: OperationalHourMA?
    ) {
        executeJob {
            appointmentUseCase.createAppoiment(
                AppointmentRequest(
                    doctorId,
                    patientId,
                    consultationMethod,
                    listOf(schedule),
                    nextStep,
                    reffAppointmentId
                )
            )
                .compose(applySchedulers())
                .doOnSubscribe { isLoadingLiveData.postValue(true) }
                .doOnTerminate { isLoadingLiveData.postValue(false) }
                .subscribe({
                    appointmentEvent.value = Triple(it , nextStep , operationalHourMA)
                }, {
                    handleFailure(it.getGeneralErrorServer())
                })
                .disposedBy(disposable)
        }
    }

    fun sendEventDoctorProfileToAnalytics(doctor: Doctor?){
        doctor?.let { mDoctor ->
            Timber.d("sendEventDoctorProfileToMoEngage execute -> $mDoctor")
            analyticManager.trackingEventDoctorProfile(
                DoctorProfilePayloadBuilder(
                    mDoctor.id,
                    mDoctor.name,
                    mDoctor.speciality,
                )
            )
        }
    }

    override fun onCleared() {
        scheduleDisposable?.dispose()
        super.onCleared()
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
