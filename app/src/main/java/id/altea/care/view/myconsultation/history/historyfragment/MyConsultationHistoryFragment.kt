package id.altea.care.view.myconsultation.history.historyfragment

import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import id.altea.care.R
import id.altea.care.core.domain.event.AppointmentStatusUpdateEvent
import id.altea.care.core.domain.event.MyConsultationFilterEvent
import id.altea.care.core.ext.disposedBy
import id.altea.care.core.helper.RxBus
import id.altea.care.view.myconsultation.history.BaseHistoryFragment

class MyConsultationHistoryFragment : BaseHistoryFragment<MyConsultationHistoryVM>() {

    override fun setStringResCardEmptyAppointment(): Int {
        return R.string.str_history_empty
    }

    override fun bindViewModel(): MyConsultationHistoryVM {
        return ViewModelProvider(this, viewModelFactory)[MyConsultationHistoryVM::class.java]
    }

    override fun listenRxBus() {
        RxBus.getEvents()
            .subscribe {
                when (it) {
                    is AppointmentStatusUpdateEvent -> viewModel?.onFirstLaunch()
                    is MyConsultationFilterEvent.SelectedFilterEvent -> {
                        if (isResumed) {
                            viewModel?.doFilterPatient(it.patientFamily)
                            if (it.patientFamily != null) {
                                viewBinding?.historyTxtFilter?.run {
                                    this.isVisible = true
                                    text = it.patientFamily.familyRelationType?.name
                                }
                                viewBinding?.text?.isVisible = true
                            } else {
                                viewBinding?.historyTxtFilter?.isVisible = false
                                viewBinding?.text?.isVisible = false
                            }
                        }
                    }
                }
            }
            .disposedBy(disposable)
    }
}
