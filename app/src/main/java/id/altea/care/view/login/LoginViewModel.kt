package id.altea.care.view.login

import com.google.gson.Gson
import id.altea.care.core.analytics.AnalyticManager
import id.altea.care.core.analytics.payload.LoginSuccessPayloadBuilder
import id.altea.care.core.base.BaseViewModel
import id.altea.care.core.domain.model.Profile
import id.altea.care.core.domain.usecase.LoginUseCase
import id.altea.care.core.exception.Failure
import id.altea.care.core.exception.UndefinedUsernameType
import id.altea.care.core.ext.TagInjection
import id.altea.care.core.ext.disposedBy
import id.altea.care.core.ext.getGeneralErrorServer
import id.altea.care.core.helper.NetworkHandler
import id.altea.care.core.helper.SingleLiveEvent
import id.altea.care.view.login.model.LoginHadleModel
import io.reactivex.Scheduler
import org.json.JSONObject
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Named

/**
 * Created by trileksono on 02/03/21.
 */
class LoginViewModel @Inject constructor(
    @Named(TagInjection.UI_Scheduler) uiSchedulers: Scheduler,
    @Named(TagInjection.IO_Scheduler) ioScheduler: Scheduler,
    networkHandler: NetworkHandler,
    private val loginUseCase: LoginUseCase,
    private val analyticManager: AnalyticManager
) : BaseViewModel(uiSchedulers, ioScheduler, networkHandler) {

    val loginResult = SingleLiveEvent<Profile>()
    val userUnverifiedEvent = SingleLiveEvent<LoginHadleModel>()

    fun doLogin(email: String, password: String) {
        executeJob {
            isLoadingLiveData.value = true
            loginUseCase.doLogin(email, password)
                .compose(applySchedulers())
                .subscribe({
                    if (it is LoginHadleModel.VerifiedUser) { // if
                        sendUserLoginToAnalytics(it.profile)// user is verified
                        isLoadingLiveData.value = false
                        loginResult.value = it.profile
                    } else {
                        isLoadingLiveData.value = false
                        userUnverifiedEvent.value = it
                    }
                }, {
                    isLoadingLiveData.value = false
                    if (it is UndefinedUsernameType) { // if usernametype is undefine set error failure to show toast
                        handleFailure(Failure.ServerError(it.message ?: ""))
                    }
                    handleFailure(it.getGeneralErrorServer())
                })
                .disposedBy(disposable)
        }
    }

    private fun sendUserLoginToAnalytics(profile: Profile) {
        analyticManager.trackingUserLogin(LoginSuccessPayloadBuilder(
            profile.id,
            "${profile.firstName} ${profile.lastName}",
            profile.firstName,
            profile.lastName,
            profile.email,
            profile.phone,
            profile.userDetails?.age?.year,
            profile.loginAt
        ))
    }
}