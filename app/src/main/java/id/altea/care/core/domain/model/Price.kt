package id.altea.care.core.domain.model

import android.os.Parcelable
import id.altea.care.core.data.response.PriceResponse
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Price(
    val formatted: String?,
    val raw: Long?
) : Parcelable {
    companion object {
        fun mapToModel(priceResponse: PriceResponse?): Price {
            return Price(priceResponse?.formatted, priceResponse?.raw)
        }
    }
}
