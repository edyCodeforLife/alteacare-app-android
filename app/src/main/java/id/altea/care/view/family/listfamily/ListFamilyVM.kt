package id.altea.care.view.family.listfamily

import id.altea.care.core.base.BaseViewModel
import id.altea.care.core.domain.model.DetailPatient
import id.altea.care.core.domain.model.PatientFamily
import id.altea.care.core.domain.model.UserAddress
import id.altea.care.core.domain.usecase.*
import id.altea.care.core.ext.TagInjection
import id.altea.care.core.ext.disposedBy
import id.altea.care.core.ext.getGeneralErrorServer
import id.altea.care.core.helper.NetworkHandler
import id.altea.care.core.helper.SingleLiveEvent
import id.altea.care.view.consultation.item.PatientItem
import io.reactivex.Scheduler
import javax.inject.Inject
import javax.inject.Named

class ListFamilyVM @Inject constructor(
        @Named(TagInjection.UI_Scheduler) uiSchedulers: Scheduler,
        @Named(TagInjection.IO_Scheduler) ioScheduler: Scheduler,
        networkHandler: NetworkHandler,
        private val getFamilyMemberUseCase: GetFamilyMemberUseCase,
        private val setPrimaryFamilyUseCase: SetPrimaryFamilyUseCase
) : BaseViewModel(uiSchedulers, ioScheduler, networkHandler) {

    val patientListEvent = SingleLiveEvent<List<PatientFamily>>()
    val isNextPageAvailableEvent = SingleLiveEvent<Boolean>()
    val isLoadingFirstEvent = SingleLiveEvent<Boolean>()
    val isLoadingMoreEvent = SingleLiveEvent<Boolean>()
    val primaryFamilyEvent = SingleLiveEvent<Int>()

    fun getFamilyMember(page : Int){
        executeJob {
            val isFirstPage = page == 1
            getFamilyMemberUseCase.getFamilyMember(page)
                    .compose(applySchedulers())
                    .doOnSubscribe {
                        if (isFirstPage) {
                            isLoadingFirstEvent.value = true
                        } else {
                            isLoadingMoreEvent.value = true
                        }
                    }
                    .doOnTerminate {
                        if (isFirstPage) {
                            isLoadingFirstEvent.value = false
                        } else {
                            isLoadingMoreEvent.value = false
                        }
                    }
                    .subscribe({
                        patientListEvent.value = it.patients
                        val totalPage = it.meta.totalPage ?: -1
                        isNextPageAvailableEvent.value = totalPage > page
                    }, {
                        isNextPageAvailableEvent.value = false
                        handleFailure(it.getGeneralErrorServer())
                    }).disposedBy(disposable)
        }
    }


    fun setPrimaryFamily(patientId : String,adapterPosition : Int){
        executeJob {
            setPrimaryFamilyUseCase.setPrimaryFamily(patientId)
                    .subscribeOn(ioScheduler)
                    .observeOn(uiSchedulers)
                    .doOnSubscribe { isLoadingLiveData.value = true }
                    .doOnTerminate { isLoadingLiveData.value = false }
                    .subscribe({
                        primaryFamilyEvent.value = adapterPosition
                    },{
                        handleFailure(it.getGeneralErrorServer())
                    }).disposedBy(disposable)
        }
    }
}