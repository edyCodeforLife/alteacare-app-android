package id.altea.care.core.ext

import android.graphics.drawable.Drawable
import android.util.TypedValue
import android.widget.TextView
import androidx.annotation.DrawableRes
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.jakewharton.rxbinding3.widget.textChanges
import id.altea.care.R
import id.altea.care.core.di.GlideApp
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import timber.log.Timber

fun TextView.loadImage(path: String, @DrawableRes errorImage: Int = R.drawable.ic_logo_loading) {
    GlideApp.with(this).load(path).placeholder(R.drawable.ic_img_placeholder)
        .error(errorImage).apply(RequestOptions().fitCenter()).into(
        object : CustomTarget<Drawable>(200,200){
            override fun onLoadCleared(placeholder: Drawable?) {
                this@loadImage.setCompoundDrawablesWithIntrinsicBounds(placeholder, null, null, null)
            }

            override fun onResourceReady(
                resource: Drawable,
                transition: Transition<in Drawable>?
            ) {
                this@loadImage.setCompoundDrawablesWithIntrinsicBounds(resource, null, null, null)
            }
        }
    )
}

fun TextView.autoSize(composite: CompositeDisposable? = null, minSize: Float? = null, minLineCount : Int? = 0) {
    val disposable = textChanges()
        .observeOn(AndroidSchedulers.mainThread())
        .map { lineCount }
        .subscribe({  txtLine ->
            Timber.d("text line is $txtLine \n text size is ${this.textSize}")
            if (txtLine > minLineCount ?: 10) {
                setTextSize(TypedValue.COMPLEX_UNIT_SP, minSize ?: 12f)
            }
        }, {throwable ->
            throwable.printStackTrace()
        })

    composite?.add(disposable)
}