package id.altea.care.view.myconsultation

import id.altea.care.core.base.BaseViewModel
import id.altea.care.core.domain.model.PatientFamily
import id.altea.care.core.domain.usecase.GetFamilyMemberUseCase
import id.altea.care.core.ext.TagInjection
import id.altea.care.core.ext.disposedBy
import id.altea.care.core.ext.getGeneralErrorServer
import id.altea.care.core.helper.NetworkHandler
import id.altea.care.core.helper.SingleLiveEvent
import io.reactivex.Scheduler
import javax.inject.Inject
import javax.inject.Named

class MyConsultationFragmentVM @Inject constructor(
    @Named(TagInjection.UI_Scheduler) uiSchedulers: Scheduler,
    @Named(TagInjection.IO_Scheduler) ioScheduler: Scheduler,
    networkHandler: NetworkHandler,
    private val getFamilyMemberUseCase: GetFamilyMemberUseCase
) : BaseViewModel(uiSchedulers, ioScheduler, networkHandler) {

    val listPatientEvent = SingleLiveEvent<Pair<PatientFamily?, List<PatientFamily>>>()
    private val listPatient = mutableListOf<PatientFamily>()

    init {
        getFamilyMember()
    }

    private fun getFamilyMember() {
        executeJob {
            getFamilyMemberUseCase.getAllFamilyMember(1)
                .subscribeOn(ioScheduler)
                .observeOn(uiSchedulers)
                .subscribe({
                    listPatient.addAll(it)
                }, {
                    handleFailure(it.getGeneralErrorServer())
                })
                .disposedBy(disposable)
        }
    }

    fun onFilterClicked(patientFamily: PatientFamily?) {
        if (listPatient.isEmpty()) {
            getFamilyMember()
        } else {
            listPatientEvent.value = Pair(patientFamily, listPatient)
        }
    }

}
