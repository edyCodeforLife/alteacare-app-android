package id.altea.care.view.account.setting

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.widget.Toolbar
import id.altea.care.R
import id.altea.care.core.base.BaseActivityVM
import id.altea.care.core.ext.onSingleClick
import id.altea.care.databinding.ActivityAccountSettingBinding
import id.altea.care.view.account.changeprofile.ChangeProfileVM
import kotlinx.android.synthetic.main.activity_account_setting.*
import kotlinx.android.synthetic.main.toolbar_default_center.view.*

class SettingAccountActivity : BaseActivityVM<ActivityAccountSettingBinding,SettingAccountVM>() {
    private val viewModel by viewModels<SettingAccountVM> { viewModelFactory }
    private val router  = SettingAccountRouter()
    override fun observeViewModel(viewModel: SettingAccountVM) {

    }

    override fun bindViewModel(): SettingAccountVM  = viewModel

    override fun enableBackButton(): Boolean = true

    override fun bindToolbar(): Toolbar? = viewBinding?.appbar?.toolbar

    override fun getUiBinding(): ActivityAccountSettingBinding = ActivityAccountSettingBinding.inflate(layoutInflater)

    override fun onFirstLaunch(savedInstanceState: Bundle?) {
        viewBinding.run {
            appbar.txtToolbarTitle.text = getString(R.string.str_setting_account)
        }
    }

    override fun initUiListener() {
        viewBinding.run {
            accountBtnChangePassword.onSingleClick().subscribe {
                router.openChangePassword(this@SettingAccountActivity)
            }
        }
    }


}