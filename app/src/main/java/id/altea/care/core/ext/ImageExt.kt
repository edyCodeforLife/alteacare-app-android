package id.altea.care.core.ext

import android.widget.ImageView
import androidx.annotation.DrawableRes
import id.altea.care.R
import id.altea.care.core.di.GlideApp


fun ImageView.loadImage(
    path: String,
    @DrawableRes errorImage: Int = R.drawable.ic_logo_loading,
    @DrawableRes placeholder: Int = R.drawable.ic_img_placeholder
) {
    GlideApp.with(this)
        .load(path)
        .placeholder(placeholder)
        .error(errorImage)
        .into(this)
}
