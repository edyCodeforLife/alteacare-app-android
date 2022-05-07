package id.altea.care.core.domain.usecase

import id.altea.care.core.domain.cache.AuthCache
import id.altea.care.core.domain.model.PaymentTypes
import id.altea.care.core.domain.repository.DataRepository
import id.altea.care.core.helper.util.ConstantQueryParam
import io.reactivex.Single
import javax.inject.Inject

class GetPaymentTypesUseCase @Inject constructor(
    private val dataRepository: DataRepository,
    private val authCache: AuthCache
) {
    fun getAppointmentTypes(
        appointmentId: Int,
        typeOfService: String,
        voucherCode: String?
    ): Single<List<PaymentTypes>> {
        val queryParam = mutableMapOf<String, Any>()
        queryParam[ConstantQueryParam.QUERY_TRX_ID] = appointmentId
        queryParam[ConstantQueryParam.QUERY_TYPE_OF_SERVICE] = typeOfService
        voucherCode?.let {
            queryParam[ConstantQueryParam.QUERY_VOUCHER_CODE] = it
        }
        return dataRepository.getPaymentTypes(queryParam)
    }
}
