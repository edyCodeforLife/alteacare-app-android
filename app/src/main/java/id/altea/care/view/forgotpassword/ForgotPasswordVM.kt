package id.altea.care.view.forgotpassword

import id.altea.care.core.base.BaseViewModel
import id.altea.care.core.domain.model.ForgotPassword
import id.altea.care.core.domain.usecase.ForgotPasswordUseCase
import id.altea.care.core.ext.TagInjection
import id.altea.care.core.ext.disposedBy
import id.altea.care.core.ext.getGeneralErrorServer
import id.altea.care.core.helper.NetworkHandler
import id.altea.care.core.helper.SingleLiveEvent
import io.reactivex.Scheduler
import javax.inject.Inject
import javax.inject.Named

/**
 * Created by trileksono on 10/03/21.
 */
class ForgotPasswordVM @Inject constructor(
    @Named(TagInjection.UI_Scheduler) uiSchedulers: Scheduler,
    @Named(TagInjection.IO_Scheduler) ioScheduler: Scheduler,
    networkHandler: NetworkHandler,
    private val forgotPasswordUseCase: ForgotPasswordUseCase
) : BaseViewModel(uiSchedulers, ioScheduler, networkHandler) {

    val resultRequestOtp = SingleLiveEvent<ForgotPassword>()

    fun requestEmailOtp(email: String) {
        executeJob {
            forgotPasswordUseCase.doRequestEmailOrSmsOtp(email)
                .compose(applySchedulers())
                .doOnSubscribe { isLoadingLiveData.value = true }
                .doOnTerminate { isLoadingLiveData.value = false }
                .subscribe(
                    { resultRequestOtp.value = it },
                    { handleFailure(it.getGeneralErrorServer()) }
                )
                .disposedBy(disposable)
        }
    }

}