package id.altea.care.core.data.response


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep
import id.altea.care.core.domain.model.PromotionDetail

@Keep
data class PromotionDetailResponse(
    @SerializedName("allow_edit")
    val allowEdit: Boolean?,
    @SerializedName("deactivated_at")
    val deactivatedAt: Any?,
    @SerializedName("deeplink_type_android")
    val deeplinkTypeAndroid : String?,
    @SerializedName("deeplink_type_ios")
    val deeplinkTypeIos: String?,
    @SerializedName("deeplink_url_android")
    val deeplinkUrlAndroid: String?,
    @SerializedName("deeplink_url_ios")
    val deeplinkUrlIos: String?,
    @SerializedName("deeplink_web")
    val deeplinkWeb: String?,
    @SerializedName("description")
    val description: String?,
    @SerializedName("id")
    val id: Int?,
    @SerializedName("image_banner_detail")
    val imageBannerDetailResponse : ImageBannerDetailResponse?,
    @SerializedName("image_banner_detail_id")
    val imageBannerDetailId: String?,
    @SerializedName("image_banner_thumbnail")
    val imageBannerThumbnailResponse : ImageBannerThumbnailResponse?,
    @SerializedName("image_banner_thumbnail_id")
    val imageBannerThumbnailId: String?,
    @SerializedName("merchant_name")
    val merchantName: String?,
    @SerializedName("merchant_type")
    val merchantType: String?,
    @SerializedName("merchant_type_id")
    val merchantTypeId: String?,
    @SerializedName("promotion_type")
    val promotionType: String?,
    @SerializedName("promotion_type_id")
    val promotionTypeId: String?,
    @SerializedName("released_at")
    val releasedAt: String?,
    @SerializedName("status")
    val status: String?,
    @SerializedName("title")
    val title: String?,
    @SerializedName("voucher_code")
    val voucherCode: String?,
    @SerializedName("voucher_code_id")
    val voucherCodeId: Any?,
    @SerializedName("weight")
    val weight: Int?
){
    fun toPromotionDetail() : PromotionDetail{
        return PromotionDetail(
            allowEdit = allowEdit,
            deactivatedAt = deactivatedAt,
            deeplinkTypeAndroid = deeplinkTypeAndroid,
            deeplinkUrlAndroid = deeplinkUrlAndroid,
            deeplinkTypeIos = deeplinkTypeIos,
            deeplinkUrlIos = deeplinkUrlIos,
            deeplinkWeb = deeplinkWeb,
            description = description,
            id = id,
            imageBannerDetail = imageBannerDetailResponse?.formats?.small,
            imageBannerDetailId = imageBannerDetailId,
            imageBannerThumbnail = imageBannerThumbnailResponse?.formats?.small,
            imageBannerThumbnailId = imageBannerThumbnailId,
            merchantName = merchantName,
            merchantType = merchantType,
            merchantTypeId = merchantTypeId,
            promotionType = promotionType,
            promotionTypeId = promotionTypeId,
            releasedAt = releasedAt,
            status = status,
            title = title,
            voucherCode = voucherCode,
            voucherCodeId = voucherCodeId,
            weight = weight
        )
    }
}