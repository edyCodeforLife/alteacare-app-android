package id.altea.care.core.data.response

import com.google.gson.annotations.SerializedName

data class PromotionListGroupResponse(
    @SerializedName("promotion_list")
    val promotionList: List<Promotion?>?,
    @SerializedName("promotion_type")
    val promotionType: String?,
    @SerializedName("promotion_type_id")
    val promotionTypeId: String?
) {
    data class Promotion(
        @SerializedName("id")
        val id: Int?,
        @SerializedName("image_banner_thumbnail")
        val imageBannerThumbnail: ImageBannerThumbnail?,
        @SerializedName("promotion_type")
        val promotionType: String?,
        val promotionTypeId: String?,
        val title: String?,
        val weight: Int?
    ) {
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
}