package id.altea.care.view.otpmail.changemail

import id.altea.care.core.base.BaseViewModel
import id.altea.care.core.domain.model.Empty
import id.altea.care.core.domain.usecase.RegisterChangeEmailUseCase
import id.altea.care.core.ext.TagInjection
import id.altea.care.core.ext.disposedBy
import id.altea.care.core.ext.getGeneralErrorServer
import id.altea.care.core.helper.NetworkHandler
import id.altea.care.core.helper.SingleLiveEvent
import io.reactivex.Scheduler
import javax.inject.Inject
import javax.inject.Named

class ChangeEmailVM @Inject constructor(
    @Named(TagInjection.UI_Scheduler) uiSchedulers: Scheduler,
    @Named(TagInjection.IO_Scheduler) ioScheduler: Scheduler,
    networkHandler: NetworkHandler,
    private val changeEmailUseCase: RegisterChangeEmailUseCase
) : BaseViewModel(uiSchedulers, ioScheduler, networkHandler) {

    val changeEmailState = SingleLiveEvent<Empty>()

    fun doChangeEmail(email: String, token: String) {
        changeEmailUseCase.doChangeEmailRegister(email, token)
            .compose(applySchedulers())
            .doOnSubscribe { isLoadingLiveData.value = true }
            .doAfterTerminate { isLoadingLiveData.value = false }
            .subscribe(
                { changeEmailState.value = Empty() },
                { handleFailure(it.getGeneralErrorServer()) })
            .disposedBy(disposable)
    }

}