package id.altea.care.view.password

import androidx.lifecycle.MutableLiveData
import id.altea.care.core.base.BaseViewModel
import id.altea.care.core.domain.usecase.ChangePasswordUseCase
import id.altea.care.core.domain.usecase.GetAuthUseCase
import id.altea.care.core.domain.usecase.RegisterPasswordValidationUseCase
import id.altea.care.core.ext.TagInjection
import id.altea.care.core.ext.disposedBy
import id.altea.care.core.ext.getGeneralErrorServer
import id.altea.care.core.helper.NetworkHandler
import id.altea.care.core.helper.SingleLiveEvent
import io.reactivex.Scheduler
import javax.inject.Inject
import javax.inject.Named

/**
 * Created by trileksono on 05/03/21.
 */
class PasswordVM @Inject constructor(
    @Named(TagInjection.UI_Scheduler) uiSchedulers: Scheduler,
    @Named(TagInjection.IO_Scheduler) ioScheduler: Scheduler,
    networkHandler: NetworkHandler,
    private val passwordValidationUseCase: RegisterPasswordValidationUseCase,
    private val changePasswordUseCase: ChangePasswordUseCase,
    private val getAuthUseCase: GetAuthUseCase
) : BaseViewModel(uiSchedulers, ioScheduler, networkHandler) {

    val isLengtValid = MutableLiveData<Boolean>()
    val isContainsNumber = MutableLiveData<Boolean>()
    val isContainsLowerText = MutableLiveData<Boolean>()
    val isContainsUpperText = MutableLiveData<Boolean>()
    val isPasswordEquals = MutableLiveData<Boolean>()
    val isChangePasswordSuccess = SingleLiveEvent<Boolean>()

    fun validatePassword(password: String) {
        isLengtValid.value = passwordValidationUseCase.isLengthValid(password)
        isContainsNumber.value = passwordValidationUseCase.isContainNumber(password)
        isContainsLowerText.value = passwordValidationUseCase.isContainLowerText(password)
        isContainsUpperText.value = passwordValidationUseCase.isContainUpperText(password)
    }

    private fun isPasswordValid(): Boolean {
        return isLengtValid.value!! && isContainsUpperText.value!! &&
                isContainsNumber.value!! && isContainsLowerText.value!!
    }

    fun isPasswordAndRePasswordValid(password: String, rePassword: String) {
        isPasswordEquals.value = (password == rePassword) && isPasswordValid()
    }

    fun doChangePassword(password: String, confirmPassword: String) {
        changePasswordUseCase.changePassword(
            password,
            confirmPassword
        ).compose(applySchedulers())
            .doOnSubscribe { isLoadingLiveData.value = true }
            .doAfterTerminate { isLoadingLiveData.value = false }
            .subscribe(
                { isChangePasswordSuccess.value = it },
                { handleFailure(it.getGeneralErrorServer()) })
            .disposedBy(disposable)
    }

    fun doChangeForgotPassword(token : String,password: String, confirmPassword: String){
        changePasswordUseCase.changeForgotPassword(
            token,
            password,
            confirmPassword
        ).compose(applySchedulers())
            .doOnSubscribe { isLoadingLiveData.value = true }
            .doAfterTerminate { isLoadingLiveData.value = false }
            .subscribe(
                { isChangePasswordSuccess.value = it },
                { handleFailure(it.getGeneralErrorServer()) })
            .disposedBy(disposable)

    }
}
