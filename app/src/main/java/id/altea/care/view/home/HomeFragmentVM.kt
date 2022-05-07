package id.altea.care.view.home

import com.moengage.core.internal.model.database.QueryParams
import id.altea.care.core.analytics.AnalyticManager
import id.altea.care.core.base.BaseViewModel
import id.altea.care.core.domain.model.*
import id.altea.care.core.domain.usecase.*
import id.altea.care.core.exception.NotFoundFailure
import id.altea.care.core.ext.TagInjection
import id.altea.care.core.ext.disposedBy
import id.altea.care.core.ext.formatDate
import id.altea.care.core.ext.getGeneralErrorServer
import id.altea.care.core.helper.NetworkHandler
import id.altea.care.core.helper.SingleLiveEvent
import id.altea.care.core.helper.util.ConstantQueryParam
import id.altea.care.core.helper.util.ConstantSortType
import id.altea.care.view.home.failure.HomeFailure
import io.reactivex.Flowable
import io.reactivex.Scheduler
import io.reactivex.Single
import retrofit2.HttpException
import timber.log.Timber
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Named

class HomeFragmentVM @Inject constructor(
    @Named(TagInjection.UI_Scheduler) uiSchedulers: Scheduler,
    @Named(TagInjection.IO_Scheduler) ioScheduler: Scheduler,
    networkHandler: NetworkHandler,
    private val switchAccountUseCase: SwitchAccountUseCase,
    private val getAuthUseCase: GetAuthUseCase,
    private val getSpecialistUseCase: GetSpecialistUseCase,
    private val getProfileUseCase: GetProfileUseCase,
    private val getOnGoingAppointmentUseCase: GetOnGoingAppointmentUseCase,
    private val getBannerUseCase: GetBannerUseCase,
    private val analyticsUseCase: AnalyticsUseCase,
    private val analyticManager: AnalyticManager,
    private val getOperationalHourMA: GetOperationalHourMaUseCase,
    private val getWidgetsUseCase: GetWidgetsUseCase,
    private val getPromotionListUseCase: GetPromotionListUseCase
) : BaseViewModel(uiSchedulers, ioScheduler, networkHandler) {

    val isUserLoggedInEvent = SingleLiveEvent<Boolean>()
    val specialistEvent = SingleLiveEvent<List<Specialization>>()
    val appointmentEvent = SingleLiveEvent<List<AppointmentData>>()
    val profileEvent = SingleLiveEvent<Profile>()
    val bannerEvent = SingleLiveEvent<List<Banner>>()
    val bannerClickEvent = SingleLiveEvent<String>()
    val showButtonSwitchAccountEvent = SingleLiveEvent<Boolean>()
    val operationalHourMAEvent = SingleLiveEvent<OperationalHourMA>()
    val widgets = SingleLiveEvent<List<Widgets>>()
    val promotionList = SingleLiveEvent<List<PromotionList>>()
    private val bodyParam = HashMap<String, Any>()

    private var isLoggedIn: Boolean = false
    var accountLoggedIn = emptyList<Account>()

    init {
        isLoggedIn = getAuthUseCase.getIsLoggedIn()
    }

    fun onFirstLaunch() {
        isUserLoggedInEvent.postValue(isLoggedIn)
        bodyParam.apply {
            put(ConstantQueryParam.QUERY_LIMIT_PROMOTION,5)
        }
        if (isLoggedIn) {
            getSpecialist()
            getProfile()
            getMyAppointment()
            getBanner()
            getLoginAccount()
            getOperationalHourMa()
            getWidgets()
            getPromotionList()
        } else {
            getWidgets()
            getBanner()
            getSpecialist()
            getPromotionList()
        }
    }

    private fun getLoginAccount() {
        Single.fromCallable { switchAccountUseCase.getAccountIsLoggedin() }
            .compose(applySchedulers())
            .subscribe({
                accountLoggedIn = it
                showButtonSwitchAccountEvent.value = true
            }, {})
            .disposedBy(disposable)
    }

    private fun getSpecialist() {
        executeJob {
            getSpecialistUseCase.getSpecialist(GetSpecialistUseCase.Param(true))
                .retryWhen { it.take(2).delay(1, TimeUnit.SECONDS) }
                .compose(applySchedulers())
                .subscribe(
                    { specialistEvent.value = it },
                    { handleFailure(HomeFailure.HomeSpecializationFailure()) })
                .disposedBy(disposable)
        }
    }

    fun getProfile() {
        executeJob {
            getProfileUseCase.getProfile()
                .retryWhen { it.take(2).delay(1, TimeUnit.SECONDS) }
                .compose(applySchedulers())
                .subscribe(
                    {
                        it?.data?.apply { profileEvent.postValue(this) }
                        sendTrackingCityToAnalytics(it.data)
                    },
                    { handleFailure(HomeFailure.HomeSpecializationFailure()) })
                .disposedBy(disposable)
        }
    }

    private fun sendTrackingCityToAnalytics(data: Profile) {
        val city = data.defaultPatientData?.city?.name
        if (!city.isNullOrEmpty()) {
            analyticsUseCase.checkTrackingHasBeenSent {
                analyticManager.trackingCity(city)
            }
        } else {
            Timber.d("city is null")
        }
    }

    fun getMyAppointment() {
        val today = Date()
        executeJob {
            getOnGoingAppointmentUseCase.doRequestOnGoingAppointment(
                GetOnGoingAppointmentUseCase.Param(
                    1,
                    "createdAt",
                    ConstantSortType.SORT_ASC,
                    null,
                    today.formatDate("yyyy-MM-dd"),
                    today.formatDate("yyyy-MM-dd")
                )
            )
                .retryWhen {
                    it.take(2)
                        .flatMap { exception ->
                            return@flatMap if (exception is HttpException && exception.code() == 404) {
                                Flowable.error<HttpException>(exception)
                            } else {
                                Flowable.just(0)
                            }
                        }
                        .delay(1, TimeUnit.SECONDS)
                }
                .compose(applySchedulers())
                .subscribe(
                    { appointmentEvent.value = it },
                    {
                        if (it is HttpException && it.code() == 404) {
                            handleFailure(NotFoundFailure.DataNotExist())
                        } else {
                            handleFailure(it.getGeneralErrorServer())
                        }
                    })
                .disposedBy(disposable)
        }
    }

    fun getRedirectUrl(redirectUrl: String?): String? {
        return redirectUrl?.replace(
            "{{REPLACE_THIS_TO_BEARER_USER}}",
            getAuthUseCase.getToken()
        )
    }

    private fun getBanner() {
        executeJob {
            getBannerUseCase
                .getBanner(category = "TELEMEDICINE")
                .retryWhen { it.take(2).delay(1, TimeUnit.SECONDS) }
                .compose(applySchedulers())
                .subscribe({ bannerResult ->
                    bannerEvent.value = bannerResult
                }, { throwable ->
                    throwable.printStackTrace()
                    // do nothing on banner error
                }).disposedBy(disposable)
        }
    }

    fun onBannerClicked(banner: Banner) {
        banner.deeplinkTypeAndroid?.let {
            if (banner.needLogin == true && !isLoggedIn) {
                handleFailure(HomeFailure.HomeBannerClickNeedLogin())
            } else {
                if (it == "WEB") {
                    bannerClickEvent.value = banner.deeplinkUrlAndroid?.replace(
                        "{email}",
                        "${profileEvent.value?.email}&at=${getAuthUseCase.getToken()}"
                    )
                } else {
                    // todo another type of banner click
                }
            }
        }
    }

    @Suppress("UnstableApiUsage")
    fun onSwitchAccount(userId: String) {
        Single.fromCallable { switchAccountUseCase.switchAccount(userId) }
            .compose(applySchedulers())
            .doOnSubscribe { isLoadingLiveData.value = true }
            .doOnTerminate { isLoadingLiveData.value = false }
            .subscribe({
                onFirstLaunch()
            }, {
                handleFailure(it.getGeneralErrorServer())
            })
            .disposedBy(disposable)
    }

    fun getOperationalHourMa() {
        executeJob {
            getOperationalHourMA.getOperationalHourMA()
                .retryWhen { it.take(2).delay(1, TimeUnit.SECONDS) }
                .compose(applySchedulers())
                .doOnSubscribe { isLoadingLiveData.value = true }
                .doOnTerminate { isLoadingLiveData.value = false }
                .subscribe({ dataOperationalHourMA ->
                    operationalHourMAEvent.value = dataOperationalHourMA
                }, { throwable ->
                    throwable.printStackTrace()
                }).disposedBy(disposable)
        }
    }

    private fun getWidgets() {
        executeJob {
            getWidgetsUseCase.getWidgets()
                .retryWhen { it.take(2).delay(1, TimeUnit.SECONDS) }
                .compose(applySchedulers())
                .doOnSubscribe { isLoadingLiveData.value = true }
                .doOnTerminate { isLoadingLiveData.value = false }
                .subscribe({ dataWidgets ->
                    widgets.value = dataWidgets
                }, { throwable ->
                    throwable.printStackTrace()
                }).disposedBy(disposable)
        }
    }

    fun replaceVaccineUrl(vaccineUrl: String): String {
        return vaccineUrl.replace(
            "{email}",
            "${profileEvent.value?.email}&at=${getAuthUseCase.getToken()}"
        )
    }

    fun setupCookie(): String {
        return getAuthUseCase.getToken()
    }

    fun getPromotionList() {
        executeJob {
            getPromotionListUseCase.getPromotionList(1,bodyParam)
                .retryWhen { it.take(2).delay(1, TimeUnit.SECONDS) }
                .compose(applySchedulers())
                .doOnSubscribe { isLoadingLiveData.value = true }
                .doOnTerminate { isLoadingLiveData.value = false }
                .subscribe({ dataPromotion ->
                    promotionList.value = dataPromotion
                }, { throwable ->
                    if (throwable is HttpException && throwable.code() == 404) {
                        handleFailure(NotFoundFailure.DataPromotionNotExist())
                    } else {
                        handleFailure(throwable.getGeneralErrorServer())
                    }
                }).disposedBy(disposable)
        }
    }
}
