package id.altea.care.core.domain.model

import id.altea.care.core.domain.event.MyConsultationFilterEvent

sealed class MyConsultationDateEvent {
    data class MyConsultationDateData(val myConsultationDate: String?) : MyConsultationDateEvent()
}