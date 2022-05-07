package id.altea.care.view.forceupdate

import android.content.Intent
import android.net.Uri
import id.altea.care.BuildConfig
import id.altea.care.core.helper.util.Constant.APPLICATION_ID

class ForceUpdateRouter {
    fun openPlayStoreUpdate(source: ForceUpdateActivity){
        try {
            source.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=${APPLICATION_ID}")))
        }catch (e : Exception){
            source.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=${APPLICATION_ID}")))
        }
    }
}