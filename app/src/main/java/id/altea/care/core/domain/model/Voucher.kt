package id.altea.care.core.domain.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Voucher( val discount: String?,
                    val grandTotal: String?,
                    val total: String?,
                    val voucherCode: String?) : Parcelable