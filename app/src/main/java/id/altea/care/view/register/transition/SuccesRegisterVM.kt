package id.altea.care.view.register.transition

import id.altea.care.core.analytics.AnalyticManager
import id.altea.care.core.analytics.payload.RegisterSuccessPayloadBuilder
import id.altea.care.core.base.BaseViewModel
import id.altea.care.core.domain.model.Profile
import id.altea.care.core.domain.usecase.GetProfileUseCase
import id.altea.care.core.ext.TagInjection
import id.altea.care.core.ext.disposedBy
import id.altea.care.core.ext.getGeneralErrorServer
import id.altea.care.core.helper.NetworkHandler
import io.reactivex.Scheduler
import javax.inject.Inject
import javax.inject.Named

class SuccesRegisterVM @Inject constructor(
    @Named(TagInjection.UI_Scheduler) uiSchedulers: Scheduler,
    @Named(TagInjection.IO_Scheduler) ioScheduler: Scheduler,
    networkHandler: NetworkHandler,
    private val profileUseCase: GetProfileUseCase,
    private val analyticManager: AnalyticManager
) : BaseViewModel(
    uiSchedulers,
    ioScheduler,
    networkHandler
) {

    fun getProfile(token: String?) {
        executeJob {
            profileUseCase.getProfileFromRegister(token)
                .compose(applySchedulers())
                .doOnSubscribe { isLoadingLiveData.value = true }
                .doAfterTerminate { isLoadingLiveData.value = false }
                .subscribe({ profileResult ->
                    sendEventRegistrationToAnalytics(profileResult.data)
                }, { handleFailure(it.getGeneralErrorServer()) })
                .disposedBy(disposable)
        }
    }

    private fun sendEventRegistrationToAnalytics(profile: Profile?) {
        analyticManager.trackingEventRegistration(
            RegisterSuccessPayloadBuilder(
                profile?.id,
                "${profile?.firstName} ${profile?.lastName}",
                profile?.userDetails?.gender,
                profile?.email,
                profile?.phone,
                profile?.userDetails?.birthDate
            )
        )
    }

}