package id.altea.care.core.data.response


import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import id.altea.care.core.domain.model.Banner

@Keep
data class BannerResponse(
    @SerializedName("banner_id")
    val bannerId: String?,
    @SerializedName("deeplink_type_android")
    val deeplinkTypeAndroid: String?,
    @SerializedName("deeplink_type_ios")
    val deeplinkTypeIos: String?,
    @SerializedName("deeplink_url_android")
    val deeplinkUrlAndroid: String?,
    @SerializedName("need_login")
    val needLogin: Boolean?,
    @SerializedName("deeplink_url_ios")
    val deeplinkUrlIos: String?,
    @SerializedName("description")
    val description: String?,
    @SerializedName("image_desktop")
    val imageDesktop: String?,
    @SerializedName("image_mobile")
    val imageMobile: String?,
    @SerializedName("title")
    val title: String?,
    @SerializedName("url_web")
    val urlWeb: String?
) {
    fun toBanner(): Banner {
        return Banner(
            bannerId,
            deeplinkTypeAndroid,
            deeplinkTypeIos,
            deeplinkUrlAndroid,
            needLogin,
            deeplinkUrlIos,
            description,
            imageDesktop,
            imageMobile,
            title,
            urlWeb
        )
    }
}