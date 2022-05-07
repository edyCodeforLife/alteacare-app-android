package id.altea.care.core.domain.event

import id.altea.care.core.domain.model.PatientFamily

sealed class MyConsultationFilterEvent {
    data class OpenFilterEvent(val patientFamily: PatientFamily?) : MyConsultationFilterEvent()
    data class SelectedFilterEvent(val patientFamily: PatientFamily?) : MyConsultationFilterEvent()
}
