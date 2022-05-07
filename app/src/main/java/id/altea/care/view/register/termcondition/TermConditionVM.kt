package id.altea.care.view.register.termcondition

import id.altea.care.core.base.BaseViewModel
import id.altea.care.core.domain.model.Auth
import id.altea.care.core.domain.model.Block
import id.altea.care.core.domain.model.RegisterInfo
import id.altea.care.core.domain.usecase.RegistrationUseCase
import id.altea.care.core.domain.usecase.GetBlocksUseCase
import id.altea.care.core.ext.TagInjection
import id.altea.care.core.ext.disposedBy
import id.altea.care.core.ext.getGeneralErrorServer
import id.altea.care.core.helper.NetworkHandler
import id.altea.care.core.helper.SingleLiveEvent
import io.reactivex.Scheduler
import javax.inject.Inject
import javax.inject.Named

class TermConditionVM @Inject constructor(
    @Named(TagInjection.UI_Scheduler) uiSchedulers: Scheduler,
    @Named(TagInjection.IO_Scheduler) ioScheduler: Scheduler,
    networkHandler: NetworkHandler,
    private val registrationUseCase: RegistrationUseCase,
    private val getBlocksUseCase: GetBlocksUseCase
) : BaseViewModel(uiSchedulers, ioScheduler, networkHandler) {

    val authEvent = SingleLiveEvent<Auth>()
    val blockEvent = SingleLiveEvent<List<Block>>()

    fun doRegister(registerInfo: RegisterInfo) {
        executeJob {
            registrationUseCase.doRegister(registerInfo)
                .compose(applySchedulers())
                .doOnSubscribe { isLoadingLiveData.value = true }
                .doAfterTerminate { isLoadingLiveData.value = false }
                .subscribe({ authEvent.value = it }, { handleFailure(it.getGeneralErrorServer()) })
                .disposedBy(disposable)
        }
    }

    fun getTermAndCondition() {
        executeJob {
            getBlocksUseCase
                .getBlocks()
                .map { it.filter { it.type == TermConditionActivity.BlockType.TERMS_CONDITION.toString() } }
                .compose(applySchedulers())
                .doOnSubscribe { isLoadingLiveData.value = true }
                .doAfterTerminate { isLoadingLiveData.value = false }
                .subscribe({
                    blockEvent.value = it
                },{
                    handleFailure(it.getGeneralErrorServer())
                }).disposedBy(disposable)

        }
    }

}
