package id.altea.care.core.ext

import android.annotation.SuppressLint
import android.app.Activity
import android.text.Html
import android.text.Spanned
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.jakewharton.rxbinding3.view.clicks
import com.jakewharton.rxbinding3.widget.textChanges
import id.altea.care.R
import id.altea.care.core.helper.validator.EmailValidator
import id.altea.care.core.helper.validator.PhoneValidator
import io.reactivex.Observable
import java.util.concurrent.TimeUnit

fun Button.changeStateButton(state: Boolean) {
    this.isEnabled = state
}

// using throttleFirst for preventing multiple click
fun View.onSingleClick(timeMillis: Long = 600): Observable<Unit> {
    return this.clicks().throttleFirst(timeMillis, TimeUnit.MILLISECONDS)
}

fun TextView.emptyViewValidator(onNext: (View, Boolean) -> Unit): Observable<Boolean> {
    return this.textChanges().map { it.isNotEmpty() }.doOnNext { onNext(this, it) }
}

fun TextView.emailValidator(onNext: (View, Boolean) -> Unit): Observable<Boolean> {
    return this.textChanges()
        .map { it.isNotEmpty() && EmailValidator().isValidEmail(it.toString()) }
        .doOnNext { onNext(this, it) }
}

fun TextView.emailOrPhoneNumberValidator(onNext: (View, Boolean) -> Unit): Observable<Boolean> {
    return this.textChanges()
        .map {  charSequence ->
            charSequence.isNotBlank() && EmailValidator().isValidEmail(charSequence.toString()) || PhoneValidator.isPhoneNumberValid(charSequence.toString())
        }.doOnNext {  boolean ->
            onNext(this, boolean)
        }
}

// see https://github.com/material-components/material-components-android/issues/99
fun LayoutInflater.cloneDefaultTheme(activity: Activity): LayoutInflater {
    val contextThemeWrapper = ContextThemeWrapper(activity, R.style.Theme_Alteaandroid)
    return this.cloneInContext(contextThemeWrapper)
}

fun View.setLayoutParamsHeight(height: Int) {
    val params = layoutParams
    params.height = height
    layoutParams = params
}

@SuppressLint("InlinedApi")
fun String.isHtmlText(): Spanned {
    return Html.fromHtml(this, Html.FROM_HTML_MODE_LEGACY)
}