package id.altea.care.core.domain.repository

import id.altea.care.core.data.request.VoucherRequest
import id.altea.care.core.domain.model.Voucher
import io.reactivex.Single

interface PaymentRepository {

    fun getVoucher(voucherRequest: VoucherRequest) : Single<Voucher>
}