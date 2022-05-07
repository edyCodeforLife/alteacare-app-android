package id.altea.care.core.data.response

import com.google.gson.annotations.SerializedName
import id.altea.care.core.domain.model.Meta
import id.altea.care.core.domain.model.MetaPage

data class MetaResponse(
    @SerializedName("limit") val limit: Int?,
    @SerializedName("page") val page: Int?,
    @SerializedName("total_data") val totalData: Int?,
    @SerializedName("total_page") val totalPage: Int?,
    @SerializedName("total") val total: Int?,
    @SerializedName("total_available") val totalAvailable: Int?,
    @SerializedName("total_unavailable") val totalUnavailable: Int?,

    ) {
    companion object {
        fun toMetaPage(metaResponse: MetaResponse?): MetaPage {
            return MetaPage(
                metaResponse?.limit,
                metaResponse?.page,
                metaResponse?.totalData,
                metaResponse?.totalPage
            )
        }

        fun toMeta(metaResponse: MetaResponse?): Meta {
            return Meta(
                metaResponse?.total,
                metaResponse?.totalAvailable,
                metaResponse?.totalUnavailable
            )
        }
    }
}
