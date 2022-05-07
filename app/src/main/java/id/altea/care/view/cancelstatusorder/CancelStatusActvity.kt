package id.altea.care.view.cancelstatusorder

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import dagger.android.AndroidInjection
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import id.altea.care.R
import id.altea.care.core.base.BaseActivity
import id.altea.care.core.ext.disposedBy
import id.altea.care.core.ext.onSingleClick
import id.altea.care.core.ext.showDialogBackConfirmationPayment
import id.altea.care.databinding.ActivityCancelStatusBinding
import id.altea.care.view.endcall.EndCallActivity
import javax.inject.Inject

class CancelStatusActvity : BaseActivity<ActivityCancelStatusBinding>(), HasAndroidInjector {
    @Inject
    lateinit var androidInjector: DispatchingAndroidInjector<Any>

    val router = CancelStatusRouter()

    private val statusCancel by lazy {
        intent.getBooleanExtra(STATUS_CANCEL,false)
    }

    override fun bindToolbar(): Toolbar?  = null

    override fun enableBackButton(): Boolean  = false

    override fun getUiBinding(): ActivityCancelStatusBinding = ActivityCancelStatusBinding.inflate(layoutInflater)

    override fun onFirstLaunch(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        viewBinding?.run {
            if (statusCancel == true){
                showSuccessSnackbar(getString(R.string.str_cancel_call_success),true)
                cancelStatusBtnMyConsultation.onSingleClick().subscribe {
                    router.openMainActivity(this@CancelStatusActvity,2)
                }.disposedBy(disposable)
            }else{
                cancelStatusBtnMyConsultation.onSingleClick().subscribe {
                    router.openMainActivity(this@CancelStatusActvity)
                }.disposedBy(disposable)
                showErrorSnackbar(getString(R.string.str_cancel_failed),true)
                cancelStatusText.text = getString(R.string.str_failed_to_cancel)
                cancelStatusText.setTextColor(getColor(R.color.redLighter))
            }
        }
    }

    override fun initUiListener() {
        viewBinding?.run {

        }
    }

    override fun onBackPressed() {
        showDialogBackConfirmationPayment(this,R.string.str_back_myconsultation) {
            router.openMainActivity(this)
        }
    }

    override fun androidInjector(): AndroidInjector<Any> = androidInjector

    companion object {
        private const val STATUS_CANCEL = "STATUS_CANCEL"

        fun createIntent(
            caller: Context,
            statusCancel : Boolean?
        ): Intent {
            return Intent(caller, CancelStatusActvity::class.java)
                .putExtra(STATUS_CANCEL, statusCancel)
        }
    }
}