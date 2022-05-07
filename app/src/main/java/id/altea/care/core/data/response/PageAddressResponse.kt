package id.altea.care.core.data.response


import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import id.altea.care.core.domain.model.PageAddress

@Keep
data class PageAddressResponse(
    @SerializedName("address") val address: List<UserAddressResponse>?,
    @SerializedName("meta") val meta: MetaResponse?
) {
    companion object {
        fun toPageAddress(response: PageAddressResponse?): PageAddress {
            return PageAddress(
                response?.address?.map { it.toAddress() } ?: listOf(),
                MetaResponse.toMetaPage(response?.meta)
            )
        }
    }
}
