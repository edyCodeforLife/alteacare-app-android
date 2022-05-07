package id.altea.care.view.payment.paymentinformation

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import id.altea.care.R
import id.altea.care.core.base.BaseActivityVM
import id.altea.care.core.domain.model.ConsultationDetail
import id.altea.care.core.ext.disposedBy
import id.altea.care.core.ext.observe
import id.altea.care.core.ext.onSingleClick
import id.altea.care.core.ext.showDialogBackConfirmationPayment
import id.altea.care.databinding.ActivityPaymentInformationBinding
import id.altea.care.view.common.enums.TypeAppointment
import id.altea.care.view.webview.WebAppInterface
import kotlinx.android.synthetic.main.dialog_delete_img_profile.view.*
import kotlinx.android.synthetic.main.dialog_payment_information.view.*
import kotlinx.android.synthetic.main.line_steper_consultation.*
import timber.log.Timber
import java.net.URISyntaxException

class PaymentInformationActivity :
    BaseActivityVM<ActivityPaymentInformationBinding, PaymentInfoVM>() {

    private val appointmentId by lazy { intent.getIntExtra(EXTRA_APPOINTMENT_ID, 0) }
    private val viewModel by viewModels<PaymentInfoVM> { viewModelFactory }

    private val router = PaymentInformationRouter()

    override fun enableBackButton(): Boolean = false

    override fun bindToolbar(): Toolbar? = viewBinding?.appbar?.toolbar

    override fun getUiBinding(): ActivityPaymentInformationBinding {
        return ActivityPaymentInformationBinding.inflate(layoutInflater)
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onFirstLaunch(savedInstanceState: Bundle?) {
        viewBinding?.run {

            appbar.txtToolbarTitle.text = getString(R.string.payment_string)
            step1.text = getString(R.string.str_step_three)
            textStep1.text = getString(R.string.str_confirmation)
            step2.text = getString(R.string.str_step_four)
            textStep2.text = getString(R.string.str_payment)
            step3.text = getString(R.string.str_step_five)
            textStep3.text = getString(R.string.str_consultation)

            paymentInfoWebView.apply {
                val webViewSetting = settings
                webViewSetting.javaScriptEnabled = true
                webViewSetting.javaScriptCanOpenWindowsAutomatically = true
                webViewSetting.domStorageEnabled = true
                webViewSetting.useWideViewPort = true
                webViewSetting.loadWithOverviewMode = true
                webViewSetting.builtInZoomControls = true
                webViewSetting.setSupportZoom(true)
                webViewSetting.defaultTextEncodingName = "utf-8"

                addJavascriptInterface(WebAppInterface(context), "NativeAndroid")
                webChromeClient = WebChromeClient()
                webViewClient = object : WebViewClient() {
                    override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                        if (url?.startsWith("intent://") == true) {
                            try {
                                val intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME)
                                if (intent != null) {
                                    view?.stopLoading()
                                    val packageManager = context.packageManager
                                    val info = packageManager.resolveActivity(
                                        intent,
                                        PackageManager.MATCH_DEFAULT_ONLY
                                    )
                                    if (info != null) {
                                        context.startActivity(intent)
                                    } else {
                                        val fallbackUrl =
                                            intent.getStringExtra("browser_fallback_url")
                                        val browserIntent =
                                            Intent(Intent.ACTION_VIEW, Uri.parse(fallbackUrl))
                                        context.startActivity(browserIntent)
                                    }
                                    return true
                                }
                            } catch (e: URISyntaxException) {
                                Timber.e(e)
                            }
                        } else if (url?.startsWith("http") == true) {
                            val browserIntent =
                                Intent(Intent.ACTION_VIEW, Uri.parse(url))
                            context.startActivity(browserIntent)
                            return true
                        }
                        return false
                    }
                }
            }.run {
                intent?.getStringExtra(EXTRA_PAYMENT_URL)?.let {
                    loadUrl(it)
                }
            }
        }
    }

    override fun initUiListener() {
        viewBinding?.run {
            paymentInfoBtnCheck
                .onSingleClick()
                .subscribe {
                    viewModel.getPaymentCheck(appointmentId)
                }
                .disposedBy(disposable)
        }
    }

    override fun onBackPressed() {
        showDialogBackConfirmationPayment(this) {
            router.openHome(this)
        }
        return
    }

    companion object {
        private const val EXTRA_APPOINTMENT_ID = "PaymentInformation.appointmentId"
        private const val EXTRA_PAYMENT_URL = "PaymentInformation.Url"
        private const val EXTRA_ORDER_CODE = "PaymentSuccess.OrderCode"
        fun createIntent(
            caller: AppCompatActivity,
            appointmentId: Int,
            urlPayment: String,
            orderCode: String
        ): Intent {
            return Intent(caller, PaymentInformationActivity::class.java)
                .putExtra(EXTRA_PAYMENT_URL, urlPayment)
                .putExtra(EXTRA_APPOINTMENT_ID, appointmentId)
                .putExtra(EXTRA_ORDER_CODE, orderCode)
        }
    }

    override fun observeViewModel(viewModel: PaymentInfoVM) {
        observe(viewModel.failureLiveData, ::handleFailure)
        observe(viewModel.isLoadingLiveData, ::handleLoadingBar)
        observe(viewModel.detailAppointmentEvent, ::handleEventAppointmentDetail)
    }

    private fun handleEventAppointmentDetail(consultationDetail: ConsultationDetail?) {
        consultationDetail?.let {
            when {
                it.status.equals(TypeAppointment.PAID.name, true) -> {
                    router.openPaymentSuccess(this@PaymentInformationActivity, appointmentId)
                }
                it.status.equals(TypeAppointment.WAITING_FOR_PAYMENT.name,true) -> {
                    showDialogPaymentInformation()
                }
            }
        }
    }

    private fun showDialogPaymentInformation() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_payment_information, null)
        val alertDialogView = AlertDialog.Builder(this).create()
        alertDialogView.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alertDialogView.setView(dialogView)

        dialogView.btnClose.onSingleClick()
            .subscribe {
                alertDialogView.dismiss()
            }.disposedBy(disposable)
        alertDialogView.show()
    }


    private fun handleLoadingBar(showLoading: Boolean?) {
        viewBinding?.progress?.isVisible = showLoading == true
    }

    override fun bindViewModel(): PaymentInfoVM = viewModel
}
