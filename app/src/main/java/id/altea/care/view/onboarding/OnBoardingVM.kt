package id.altea.care.view.onboarding

import id.altea.care.core.base.BaseViewModel
import id.altea.care.core.domain.cache.OnboardingCache
import id.altea.care.core.domain.usecase.GetAppVersion
import id.altea.care.core.ext.TagInjection
import id.altea.care.core.helper.NetworkHandler
import io.reactivex.Scheduler
import javax.inject.Named

class OnBoardingVM @javax.inject.Inject constructor(
    @Named(TagInjection.UI_Scheduler) uiSchedulers: Scheduler,
    @Named(TagInjection.IO_Scheduler) ioScheduler: Scheduler,
    networkHandler: NetworkHandler,
    private val onboardingCache: OnboardingCache
) : BaseViewModel(uiSchedulers, ioScheduler, networkHandler) {

    fun setOnBoarding(status : Boolean){
        onboardingCache.setIsOnBoarding(status)
    }


}