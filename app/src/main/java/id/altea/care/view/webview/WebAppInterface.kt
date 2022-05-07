package id.altea.care.view.webview

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.webkit.JavascriptInterface

class WebAppInterface(val context: Context) {
    @JavascriptInterface
    fun copyToClipboard(text: String?) {
        val clipboard: ClipboardManager? =
            context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager?
        val clip = ClipData.newPlainText("demo", text)
        clipboard?.setPrimaryClip(clip)
    }
}