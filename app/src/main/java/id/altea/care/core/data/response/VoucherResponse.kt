package id.altea.care.core.data.response


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep
import id.altea.care.core.domain.model.Voucher

@Keep
data class VoucherResponse(
    @SerializedName("discount")
    val discount: PriceResponse?,
    @SerializedName("grand_total")
    val grandTotal: PriceResponse?,
    @SerializedName("total")
    val total: PriceResponse?,
    @SerializedName("voucher_code")
    val voucherCode: String?
) {
    fun toVoucher() : Voucher{
        return Voucher(
            discount?.formatted,
            grandTotal?.formatted,
            total?.formatted,
            voucherCode
        )
    }
}