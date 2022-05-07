package id.altea.care.core.domain.model





data class ImageBannerDetail(
    val formats: Formats?,
    val mimeType: String?,
    val sizeFormatted: String?,
    val uploadedBy: UploadedBy?,
    val url: String?
)