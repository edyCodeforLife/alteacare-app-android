package id.altea.care.view.consultationdetail.consultationcost

import androidx.fragment.app.FragmentActivity

import id.altea.care.view.viewdocument.ViewDocumentActivity
import id.altea.care.view.viewdocument.ViewDocumentDownloadActivity

class ConsultationCostRouter {
    fun openViewDocument(source: FragmentActivity, urlPath : String,name : String) {
        source.startActivity(ViewDocumentDownloadActivity.createIntent(source,urlPath,name))
    }

}