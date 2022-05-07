package id.altea.care.core.domain.usecase

import id.altea.care.core.data.request.VoucherRequest
import id.altea.care.core.domain.model.Voucher
import id.altea.care.core.domain.repository.PaymentRepository
import io.reactivex.Single
import javax.inject.Inject

class VoucherUseCase @Inject constructor(private val paymentRepository: PaymentRepository) {

    fun getVoucher(code: String?,transactionId: String?,typeOfService: String?) : Single<Voucher>{
        return paymentRepository.getVoucher(VoucherRequest(code,transactionId,typeOfService))
    }

}