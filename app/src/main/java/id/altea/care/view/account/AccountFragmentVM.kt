package id.altea.care.view.account

import com.moe.pushlibrary.MoEHelper
import id.altea.care.AlteaApps
import id.altea.care.core.base.BaseViewModel
import id.altea.care.core.domain.model.General
import id.altea.care.core.domain.model.Profile
import id.altea.care.core.domain.usecase.GetProfileUseCase
import id.altea.care.core.domain.usecase.LogoutUseCase
import id.altea.care.core.exception.Failure
import id.altea.care.core.ext.TagInjection
import id.altea.care.core.ext.disposedBy
import id.altea.care.core.ext.getGeneralErrorServer
import id.altea.care.core.helper.NetworkHandler
import id.altea.care.core.helper.SingleLiveEvent
import io.reactivex.Scheduler
import javax.inject.Inject
import javax.inject.Named

class AccountFragmentVM @Inject constructor(
    @Named(TagInjection.UI_Scheduler) uiSchedulers: Scheduler,
    @Named(TagInjection.IO_Scheduler) ioScheduler: Scheduler,
    networkHandler: NetworkHandler,
    private val logoutUseCase: LogoutUseCase,
    private val profileUseCase: GetProfileUseCase,
    private val application: AlteaApps
) : BaseViewModel(uiSchedulers, ioScheduler, networkHandler) {
    val loadingLottieEvent = SingleLiveEvent<Boolean>()
    val generalEvent = SingleLiveEvent<General>()
    val profile = SingleLiveEvent<Profile>()
    fun logout() {
        logoutUseCase.doLogout()
            .compose(applySchedulers())
            .doOnSubscribe { isLoadingLiveData.value = true }
            .doOnTerminate { isLoadingLiveData.value = false }
            .doAfterSuccess { MoEHelper.getInstance(application).logoutUser() }
            .subscribe({
                generalEvent.value = it
            }, {
                handleFailure(it.getGeneralErrorServer())
            }).disposedBy(disposable)
    }

    fun getProfile(isFromCache: Boolean) {
        isLoadingLiveData.value = true
        profileUseCase.getProfile(isFromCache)
            .compose(applySchedulers())
            .doOnSubscribe { loadingLottieEvent.value = true }
            .doAfterSuccess { loadingLottieEvent.value = false }
            .subscribe({ resultProfile ->
                if (resultProfile.status == true) {
                    profile.value = resultProfile.data ?: null
                    isLoadingLiveData.value = false
                }
            }, {
                handleFailure(Failure.ServerError(it.message ?: ""))
            }).disposedBy(disposable)
    }


}
