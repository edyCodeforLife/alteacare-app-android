package id.altea.care.view.register.termcondition

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import com.jakewharton.rxbinding3.widget.checkedChanges
import id.altea.care.core.base.BaseActivityVM
import id.altea.care.core.domain.model.Auth
import id.altea.care.core.domain.model.Block
import id.altea.care.core.domain.model.RegisterInfo
import id.altea.care.core.ext.changeStateButton
import id.altea.care.core.ext.disposedBy
import id.altea.care.core.ext.observe
import id.altea.care.core.ext.onSingleClick
import id.altea.care.databinding.ActivityTermConditionBinding

/**
 * Created by trileksono on 08/03/21.
 */
class TermConditionActivity : BaseActivityVM<ActivityTermConditionBinding, TermConditionVM>() {

    private val registerInfo: RegisterInfo by lazy { intent.getParcelableExtra(EXTRA_REGISTER_INFO)!! }
    private val router = TermConditionRouter()

    override fun enableBackButton(): Boolean = false

    override fun bindToolbar(): Toolbar? = null

    override fun getUiBinding(): ActivityTermConditionBinding {
        return ActivityTermConditionBinding.inflate(layoutInflater)
    }

    override fun onFirstLaunch(savedInstanceState: Bundle?) {
        baseViewModel?.getTermAndCondition()
    }

    override fun initUiListener() {
        viewBinding?.run {
            termCheckboxAccept.checkedChanges()
                .subscribe { termBtnNext.changeStateButton(it) }
                .disposedBy(disposable)

            termBtnNext.onSingleClick()
                .subscribe { baseViewModel?.doRegister(registerInfo) }
                .disposedBy(disposable)
        }
    }

    override fun observeViewModel(viewModel: TermConditionVM) {
        observe(viewModel.failureLiveData, ::handleFailure)
        observe(viewModel.isLoadingLiveData, ::handleLoading)
        observe(viewModel.authEvent, ::handleSuccessRegister)
        observe(viewModel.blockEvent, ::handleTermAndCondition)
    }

    private fun handleTermAndCondition(list: List<Block>?) {
        viewBinding?.run {
            termWebView.loadData(list?.get(0)?.text!!, "text/html", "UTF-8")
        }
    }

    override fun bindViewModel(): TermConditionVM {
        return ViewModelProvider(this, viewModelFactory)[TermConditionVM::class.java]
    }

    private fun handleSuccessRegister(auth: Auth?) {
        auth?.let { router.openSmsOtp(this, registerInfo.newPhoneNumber, registerInfo.email!!, it.accessToken) }
    }

    companion object {
        private const val EXTRA_REGISTER_INFO = "TermCond.RegisterInfo"
        fun createIntent(caller: Context, registerInfo: RegisterInfo): Intent {
            return Intent(caller, TermConditionActivity::class.java)
                .putExtra(EXTRA_REGISTER_INFO, registerInfo)
        }
    }

    enum class BlockType {
        TERMS_CONDITION, PRIVACY_POLICY ,TERM_REFUND_CANCEL,PAYMENT_GUIDE_BCA_VA
    }
}
