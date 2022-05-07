package id.altea.care.view.register.contactinfo

import id.altea.care.core.base.BaseViewModel
import id.altea.care.core.data.request.AddCheckUserRequest
import id.altea.care.core.domain.model.*
import id.altea.care.core.domain.usecase.*
import id.altea.care.core.ext.TagInjection
import id.altea.care.core.ext.disposedBy
import id.altea.care.core.ext.getGeneralErrorServer
import id.altea.care.core.helper.NetworkHandler
import id.altea.care.core.helper.SingleLiveEvent
import io.reactivex.Scheduler
import javax.inject.Inject
import javax.inject.Named

class RegisterContactVM @Inject constructor(
        @Named(TagInjection.UI_Scheduler) uiSchedulers: Scheduler,
        @Named(TagInjection.IO_Scheduler) ioScheduler: Scheduler,
        networkHandler: NetworkHandler,
        private val  addFamilyMemberUseCase: AddFamilyMemberUseCase,
        private val addFamilyMemberExistingAccountUseCase: AddFamilyMemberExistingAccountUseCase,
        private val checkUserUseCase: CheckUserUseCase
) : BaseViewModel(uiSchedulers, ioScheduler, networkHandler) {

    val statusAddUpdateFamilyEvent = SingleLiveEvent<Boolean>()
    val addFamilyMemberExistingEvent = SingleLiveEvent<Empty>()
    val checkUserEvent = SingleLiveEvent<CheckUser>()

    fun addFamilyExistingAccountMember(patientId: String,email : String,phone : String) {
        executeJob {
            addFamilyMemberExistingAccountUseCase
                .addFamilyMemberExistingAccount(patientId, email, phone)
                .subscribeOn(ioScheduler)
                .observeOn(uiSchedulers)
                .doOnSubscribe { isLoadingLiveData.value = true }
                .doOnTerminate { isLoadingLiveData.value = false }
                .subscribe(
                    { addFamilyMemberExistingEvent.value = Empty() },
                    { handleFailure(it.getGeneralErrorServer()) }
                ).disposedBy(disposable)
        }
    }
    
    fun addFamilyRegisterMemberAccount(params : FamilyInfo){
        addFamilyMemberUseCase
            .addMemberFamilyRegisterAccount(params)
            .compose(applySchedulers())
            .doOnSubscribe { isLoadingLiveData.value = true }
            .doOnTerminate { isLoadingLiveData.value = false }
            .subscribe({
                statusAddUpdateFamilyEvent.value = it
            },{
                handleFailure(it.getGeneralErrorServer())
            }).disposedBy(disposable)
    }

    fun checkUser(params: AddCheckUserRequest) {
        checkUserUseCase
            .checkUser(params)
            .compose(applySchedulers())
            .doOnSubscribe { isLoadingLiveData.value = true }
            .doOnTerminate { isLoadingLiveData.value = false }
            .subscribe({
                checkUserEvent.value = it
            },{
                handleFailure(it.getGeneralErrorServer())
            }).disposedBy(disposable)
    }

}