package id.altea.care.view.viewdocument

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.FileUtils
import android.util.Log
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.FileProvider
import androidx.documentfile.provider.DocumentFile

import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import id.altea.care.R
import id.altea.care.core.base.BaseActivity
import id.altea.care.core.ext.*
import id.altea.care.core.helper.util.Constant
import id.altea.care.databinding.ActivityViewDocumentDownloadBinding
import permissions.dispatcher.*
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.lang.Exception
import java.lang.StringBuilder
import javax.inject.Inject

@RuntimePermissions
class ViewDocumentDownloadActivity : BaseActivity<ActivityViewDocumentDownloadBinding>(),HasAndroidInjector {

    @Inject
    lateinit var androidInjector: DispatchingAndroidInjector<Any>

    private var urlPath: String? =""
    private val name by lazy {
        intent.getStringExtra(EXTRA_NAME)
    }

    override fun bindToolbar(): Toolbar? {
        return viewBinding?.viewDocumentToolbar?.toolbar
    }

    override fun enableBackButton(): Boolean {
        return true
    }

    override fun getUiBinding(): ActivityViewDocumentDownloadBinding {
       return ActivityViewDocumentDownloadBinding.inflate(layoutInflater)
    }

    override fun onFirstLaunch(savedInstanceState: Bundle?) {
        urlPath = intent.getStringExtra(EXTRA_URL).orEmpty()
        viewBinding?.viewDocumentToolbar?.viewDocumentBtnDownload?.text = getString(R.string.str_save_title)
        viewBinding?.viewDocumentToolbar?.txtToolbarTitle?.text =getString(R.string.str_view_document)
        viewBinding?.viewDocumentPdf?.fromFile(File(urlPath))?.show()
    }

    @Suppress("DEPRECATION")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        onRequestPermissionsResult(requestCode, grantResults)
    }

    @OnShowRationale(
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE
    )
    fun showRationale(request: PermissionRequest) {
        showRationaleDialog(this, R.string.permission_call_deny, request)
    }

    @OnPermissionDenied(
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE,
    )
    fun onPermissionDenied() {
        doMoveFileWithPermissionCheck()
    }

    @OnNeverAskAgain(
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE
    )
    fun onStorageNeverAskAgain() {
        showPermissionSettingDialog(this, R.string.permission_call_deny)
    }

    override fun initUiListener() {
       viewBinding?.run {
           viewDocumentToolbar.viewDocumentBtnDownload.onSingleClick().subscribe {
               doMoveFileWithPermissionCheck()
           }.disposedBy(disposable)
       }
    }

    @NeedsPermission(
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE
    )
    fun doMoveFile() {
        var input : InputStream? = null
        var out: OutputStream? = null
        try {
            input =  File(urlPath).inputStream()
            val newFileName = File("${Constant.PATH_DOWNLOAD}/${ 
                if(name?.isNotEmpty()  == true) 
                    name?.addStringAtIndex("_${System.currentTimeMillis()}",32) else name }"
            )
            out = FileOutputStream(newFileName)
            val buffer = ByteArray(1024)
            var read: Int
            while (input.read(buffer).also { read = it } != -1) {
                out.write(buffer, 0, read)
            }
            input.close()
            input = null
            out.flush()
            out.close()
            out = null
            showSuccessSnackbar("Berhasil Menyimpan File : ${newFileName} ")
        } catch (e: Exception) {
            Log.i("TAGED", "moveFile: "+e.message)
        }

    }

    override fun androidInjector(): AndroidInjector<Any> {
      return androidInjector
    }

    companion object {
        private const val EXTRA_URL = "ViewDocument.Url"
        private const val EXTRA_NAME = "ViewDocument.Name"

        fun createIntent(
            caller: Context,
            urlPath: String,
            name : String
        ): Intent {
            return Intent(caller, ViewDocumentDownloadActivity::class.java)
                .putExtra(EXTRA_URL, urlPath)
                .putExtra(EXTRA_NAME, name)
        }

    }
}