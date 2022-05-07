package id.altea.care.view.myconsultation.history

import id.altea.care.core.base.BaseViewModel
import id.altea.care.core.domain.model.AppointmentData
import id.altea.care.core.domain.model.PatientFamily
import id.altea.care.core.helper.NetworkHandler
import id.altea.care.core.helper.SingleLiveEvent
import id.altea.care.core.helper.util.ConstantSortType
import io.reactivex.Scheduler

abstract class BaseHistoryConsultationVM constructor(
    uiSchedulers: Scheduler,
    ioScheduler: Scheduler,
    networkHandler: NetworkHandler,
) : BaseViewModel(uiSchedulers, ioScheduler, networkHandler) {

    var patientFamily: PatientFamily? = null
    protected var query: String? = null
    protected var defailtSortBy = "createdAt"
    var sortType: String = ConstantSortType.SORT_DESC

    val appointmentListEvent = SingleLiveEvent<List<AppointmentData>>()
    val appointmentListLoadMoreEvent = SingleLiveEvent<List<AppointmentData>>()
    val isLoadingLoadMore = SingleLiveEvent<Boolean>()

    open fun onFirstLaunch() {}

    open fun loadMoreData(page: Int) {}

    fun updateTextSearch(query: String?) {
        this.query = query
        onFirstLaunch()
    }

    fun changeSortType(it: String) {
        this.sortType = it
        onFirstLaunch()
    }

    fun doFilterPatient(patientFamily: PatientFamily?) {
        this.patientFamily = patientFamily
        onFirstLaunch()
    }

}
