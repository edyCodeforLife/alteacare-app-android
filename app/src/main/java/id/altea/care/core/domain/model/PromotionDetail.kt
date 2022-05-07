package id.altea.care.core.domain.model


data class PromotionDetail(
    val allowEdit: Boolean?,
    val deactivatedAt: Any?,
    val deeplinkTypeAndroid: String?,
    val deeplinkTypeIos: String?,
    val deeplinkUrlAndroid: String?,
    val deeplinkUrlIos: String?,
    val deeplinkWeb: String?,
    val description: String?,
    val id: Int?,
    val imageBannerDetail: String?,
    val imageBannerDetailId: String?,
    val imageBannerThumbnail: String?,
    val imageBannerThumbnailId: String?,
    val merchantName: String?,
    val merchantType: String?,
    val merchantTypeId: String?,
    val promotionType: String?,
    val promotionTypeId: String?,
    val releasedAt: String?,
    val status: String?,
    val title: String?,
    val voucherCode: String?,
    val voucherCodeId: Any?,
    val weight: Int?
)