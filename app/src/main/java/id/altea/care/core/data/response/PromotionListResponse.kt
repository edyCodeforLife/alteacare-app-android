package id.altea.care.core.data.response

import com.google.gson.annotations.SerializedName

data class PromotionListResponse(
    val allowEdit: Boolean?,
    @SerializedName("id")
    val id: Int?,
    val imageBannerDetail: ImageBannerDetail?,
    @SerializedName("image_banner_thumbnail")
    val imageBannerThumbnail: ImageBannerThumbnail?,
    val merchantName: String?,
    val merchantType: String?,
    @SerializedName("promotion_type")
    val promotionType: String?,
    val status: String?,
    @SerializedName("title")
    val title: String?,
    val voucherCode: String?,
    val weight: Int?
) {
    data class ImageBannerDetail(
        val formats: Formats?,
        val mimeType: String?,
        val sizeFormatted: String?,
        val uploadedBy: UploadedBy?,
        val url: String?
    ) {
        data class Formats(
            val large: String?,
            val medium: String?,
            val small: String?,
            val thumbnail: String?
        )

        data class UploadedBy(
            val firstName: String?,
            val lastName: String?,
            val refId: String?
        )
    }

    data class ImageBannerThumbnail(
        val formats: Formats?,
        val mimeType: String?,
        val sizeFormatted: String?,
        val uploadedBy: UploadedBy?,
        val url: String?
    ) {
        data class Formats(
            val large: String?,
            val medium: String?,
            val small: String?,
            val thumbnail: String?
        )

        data class UploadedBy(
            val firstName: String?,
            val lastName: String?,
            val refId: String?
        )
    }
}