package id.altea.care.view.deeplink

import android.app.Activity
import android.net.Uri

interface DeepLinkManager {

    fun setDeepLinkUri(uri: Uri)
    fun getDeepLinkUri(): Uri?
    fun executeDeepLink(source: Activity)
    fun executeDeepLink(uri: Uri?, source: Activity)
    fun invalidate()

}
