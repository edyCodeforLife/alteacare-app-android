package id.altea.care.core.data.network.api

import id.altea.care.core.data.request.VoucherRequest
import id.altea.care.core.data.response.Response
import id.altea.care.core.data.response.VoucherResponse
import io.reactivex.Single
import retrofit2.http.Body
import retrofit2.http.POST

interface PaymentApi {
    @POST("/payment/voucher/check")
    fun getVoucher(@Body voucherRequest: VoucherRequest) : Single<Response<VoucherResponse>>
}