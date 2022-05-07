package id.altea.care.core.domain.model

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class TransactionConsulDetail(
    val bank: String?,
    val expiredAt: String?,
    val paymentUrl: String?,
    val provider: String?,
    val refId: String?,
    val total: Int?,
    val type: String?,
    val vaNumber: String?,
    val detailTransaction : DetailTransaction?
) : Parcelable