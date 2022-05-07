package id.altea.care.view.viewdocument

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import id.altea.care.core.base.BaseActivityVM
import id.altea.care.core.ext.disposedBy
import id.altea.care.core.ext.loadImage
import id.altea.care.core.ext.onSingleClick
import id.altea.care.databinding.ActivityViewDocumentBinding
import id.altea.care.view.consultationdetail.ConsultationDetailSharedVM

class ViewDocumentActivity :
    BaseActivityVM<ActivityViewDocumentBinding, ConsultationDetailSharedVM>() {
    private lateinit var urlPath: String

    override fun observeViewModel(viewModel: ConsultationDetailSharedVM) {

    }

    override fun bindViewModel(): ConsultationDetailSharedVM {
        return ViewModelProvider(
            this.viewModelStore,
            viewModelFactory
        ).get(ConsultationDetailSharedVM::class.java)
    }

    override fun getUiBinding(): ActivityViewDocumentBinding =
        ActivityViewDocumentBinding.inflate(layoutInflater)


    override fun onFirstLaunch(savedInstanceState: Bundle?) {
        urlPath = intent.getStringExtra(EXTRA_URL).orEmpty()

        viewBinding?.run {
            urlPath.let {

                viewDocumentImage.isVisible = true
                viewDocumentImage.loadImage(urlPath)
            }
        }
    }

    override fun enableBackButton(): Boolean = false

    override fun bindToolbar(): Toolbar? = null

    override fun initUiListener() {
        viewBinding?.run {
            viewDocumentBtnClose.onSingleClick().subscribe {
                finish()
            }.disposedBy(disposable)
        }

    }

    override fun onBackPressed() {
        finish()
        super.onBackPressed()
    }

    companion object {
        private const val EXTRA_URL = "ViewDocument.Url"
        private const val EXTRA_TYPE_FILE = "ViewDocument.TypeFile"

        fun createIntent(
            caller: Context,
            urlPath: String
        ): Intent {
            return Intent(caller, ViewDocumentActivity::class.java)
                .putExtra(EXTRA_URL, urlPath)
        }

    }
}