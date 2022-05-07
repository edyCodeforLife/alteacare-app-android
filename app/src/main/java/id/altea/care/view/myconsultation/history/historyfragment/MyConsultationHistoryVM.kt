package id.altea.care.view.myconsultation.history.historyfragment

import id.altea.care.core.domain.usecase.GetHistoryAppointmentUseCase
import id.altea.care.core.exception.NotFoundFailure
import id.altea.care.core.ext.TagInjection
import id.altea.care.core.ext.disposedBy
import id.altea.care.core.ext.getGeneralErrorServer
import id.altea.care.core.helper.NetworkHandler
import id.altea.care.view.myconsultation.history.BaseHistoryConsultationVM
import io.reactivex.Scheduler
import retrofit2.HttpException
import javax.inject.Inject
import javax.inject.Named

class MyConsultationHistoryVM @Inject constructor(
    @Named(TagInjection.UI_Scheduler) uiSchedulers: Scheduler,
    @Named(TagInjection.IO_Scheduler) ioScheduler: Scheduler,
    networkHandler: NetworkHandler,
    private val getHistoryAppointmentUseCase: GetHistoryAppointmentUseCase
) : BaseHistoryConsultationVM(uiSchedulers, ioScheduler, networkHandler) {

    override fun onFirstLaunch() {
        super.onFirstLaunch()
        fetchDataHistory()
    }

    override fun loadMoreData(page: Int) {
        loadMoreHistory(page)
    }

    private fun fetchDataHistory() {
        executeJob {
            getHistoryAppointmentUseCase.doRequestOnGoingAppointment(
                GetHistoryAppointmentUseCase.Param(
                    1,
                    sortType = this.sortType,
                    sort = defailtSortBy,
                    keyword = this.query,
                    patientId = this.patientFamily?.id
                )
            )
                .compose(applySchedulers())
                .doOnSubscribe { isLoadingLiveData.postValue(true) }
                .doOnTerminate { isLoadingLiveData.postValue(false) }
                .subscribe(
                    { appointmentListEvent.value = it },
                    {
                        if (it is HttpException && it.code() == 404) {
                            handleFailure(NotFoundFailure.DataNotExist())
                        } else {
                            handleFailure(it.getGeneralErrorServer())
                        }
                    }
                )
                .disposedBy(disposable)
        }
    }

    private fun loadMoreHistory(page: Int) {
        executeJob {
            getHistoryAppointmentUseCase.doRequestOnGoingAppointment(
                GetHistoryAppointmentUseCase.Param(
                    page,
                    keyword = query,
                    sort = defailtSortBy,
                    sortType = sortType,
                    patientId = this.patientFamily?.id
                )
            )
                .compose(applySchedulers())
                .doOnSubscribe { isLoadingLoadMore.postValue(true) }
                .doOnTerminate { isLoadingLoadMore.postValue(false) }
                .subscribe(
                    { appointmentListLoadMoreEvent.value = it },
                    {
                        if (it is HttpException && it.code() == 404) {
                            handleFailure(NotFoundFailure.EmptyListLoadMore())
                        } else {
                            handleFailure(it.getGeneralErrorServer())
                        }
                    })
                .disposedBy(disposable)
        }
    }

}
