package id.altea.care.core.domain.model

import id.altea.care.view.common.enums.TypeAppointment

data class AppointmentData(
    val created: String?,
    val doctor: Doctor?,
    val id: Int?,
    val orderCode: String?,
    val schedule: DoctorSchedule?,
    val status: String?,
    val expiredAt: String?,
    val inOperationalHour: Boolean?,
    val user: Profile?,
    val statusDetail: AppointmentStatusDetail?,
    val transaction: Transaction?,
    val patient: AppointmentPatient
) {
    companion object {
        fun mapToMyAppointment(appointment: AppointmentData?): MyAppointment {
            val hospitals = appointment?.doctor?.hospital
            val startTime = if (appointment?.schedule?.startTime != null && appointment.schedule.startTime.length >= 4)
                appointment.schedule.startTime.substring(0, 5) else appointment?.schedule?.startTime
            val endTime = if (appointment?.schedule?.endTime != null && appointment.schedule.endTime.length >= 4)
                appointment.schedule.endTime.substring(0, 5) else appointment?.schedule?.endTime

            return MyAppointment(
                id = appointment?.id ?: 0,
                orderCode = appointment?.orderCode.orEmpty(),
                doctorName = appointment?.doctor?.doctorName.orEmpty(),
                specialization = appointment?.doctor?.specialization?.name.orEmpty(),
                hospitalName = if (!hospitals.isNullOrEmpty()) hospitals[0].name.orEmpty() else "",
                scheduleDate = appointment?.schedule?.date.orEmpty(),
                scheduleStart = startTime.orEmpty(),
                scheduleEnd = endTime.orEmpty(),
                doctorImage = appointment?.doctor?.photo.orEmpty(),
                hospitalImage = if (!hospitals.isNullOrEmpty()) hospitals[0].icon.orEmpty() else "",
                status = if (!appointment?.status.isNullOrEmpty()) TypeAppointment.valueOf(appointment?.status.orEmpty()) else null,
                expiredAt = appointment?.expiredAt,
                inOperationalHour = appointment?.inOperationalHour,
                statusDetail = appointment?.statusDetail,
                transaction = appointment?.transaction,
                patient = appointment?.patient,
                doctorId = appointment?.doctor?.doctorId
            )
        }
    }
}
