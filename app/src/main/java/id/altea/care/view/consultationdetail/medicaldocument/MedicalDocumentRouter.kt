package id.altea.care.view.consultationdetail.medicaldocument

import androidx.fragment.app.FragmentActivity
import id.altea.care.view.viewdocument.ViewDocumentActivity

class MedicalDocumentRouter {

    fun openViewDocument(source: FragmentActivity, urlPath : String){
        source.startActivity(ViewDocumentActivity.createIntent(source.applicationContext,urlPath))
    }
}