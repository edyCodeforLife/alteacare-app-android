package id.altea.care.view.contact

import id.altea.care.core.base.BaseViewModel
import id.altea.care.core.data.request.MessageRequest
import id.altea.care.core.domain.cache.AuthCache
import id.altea.care.core.domain.model.Auth
import id.altea.care.core.domain.model.InformationCentral
import id.altea.care.core.domain.model.Profile
import id.altea.care.core.domain.usecase.GetAuthUseCase
import id.altea.care.core.domain.usecase.GetInformationCentralUseCase
import id.altea.care.core.domain.usecase.GetProfileUseCase
import id.altea.care.core.domain.usecase.SendMessageUseCase
import id.altea.care.core.exception.Failure
import id.altea.care.core.ext.TagInjection
import id.altea.care.core.ext.disposedBy
import id.altea.care.core.ext.getGeneralErrorServer
import id.altea.care.core.helper.NetworkHandler
import id.altea.care.core.helper.SingleLiveEvent
import io.reactivex.Scheduler
import javax.inject.Inject
import javax.inject.Named

/**
 * Created by trileksono on 11/03/21.
 */
class ContactVM @Inject constructor(
    @Named(TagInjection.UI_Scheduler) uiSchedulers: Scheduler,
    @Named(TagInjection.IO_Scheduler) ioScheduler: Scheduler,
    networkHandler: NetworkHandler,
    private val getInformationCentralUseCase: GetInformationCentralUseCase,
    private val sendMessageUseCase: SendMessageUseCase,
    private val profileUseCase: GetProfileUseCase,
    private val auth: AuthCache
) : BaseViewModel(uiSchedulers, ioScheduler, networkHandler) {
    val informationCentralLivedata = SingleLiveEvent<InformationCentral>()
    val resultLiveData = SingleLiveEvent<Boolean>()
    val profile = SingleLiveEvent<Profile>()

    fun onFirstLaunch() {
        if (auth.getToken().isNotEmpty()) getProfile()
        getInformationCentral()
    }

    private fun getInformationCentral(){
        executeJob {
            getInformationCentralUseCase
                .getInformationCentral()
                .compose(applySchedulers())
                .doOnSubscribe { isLoadingLiveData.value = true  }
                .doOnTerminate { isLoadingLiveData.value = false }
                .subscribe({
                    informationCentralLivedata.value = it
                },{
                    handleFailure(it.getGeneralErrorServer())
                }).disposedBy(disposable)
        }
    }

    fun sendMessage(name :String,email : String,phoneNumber :String,userId : String?,idMessage : String, messageContent : String){
        executeJob {
            sendMessageUseCase
                .sendMessage(MessageRequest(name,email,phoneNumber,userId,idMessage,messageContent))
                .compose(applySchedulers())
                .doOnSubscribe { isLoadingLiveData.value = true }
                .doOnTerminate { isLoadingLiveData.value = false }
                .subscribe({
                    resultLiveData.value = it
                },{
                    handleFailure(it.getGeneralErrorServer())
                }).disposedBy(disposable)
        }
    }


    private fun getProfile() {
        executeJob {
            isLoadingLiveData.value = true
            profileUseCase.getProfile()
                    .subscribeOn(ioScheduler)
                    .observeOn(uiSchedulers)
                    .subscribe({ resultProfile ->
                        if (resultProfile.status == true) {
                            profile.value = resultProfile.data ?: null
                            isLoadingLiveData.value = false
                        }
                    }, {
                        handleFailure(it.getGeneralErrorServer())
                    }).disposedBy(disposable)
        }
    }

}
