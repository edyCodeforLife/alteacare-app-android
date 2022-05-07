package id.altea.care.view.changephonenumber.otpphone

import android.os.CountDownTimer
import id.altea.care.core.base.BaseViewModel
import id.altea.care.core.domain.model.CountDownState
import id.altea.care.core.domain.model.Empty
import id.altea.care.core.domain.usecase.GetChangePhoneOtpUseCase
import id.altea.care.core.domain.usecase.GetRequestChangePhoneUserCase
import id.altea.care.core.ext.TagInjection
import id.altea.care.core.ext.disposedBy
import id.altea.care.core.ext.getGeneralErrorServer
import id.altea.care.core.helper.NetworkHandler
import id.altea.care.core.helper.SingleLiveEvent
import io.reactivex.Scheduler
import java.util.*
import javax.inject.Inject
import javax.inject.Named

class PhoneOtpVM @Inject constructor(
    @Named(TagInjection.UI_Scheduler) uiSchedulers: Scheduler,
    @Named(TagInjection.IO_Scheduler) ioScheduler: Scheduler,
    networkHandler: NetworkHandler,
    val getRequestChangePhoneUserCase : GetRequestChangePhoneUserCase,
    val getChangePhoneOtpUserCase : GetChangePhoneOtpUseCase
) : BaseViewModel(uiSchedulers, ioScheduler, networkHandler) {
    private var countDownTimer: CountDownTimer? = null
    private val defaultCountDown = 120_000L
    val phoneNumberType = SingleLiveEvent<Empty>()
    val countDownState = SingleLiveEvent<CountDownState>()
    val otpRequest = SingleLiveEvent<Int>()
    val otpPhoneValidation = SingleLiveEvent<Boolean>()
    private lateinit var phoneNumber: String


    fun onFirstLaunch(phoneNumber: String) {
        this.phoneNumber = phoneNumber
        getRequestChangePhoneNumber(phoneNumber)
        phoneNumberType.postValue(Empty())
    }


    fun getRequestChangePhoneNumber(phone: String) {
        executeJob {
            getRequestChangePhoneUserCase
                .requestChangePhone(phone)
                .compose(applySchedulers())
                .doOnSubscribe { isLoadingLiveData.postValue(true) }
                .doOnTerminate { isLoadingLiveData.postValue(false) }
                .subscribe({
                    otpRequest.value = 0
                    startCountDown()
                }, {
                    handleFailure(it.getGeneralErrorServer())
                }).disposedBy(disposable)
        }
    }


    fun doChangePhoneNumberAccountOtp(otp: String) {
        executeJob {
            getChangePhoneOtpUserCase.requestChangePhoneOtp(phoneNumber, otp)
                .compose(applySchedulers())
                .doOnSubscribe { isLoadingLiveData.postValue(true) }
                .doOnTerminate { isLoadingLiveData.postValue(false) }
                .subscribe({
                    otpPhoneValidation.postValue(it)
                    startCountDown()
                }, {
                    handleFailure(it.getGeneralErrorServer())
                }).disposedBy(disposable)
        }
    }


    private fun startCountDown() {
        countDownTimer?.cancel()
        countDownTimer = object : CountDownTimer(defaultCountDown, 1000L) {
            override fun onTick(millisUntilFinished: Long) {
                val minutes = (millisUntilFinished / 1000).toInt() / 60
                val seconds = (millisUntilFinished / 1000).toInt() % 60
                val timeLeftFormatted: String =
                    String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
                countDownState.value = CountDownState.onTick(timeLeftFormatted)
            }

            override fun onFinish() {
                countDownState.value = CountDownState.onFinish
            }
        }.start()
    }



    override fun onCleared() {
        super.onCleared()
        countDownTimer?.cancel()
    }


}