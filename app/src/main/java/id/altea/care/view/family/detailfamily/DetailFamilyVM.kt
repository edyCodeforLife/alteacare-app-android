package id.altea.care.view.family.detailfamily

import id.altea.care.core.base.BaseViewModel
import id.altea.care.core.domain.model.DetailPatient
import id.altea.care.core.domain.model.Empty
import id.altea.care.core.domain.usecase.AddFamilyMemberExistingAccountUseCase
import id.altea.care.core.domain.usecase.DeleteFamilyUseCase
import id.altea.care.core.domain.usecase.GetFamilyDetailUseCase
import id.altea.care.core.domain.usecase.GetFamilyMemberUseCase
import id.altea.care.core.ext.TagInjection
import id.altea.care.core.ext.disposedBy
import id.altea.care.core.ext.getGeneralErrorServer
import id.altea.care.core.helper.NetworkHandler
import id.altea.care.core.helper.SingleLiveEvent
import io.reactivex.Scheduler
import javax.inject.Inject
import javax.inject.Named

class DetailFamilyVM @Inject constructor(
        @Named(TagInjection.UI_Scheduler) uiSchedulers: Scheduler,
        @Named(TagInjection.IO_Scheduler) ioScheduler: Scheduler,
        networkHandler: NetworkHandler,
        private val getFamilyDetailUseCase: GetFamilyDetailUseCase,
        private val deleteFamilyUseCase: DeleteFamilyUseCase
) : BaseViewModel(uiSchedulers, ioScheduler, networkHandler) {

    val detailPatientEvent = SingleLiveEvent<DetailPatient>()
    val statusDeleteEvent = SingleLiveEvent<Boolean>()

    fun getFamilyDetail(patientId : String){
        getFamilyDetailUseCase
                .getFamilyDetail(patientId)
                .compose(applySchedulers())
                .doOnSubscribe { isLoadingLiveData.value = true }
                .doOnTerminate { isLoadingLiveData.value = false }
                .subscribe({
                   detailPatientEvent.value = it
                },{
                    handleFailure(it.getGeneralErrorServer())
                }).disposedBy(disposable)
    }

    fun deleteFamilyDetail(patientId: String){
        deleteFamilyUseCase
                .deleteFamilyMember(patientId)
                .compose(applySchedulers())
                .doOnSubscribe { isLoadingLiveData.value = true }
                .doOnTerminate { isLoadingLiveData.value = false }
                .subscribe({
                    statusDeleteEvent.value = it
                },{
                    handleFailure(it.getGeneralErrorServer())
                }).disposedBy(disposable)
    }

}