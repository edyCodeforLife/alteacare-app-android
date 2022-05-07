package id.altea.care.view.changepassword

import androidx.lifecycle.MutableLiveData
import id.altea.care.core.base.BaseViewModel
import id.altea.care.core.domain.usecase.ChangePasswordUseCase
import id.altea.care.core.domain.usecase.CheckPasswordUseCase
import id.altea.care.core.domain.usecase.GetAuthUseCase
import id.altea.care.core.ext.TagInjection
import id.altea.care.core.ext.disposedBy
import id.altea.care.core.ext.getGeneralErrorServer
import id.altea.care.core.helper.NetworkHandler
import id.altea.care.core.helper.SingleLiveEvent
import io.reactivex.Scheduler
import javax.inject.Inject
import javax.inject.Named

class ChangePasswordVM @Inject constructor(
    @Named(TagInjection.UI_Scheduler) uiSchedulers: Scheduler,
    @Named(TagInjection.IO_Scheduler) ioScheduler: Scheduler,
    networkHandler: NetworkHandler,
    private val checkPasswordUseCase : CheckPasswordUseCase,
    private val changePasswordUseCase: ChangePasswordUseCase,
    private val authUseCase: GetAuthUseCase
) : BaseViewModel(uiSchedulers, ioScheduler, networkHandler) {
    val resultCheckPasswordLiveData = SingleLiveEvent<Boolean>()

    fun checkPassword(password : String?){
        checkPasswordUseCase
            .checkPassword(password)
            .compose(applySchedulers())
            .doOnSubscribe { isLoadingLiveData.value = true }
            .doOnTerminate { isLoadingLiveData.value = false }
            .subscribe({
                resultCheckPasswordLiveData.value = it
            },{
                handleFailure(it.getGeneralErrorServer())
            }).disposedBy(disposable)
    }

}