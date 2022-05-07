package id.altea.care.view.otpmail

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
import timber.log.Timber
import java.util.*
import javax.inject.Inject
import javax.inject.Named

/**
 * Created by trileksono on 08/03/21.
 */
class EmailOtpVM @Inject constructor(
    @Named(TagInjection.UI_Scheduler) uiSchedulers: Scheduler,
    @Named(TagInjection.IO_Scheduler) ioScheduler: Scheduler,
    networkHandler: NetworkHandler,
    private val registerSendEmailUseCase: RegisterSendEmailOtpUseCase,
    private val registerValidateEmailOtpUseCase: RegisterValidateEmailOtpUseCase,
    private val forgotPasswordUseCase: ForgotPasswordUseCase,
    private val getRequestChangeEmailUseCase: GetRequestChangeEmailUseCase,
    private val getChangeEmailOtpUseCase: GetChangeEmailOtpUseCase
) : BaseViewModel(uiSchedulers, ioScheduler, networkHandler) {

    private var countDownTimer: CountDownTimer? = null
    private val defaultCountDown = 120_000L
    private var tokenRegister: String = ""
    private lateinit var email: String
    private lateinit var type: EmailOtpType

    val countDownState = SingleLiveEvent<CountDownState>()
    val otpRequest = SingleLiveEvent<Int>()
    val otpRegisterValidation = SingleLiveEvent<String>()
    val otpAccountValidation = SingleLiveEvent<Boolean>()
    val otpForgotPassValidation = SingleLiveEvent<Auth>()
    val registerType = SingleLiveEvent<Empty>()
    val forgotPassType = SingleLiveEvent<Empty>()
    val accountType = SingleLiveEvent<Empty>()

    fun onFirstLaunch(type: EmailOtpType, email: String, token: String?) {
        this.email = email
        this.type = type
        token?.let { tokenRegister = it }

        when (type) {
            EmailOtpType.FORGOT_PASSWORD -> {
                forgotPassType.postValue(Empty())
                startCountDown()
            }
            EmailOtpType.REGISTER -> {
                registerType.postValue(Empty())
                doRequestEmailOtpRegistration(this.email, tokenRegister)
            }
            EmailOtpType.ACCOUNT -> {
                accountType.postValue(Empty())
                startCountDown()
                dochangeEmailRequestAccount(this.email)
            }
        }
    }

    private fun doRequestEmailOtpRegistration(email: String, token: String) {
        executeJob {
            registerSendEmailUseCase.doRequestEmailOTP(email, token)
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

    private fun dochangeEmailRequestAccount(email : String){
        getRequestChangeEmailUseCase.requestChangeEmail(email)
            .compose(applySchedulers())
            .doOnSubscribe { isLoadingLiveData.value = true }
            .doOnTerminate { isLoadingLiveData.value = false }
            .subscribe ({
                otpRequest.value = 0
                startCountDown()
            },{ handleFailure(it.getGeneralErrorServer()) })
            .disposedBy(disposable)
    }

    private fun doRequestEmailOtpForgotPass(email: String) {
        executeJob {
            forgotPasswordUseCase.doRequestEmailOrSmsOtp(email)
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

    fun doChangeEmailAccountOtp(otp : String){
        executeJob {
            getChangeEmailOtpUseCase.changeEmailOtp(email,otp)
                .compose(applySchedulers())
                .doOnSubscribe { isLoadingLiveData.value = true }
                .doOnTerminate { isLoadingLiveData.value = false }
                .subscribe ({
                  otpAccountValidation.value = it
                  startCountDown()
                },{
                    handleFailure(it.getGeneralErrorServer())
                }).disposedBy(disposable)
        }
    }

    fun resendEmail(email: String) {
        this.email = email
        when (type) {
            EmailOtpType.REGISTER -> doRequestEmailOtpRegistration(email, tokenRegister)
            EmailOtpType.FORGOT_PASSWORD -> doRequestEmailOtpForgotPass(email)
            EmailOtpType.ACCOUNT -> dochangeEmailRequestAccount(email)

        }
    }

    fun doValidateOtpCode(otpCode: String) {
        executeJob {
            registerValidateEmailOtpUseCase.doValidate(email, otpCode, tokenRegister)
                .compose(applySchedulers())
                .doOnSubscribe { isLoadingLiveData.value = true }
                .doAfterTerminate { isLoadingLiveData.value = false }
                .subscribe({ auth ->
                    otpRegisterValidation.value = auth.accessToken ?: "" },
                    { handleFailure(it.getGeneralErrorServer()) })
                .disposedBy(disposable)
        }
    }

    fun doValidateOtpForgot(otpCode: String) {
        executeJob {
            forgotPasswordUseCase.doValidateTokenOtp(email, otpCode)
                .compose(applySchedulers())
                .doOnSubscribe { isLoadingLiveData.value = true }
                .doAfterTerminate { isLoadingLiveData.value = false }
                .subscribe(
                    { otpForgotPassValidation.value = it },
                    { handleFailure(it.getGeneralErrorServer()) })
                .disposedBy(disposable)
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
