package id.altea.care.view.myconsultation.history.cancelfragment

import id.altea.care.core.domain.usecase.GetCancelAppointmentUseCase
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

class MyConsultationCancelFragmentVM @Inject constructor(
    @Named(TagInjection.UI_Scheduler) uiSchedulers: Scheduler,
    @Named(TagInjection.IO_Scheduler) ioScheduler: Scheduler,
    networkHandler: NetworkHandler,
    private val getCancelAppointmentUseCase: GetCancelAppointmentUseCase
) : BaseHistoryConsultationVM(uiSchedulers, ioScheduler, networkHandler) {

    override fun onFirstLaunch() {
        super.onFirstLaunch()
        fetchDataCancel()
    }

    override fun loadMoreData(page: Int) {
        super.loadMoreData(page)
        loadMoreCancel(page)
    }

    private fun fetchDataCancel() {
        executeJob {
            getCancelAppointmentUseCase.doRequestOnGoingAppointment(
                GetCancelAppointmentUseCase.Param(
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
                    })
                .disposedBy(disposable)
        }
    }

    private fun loadMoreCancel(page: Int) {
        executeJob {
            getCancelAppointmentUseCase.doRequestOnGoingAppointment(
                GetCancelAppointmentUseCase.Param(
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
                    }
                )
                .disposedBy(disposable)
        }
    }

}
