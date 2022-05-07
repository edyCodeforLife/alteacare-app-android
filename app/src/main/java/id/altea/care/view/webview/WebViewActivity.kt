package id.altea.care.view.webview

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.webkit.GeolocationPermissions
import android.webkit.WebChromeClient
import android.webkit.WebView
import androidx.appcompat.widget.Toolbar
import id.altea.care.R
import id.altea.care.core.base.BaseActivity
import id.altea.care.core.ext.showDialogBackConfirmationPayment
import id.altea.care.core.helper.util.AdvancedWebView
import id.altea.care.core.helper.util.Constant.COOKIE_DRUG_STORE
import id.altea.care.core.helper.util.Constant.URL_CLOSE_DRUG_STORE
import id.altea.care.core.helper.util.Constant.URL_CLOSE_VACCINE
import id.altea.care.databinding.ActivityWebviewBinding
import permissions.dispatcher.NeedsPermission
import permissions.dispatcher.RuntimePermissions

@RuntimePermissions
class WebViewActivity : BaseActivity<ActivityWebviewBinding>() {

    override fun enableBackButton(): Boolean = false

    override fun bindToolbar(): Toolbar? = null

    override fun getUiBinding(): ActivityWebviewBinding =
        ActivityWebviewBinding.inflate(layoutInflater)

    private val webUrl by lazy {
        intent?.getStringExtra(EXTRA_URL)
    }

    private val cookie by lazy {
        intent?.getStringExtra(EXTRA_COOKIE)
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onFirstLaunch(savedInstanceState: Bundle?) {
        viewBinding?.vaccineInfoWebView?.clearCache(true)
        viewBinding?.vaccineInfoWebView?.run {
            if (!cookie.isNullOrEmpty()) {
                setCookiesEnabled(true)
                setGeolocationEnabled(true)
                setCookie(webUrl, "$COOKIE_DRUG_STORE=$cookie")
            }
            addJavascriptInterface(WebAppInterface(context), "NativeAndroid")
            setMixedContentAllowed(false)
            settings.javaScriptEnabled = true
            webUrl?.let { loadUrl(it) }
        }
    }

    override fun initUiListener() {
        viewBinding?.vaccineInfoWebView?.setListener(this,object : AdvancedWebView.Listener {
            override fun onExternalPageRequest(url: String?) {}

            override fun onPageStarted(url: String?, favicon: Bitmap?) {}

            override fun onPageFinished(url: String?) {}

            override fun onDownloadRequested(
                url: String?,
                suggestedFilename: String?,
                mimeType: String?,
                contentLength: Long,
                contentDisposition: String?,
                userAgent: String?
            ) {}

            override fun onPageError(
                errorCode: Int,
                description: String?,
                failingUrl: String?
            ) {
                showToast(getString(R.string.general_error_item_retry))
                finish()
            }

            override fun doUpdateVisitedHistory(
                view: WebView?,
                url: String?,
                isReload: Boolean
            ) {
                if (url?.contains(URL_CLOSE_VACCINE) == true || url?.contains(URL_CLOSE_DRUG_STORE) == true) {
                    finish()
                }

                if (url?.contains("address/set-location") == true ){
                    connectMapsWithPermissionCheck()
                }
            }
        })
    }

    override fun onResume() {
        super.onResume()
        viewBinding?.vaccineInfoWebView?.onResume()
    }

    override fun onPause() {
        viewBinding?.vaccineInfoWebView?.onPause()
        super.onPause()
    }

    override fun onDestroy() {
        viewBinding?.vaccineInfoWebView?.onDestroy()
        super.onDestroy()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)
        viewBinding?.vaccineInfoWebView?.onActivityResult(requestCode, resultCode, intent)
    }

    override fun onBackPressed() {
        viewBinding?.vaccineInfoWebView?.run {
            onBackPressed()
            if (url?.contains("/vaccine?email") == true || url?.contains(URL_CLOSE_DRUG_STORE) == true) {
                showDialogBackConfirmationPayment(this@WebViewActivity, R.string.str_back_vaccine) {
                    super.onBackPressed()
                }
                return
            }
        }
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

    @NeedsPermission(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION
    )fun connectMaps(){
        viewBinding?.vaccineInfoWebView?.run {
            webChromeClient = object : WebChromeClient() {
                override fun onGeolocationPermissionsShowPrompt(
                    origin: String?,
                    callback: GeolocationPermissions.Callback?
                ) {
                    callback?.invoke(origin, true, false)
                }
            }
        }
    }

    companion object {
        private const val EXTRA_URL = "Url"
        private const val EXTRA_COOKIE = "Cookie"
        fun createIntent(
            caller: Context,
            urlVaccine: String,
            cookie : String? = null
        ): Intent {
            return Intent(caller, WebViewActivity::class.java)
                .putExtra(EXTRA_URL, urlVaccine)
                .putExtra(EXTRA_COOKIE, cookie)
        }
    }
}