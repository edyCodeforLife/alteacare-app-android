package id.altea.care.view.account.termandcondition

import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import id.altea.care.core.base.BaseActivityVM
import id.altea.care.core.domain.model.Block
import id.altea.care.core.ext.disposedBy
import id.altea.care.core.ext.observe
import id.altea.care.core.ext.onSingleClick
import id.altea.care.databinding.ActivityTermConditionAccountBinding

class TermConditionAccount : BaseActivityVM<ActivityTermConditionAccountBinding,TermAndConditionAccountVM>() {


    private val router = TermConditionAccountRouter()

    override fun enableBackButton(): Boolean = false

    override fun bindToolbar(): Toolbar? = null

    override fun getUiBinding(): ActivityTermConditionAccountBinding {
        return ActivityTermConditionAccountBinding.inflate(layoutInflater)
    }

    override fun onFirstLaunch(savedInstanceState: Bundle?) {
        baseViewModel?.getTermAndCondition()
    }

    override fun initUiListener() {
        viewBinding?.run {
            termBtnApprove.onSingleClick()
                .subscribe { finish() }
                .disposedBy(disposable)
        }
    }

    override fun observeViewModel(viewModel: TermAndConditionAccountVM) {
        observe(viewModel.failureLiveData, ::handleFailure)
        observe(viewModel.isLoadingLiveData, ::handleLoading)
        observe(viewModel.blockEvent, ::handleTermAndCondition)
    }

    override fun bindViewModel(): TermAndConditionAccountVM {
        return ViewModelProvider(this, viewModelFactory)[TermAndConditionAccountVM::class.java]
    }

    private fun handleTermAndCondition(list: List<Block>?) {
        viewBinding?.run {
            termWebView.loadData(list?.get(0)?.text!!, "text/html", "UTF-8")
        }
    }
}