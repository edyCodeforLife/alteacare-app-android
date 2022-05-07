package id.altea.care.core.data.repositoryimpl

import id.altea.care.core.data.network.api.PaymentApi
import id.altea.care.core.data.request.VoucherRequest
import id.altea.care.core.data.response.Response
import id.altea.care.core.data.response.VoucherResponse
import id.altea.care.core.domain.model.Voucher
import id.altea.care.core.domain.repository.PaymentRepository
import io.reactivex.Single

class PaymentRespositoryImpl(private val paymentApi: PaymentApi) : PaymentRepository {

    override fun getVoucher(voucherRequest: VoucherRequest): Single<Voucher> {
        return paymentApi.getVoucher(voucherRequest).map { it.data.toVoucher() }
    }


}