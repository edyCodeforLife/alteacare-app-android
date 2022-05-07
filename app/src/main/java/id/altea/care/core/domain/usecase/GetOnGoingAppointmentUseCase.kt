package id.altea.care.core.domain.usecase

import id.altea.care.core.data.request.AppointmentListRequest
import id.altea.care.core.domain.model.AppointmentData
import id.altea.care.core.domain.repository.AppointmentRepository
import io.reactivex.Single
import javax.inject.Inject

class GetOnGoingAppointmentUseCase @Inject constructor(private val appointmentRepository: AppointmentRepository) {

    data class Param(
        val page: Int,
        val sort: String? = null,
        val sortType: String? = null,
        val keyword: String? = null,
        val startDate: String? = null,
        val endDate: String? = null,
        val patientId: String? = null
    )

    fun doRequestOnGoingAppointment(param: Param): Single<List<AppointmentData>> {
        return appointmentRepository.getOnGoingAppointment(
            AppointmentListRequest(
                page = param.page,
                scheduleDateEnd = param.endDate,
                scheduleDateStart = param.startDate,
                sortBy = param.sort,
                sortType = param.sortType,
                keyword = param.keyword,
                patientId =  param.patientId
            )
        )
    }

}