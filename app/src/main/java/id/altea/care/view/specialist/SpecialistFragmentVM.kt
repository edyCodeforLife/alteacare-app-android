package id.altea.care.view.specialist

import id.altea.care.core.base.BaseViewModel
import id.altea.care.core.domain.model.Specialization
import id.altea.care.core.domain.usecase.GetSpecialistUseCase
import id.altea.care.core.ext.TagInjection
import id.altea.care.core.ext.disposedBy
import id.altea.care.core.ext.getGeneralErrorServer
import id.altea.care.core.helper.NetworkHandler
import id.altea.care.core.helper.SingleLiveEvent
import io.reactivex.Scheduler
import javax.inject.Inject
import javax.inject.Named

class SpecialistFragmentVM @Inject constructor(
    @Named(TagInjection.UI_Scheduler) uiSchedulers: Scheduler,
    @Named(TagInjection.IO_Scheduler) ioScheduler: Scheduler,
    networkHandler: NetworkHandler,
    private val getSpecialistUseCase: GetSpecialistUseCase
) : BaseViewModel(uiSchedulers, ioScheduler, networkHandler) {

    val specialistResponse = SingleLiveEvent<List<Specialization>>()

    fun getSpecialist() {
        executeJob {
            getSpecialistUseCase.getSpecialist()
                .compose(applySchedulers())
                .doOnSubscribe { isLoadingLiveData.value = true }
                .doOnTerminate { isLoadingLiveData.value = false }
                .subscribe(
                    {
                        specialistResponse.value = it
                    }, {
                        handleFailure(it.getGeneralErrorServer())
                    })
                .disposedBy(disposable)
        }
    }
}
