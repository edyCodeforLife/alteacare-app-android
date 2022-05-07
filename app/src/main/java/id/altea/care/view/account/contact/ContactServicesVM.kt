package id.altea.care.view.account.contact

import id.altea.care.core.base.BaseViewModel
import id.altea.care.core.domain.model.Profile
import id.altea.care.core.domain.usecase.GetProfileUseCase
import id.altea.care.core.ext.TagInjection
import id.altea.care.core.ext.disposedBy
import id.altea.care.core.ext.getGeneralErrorServer
import id.altea.care.core.helper.NetworkHandler
import id.altea.care.core.helper.SingleLiveEvent
import io.reactivex.Scheduler
import javax.inject.Inject
import javax.inject.Named

class ContactServicesVM @Inject constructor(
    @Named(TagInjection.UI_Scheduler) uiSchedulers: Scheduler,
    @Named(TagInjection.IO_Scheduler) ioScheduler: Scheduler,
    networkHandler: NetworkHandler,
    private val getProfileUseCase: GetProfileUseCase
) : BaseViewModel(uiSchedulers, ioScheduler, networkHandler) {

    val profileEvent = SingleLiveEvent<Profile>()

    fun getProfile(isFormCache: Boolean) {
        executeJob {
            getProfileUseCase.getProfile(isFormCache)
                .compose(applySchedulers())
                .doOnSubscribe { isLoadingLiveData.value = true }
                .doAfterSuccess { isLoadingLiveData.value = false }
                .subscribe({ resultProfile ->
                    if (resultProfile.status == true) {
                        profileEvent.value = resultProfile.data ?: null
                        isLoadingLiveData.value = false
                    }
                }, { throwable ->
                    handleFailure(throwable.getGeneralErrorServer())
                }).disposedBy(disposable)
        }
    }

}