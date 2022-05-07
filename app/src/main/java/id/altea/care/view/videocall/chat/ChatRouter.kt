package id.altea.care.view.videocall.chat

import androidx.fragment.app.FragmentActivity
import id.altea.care.view.viewdocument.ViewDocumentActivity

class ChatRouter {
    fun openViewDocument(source: FragmentActivity, urlPath : String){
        source.startActivity(ViewDocumentActivity.createIntent(source.applicationContext,urlPath))
    }
}