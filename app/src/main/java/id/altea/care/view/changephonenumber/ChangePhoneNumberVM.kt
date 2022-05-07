package id.altea.care.view.changephonenumber

import id.altea.care.core.base.BaseViewModel
import id.altea.care.core.domain.model.Empty
import id.altea.care.core.domain.model.Token
import id.altea.care.core.domain.usecase.GetRequestChangePhoneUserCase
import id.altea.care.core.domain.usecase.RegisterChangePhoneNumberUseCase
import id.altea.care.core.ext.TagInjection
import id.altea.care.core.ext.disposedBy
import id.altea.care.core.ext.getGeneralErrorServer
import id.altea.care.core.helper.NetworkHandler
import id.altea.care.core.helper.SingleLiveEvent
import id.altea.care.view.otpmail.EmailOtpType
import io.reactivex.Scheduler
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Named

class ChangePhoneNumberVM @Inject constructor(
    @Named(TagInjection.UI_Scheduler) uiSchedulers: Scheduler,
    @Named(TagInjection.IO_Scheduler) ioScheduler: Scheduler,
    networkHandler: NetworkHandler,
    private val useCase: RegisterChangePhoneNumberUseCase

) : BaseViewModel(uiSchedulers, ioScheduler, networkHandler) {

    val changePhonerNumberState = SingleLiveEvent<Empty>()

    fun doChangePhoneNumberRegister(phoneNumber: String, token: String) {
        useCase.doChangePhoneNumberRegister(phoneNumber, token)
            .compose(applySchedulers())
            .doOnSubscribe {
                isLoadingLiveData.value = true
            }
            .doAfterTerminate {
                isLoadingLiveData.value = false
            }.subscribe(
                { changePhonerNumberState.value = Empty() },
                { handleFailure(it.getGeneralErrorServer()) })
            .disposedBy(disposable)
    }

}