package id.altea.care.core.domain.event

import id.altea.care.view.common.enums.TypeAppointment

data class AppointmentStatusUpdateEvent(val appointmentId: Int, val appointmentType: TypeAppointment?)
