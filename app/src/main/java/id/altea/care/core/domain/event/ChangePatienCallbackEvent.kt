package id.altea.care.core.domain.event

import id.altea.care.core.domain.model.PatientFamily

data class ChangePatienCallbackEvent(val changePatienCallbackEventCreated : Boolean = false, val patientFamily: PatientFamily)