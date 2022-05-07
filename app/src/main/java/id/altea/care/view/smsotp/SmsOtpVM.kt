package id.altea.care.view.smsotp

import android.os.CountDownTimer
import id.altea.care.core.base.BaseViewModel
import id.altea.care.core.domain.model.Auth
import id.altea.care.core.domain.model.CountDownState
import id.altea.care.core.domain.model.Empty
import id.altea.care.core.domain.usecase.*
import id.altea.care.core.ext.TagInjection
import id.altea.care.core.ext.disposedBy
import id.altea.care.core.ext.getGeneralErrorServer
import id.altea.care.core.helper.NetworkHandler
import id.altea.care.core.helper.SingleLiveEvent
import io.reactivex.Scheduler
import java.util.*
import javax.inject.Inject
import javax.inject.Named

class SmsOtpVM @Inject constructor(
    @Named(TagInjection.UI_Scheduler) uiScheduler: Scheduler,
    @Named(TagInjection.IO_Scheduler) ioScheduler: Scheduler,
    networkHandler: NetworkHandler,
    private val registerSendSmsOtpUseCase: RegisterSendSmsOtpUseCase,
    private val registerValidateSmsOtpUseCase: RegisterValidateSmsOtpUseCase,
    private val getRequestChangePhoneUserCase : GetRequestChangePhoneUserCase,
    private val getChangePhoneOtpUserCase : GetChangePhoneOtpUseCase,
    private val forgotPasswordUseCase: ForgotPasswordUseCase,
    ) : BaseViewModel(uiScheduler, ioScheduler, networkHandler) {

    private lateinit var smsOtpType: SmsOtpType
    private var countDownTimer: CountDownTimer? = null

    val registerType = SingleLiveEvent<Empty>()
    val forgotPassType = SingleLiveEvent<Empty>()
    val accountType = SingleLiveEvent<Empty>()
    val otpRequest = SingleLiveEvent<Int>()
    val otpRequestVerify = SingleLiveEvent<String>()
    val otpAccountValidation = SingleLiveEvent<Boolean>()
    val otpForgotPassValidation = SingleLiveEvent<Auth>()
    val countDownTimerState = SingleLiveEvent<CountDownState>()

    fun onFirstLaunch(smsOtpType: SmsOtpType, phoneNumber: String?, token: String?) {
        this.smsOtpType = smsOtpType
        when (smsOtpType) {
            SmsOtpType.REGISTER -> {
                registerType.postValue(Empty())
                doRequestSmsOtpRegistration(phoneNumber ?: "", token ?: "")
            }
            SmsOtpType.FORGOT_PASSWORD -> {
                forgotPassType.postValue(Empty())
                startCountDown()
            }
            SmsOtpType.ACCOUNT -> {
                accountType.postValue(Empty())
                startCountDown()
                getRequestChangePhoneNumber(phoneNumber ?: "")
            }
        }
    }

    private fun doRequestSmsOtpRegistration(phoneNumber: String, token: String) {
        executeJob {
            registerSendSmsOtpUseCase.doRequestSmsOtpRegistration(phone = phoneNumber, token = token)
                .compose(applySchedulers())
                .doOnSubscribe { isLoadingLiveData.value = true }
                .doAfterTerminate { isLoadingLiveData.value = false }
                .subscribe({
                    otpRequest.value = 0
                    startCountDown()
                }, { throwable ->
                    handleFailure(throwable.getGeneralErrorServer())
                }).disposedBy(disposable)
        }
    }

    fun doValidationSmsOtpRegistration(otp: String, phoneNumber: String, token: String) {
        executeJob {
            registerValidateSmsOtpUseCase.doValidationSmsOtpRegistration(phone = phoneNumber, otp = otp, token)
                .compose(applySchedulers())
                .doOnSubscribe { isLoadingLiveData.value = true }
                .doAfterTerminate { isLoadingLiveData.value = false }
                .subscribe({ auth ->
                    otpRequestVerify.value = auth.accessToken ?: ""
                    startCountDown()
                }, { throwable ->
                    handleFailure(throwable.getGeneralErrorServer())
                }).disposedBy(disposable)
        }
    }

    private fun getRequestChangePhoneNumber(phone: String) {
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


    fun doChangePhoneNumberAccountOtp(phoneNumber: String, otp: String) {
        executeJob {
            getChangePhoneOtpUserCase.requestChangePhoneOtp(phoneNumber, otp)
                .compose(applySchedulers())
                .doOnSubscribe { isLoadingLiveData.postValue(true) }
                .doOnTerminate { isLoadingLiveData.postValue(false) }
                .subscribe({
                    otpAccountValidation.postValue(it)
                    startCountDown()
                }, {
                    handleFailure(it.getGeneralErrorServer())
                }).disposedBy(disposable)
        }
    }

    private fun doRequestSmsOtpForgotPass(phoneNumber: String) {
        executeJob {
            forgotPasswordUseCase.doRequestEmailOrSmsOtp(phoneNumber)
                .compose(applySchedulers())
                .doOnSubscribe { isLoadingLiveData.value = true }
                .doAfterTerminate { isLoadingLiveData.value = false }
                .subscribe({
                    otpRequest.value = 0
                    startCountDown()
                }, { handleFailure(it.getGeneralErrorServer()) })
                .disposedBy(disposable)

        }
    }

    fun doValidateOtpForgot(phoneNumber: String, otpCode: String) {
        executeJob {
            forgotPasswordUseCase.doValidateTokenOtp(phoneNumber, otpCode)
                .compose(applySchedulers())
                .doOnSubscribe { isLoadingLiveData.value = true }
                .doAfterTerminate { isLoadingLiveData.value = false }
                .subscribe(
                    { otpForgotPassValidation.value = it },
                    { handleFailure(it.getGeneralErrorServer()) })
                .disposedBy(disposable)
        }
    }

    fun resendSmsOtp(phoneNumber: String?, token: String?) {
        when(smsOtpType) {
            SmsOtpType.REGISTER -> { doRequestSmsOtpRegistration(phoneNumber ?: "", token ?: "") }
            SmsOtpType.FORGOT_PASSWORD -> { doRequestSmsOtpForgotPass(phoneNumber ?: "") }
            SmsOtpType.ACCOUNT -> { getRequestChangePhoneNumber(phoneNumber ?: "") }
        }
    }


    private fun startCountDown() {
        countDownTimer?.cancel()
        countDownTimer = object : CountDownTimer(120_000L, 1000L) {
            override fun onTick(millisUntilFinished: Long) {
                val minutes = (millisUntilFinished / 1000).toInt() / 60
                val seconds = (millisUntilFinished / 1000).toInt() % 60
                val timeLeftFormatted: String = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
                countDownTimerState.value = CountDownState.onTick(timeLeftFormatted)
            }

            override fun onFinish() {
                countDownTimerState.value = CountDownState.onFinish
            }
        }.start()
    }

    override fun onCleared() {
        super.onCleared()
        countDownTimer?.cancel()
    }
}