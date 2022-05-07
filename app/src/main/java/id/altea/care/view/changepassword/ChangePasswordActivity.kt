package id.altea.care.view.changepassword

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.widget.Toolbar
import com.jakewharton.rxbinding3.widget.textChanges
import id.altea.care.R
import id.altea.care.core.base.BaseActivityVM
import id.altea.care.core.domain.model.RegisterInfo
import id.altea.care.core.domain.model.ResultFilter
import id.altea.care.core.ext.changeStateButton
import id.altea.care.core.ext.disposedBy
import id.altea.care.core.ext.observe
import id.altea.care.core.ext.onSingleClick
import id.altea.care.databinding.ActivityChangePasswordBinding
import id.altea.care.view.common.enums.Gender
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.activity_account_setting.*
import kotlinx.android.synthetic.main.activity_change_password.*
import kotlinx.android.synthetic.main.activity_change_password.appbar
import kotlinx.android.synthetic.main.toolbar_default_center.view.*
import java.util.concurrent.TimeUnit


class ChangePasswordActivity : BaseActivityVM<ActivityChangePasswordBinding,ChangePasswordVM>() {

    private val viewModel by viewModels<ChangePasswordVM> { viewModelFactory }
    private val router = ChangePasswordRouter()

    override fun observeViewModel(viewModel: ChangePasswordVM) {
        observe(viewModel.failureLiveData, ::handleFailure)
        observe(viewModel.isLoadingLiveData, ::handleLoading)
        observe(viewModel.resultCheckPasswordLiveData, :: getResultCheckPassword)
    }

    override fun bindViewModel(): ChangePasswordVM = viewModel

    override fun enableBackButton(): Boolean  = true

    override fun bindToolbar(): Toolbar?  =  viewBinding?.appbar?.toolbar

    override fun getUiBinding(): ActivityChangePasswordBinding  = ActivityChangePasswordBinding.inflate(layoutInflater)

    override fun onFirstLaunch(savedInstanceState: Bundle?) {
        viewBinding.run {
            appbar.txtToolbarTitle.text = getString(R.string.str_change_password)
        }
    }

    override fun initUiListener() {
        viewBinding.run {
            changePasswordEdtxPassword.textChanges()
                .debounce(300,TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .map {
                    it.isNotEmpty()
                }
                .subscribe {
                    changePasswordBtn.changeStateButton(it)
                }.disposedBy(disposable)
            changePasswordBtn.onSingleClick().subscribe {
                viewModel.checkPassword(changePasswordEdtxPassword.text.toString())
            }
        }

    }

    private fun getResultCheckPassword(result : Boolean?) {
        if (result == true) {
            router.openPasswordActivity(this@ChangePasswordActivity, RegisterInfo("","","",Gender.MALE,
                ResultFilter("","",""),""))
        }
    }
}