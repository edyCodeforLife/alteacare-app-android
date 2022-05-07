package id.altea.care.view.splashscreen

import id.altea.care.core.base.BaseViewModel
import id.altea.care.core.domain.cache.OnboardingCache
import id.altea.care.core.domain.model.CheckApp
import id.altea.care.core.domain.usecase.GetAppVersion
import id.altea.care.core.ext.TagInjection
import id.altea.care.core.ext.disposedBy
import id.altea.care.core.ext.getGeneralErrorServer
import id.altea.care.core.helper.NetworkHandler
import id.altea.care.core.helper.SingleLiveEvent
import io.reactivex.Scheduler
import javax.inject.Named

class SplashVM @javax.inject.Inject constructor(
    @Named(TagInjection.UI_Scheduler) uiSchedulers: Scheduler,
    @Named(TagInjection.IO_Scheduler) ioScheduler: Scheduler,
    networkHandler: NetworkHandler,
    val getAppVersion: GetAppVersion,
    private val onboardingCache: OnboardingCache
) : BaseViewModel(uiSchedulers, ioScheduler, networkHandler) {

    val appVersionEvent = SingleLiveEvent<CheckApp>()

    fun getApplicationVersion(){
       executeJob {
           getAppVersion
                .getAppVersion()
                .compose(applySchedulers())
                .subscribe({
                  appVersionEvent.value = it
                },{
                    handleFailure(it.getGeneralErrorServer())
                }).disposedBy(disposable)
       }
    }
}
