package id.altea.care.view.forceupdate

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import id.altea.care.core.base.BaseActivityVM
import id.altea.care.core.ext.disposedBy
import id.altea.care.core.ext.onSingleClick
import id.altea.care.databinding.ActivityForceUpdateBinding
import id.altea.care.view.main.MainActivity

class ForceUpdateActivity : BaseActivityVM<ActivityForceUpdateBinding,ForceUpdateVM>() {

    val router = ForceUpdateRouter()

    override fun enableBackButton(): Boolean = false

    override fun bindToolbar(): Toolbar? = null

    override fun getUiBinding(): ActivityForceUpdateBinding {
        return ActivityForceUpdateBinding.inflate(layoutInflater)
    }

    override fun onFirstLaunch(savedInstanceState: Bundle?) {

    }

    override fun onBackPressed() {

    }

    override fun initUiListener() {
        viewBinding?.run {
            forceUpdateButton.onSingleClick().subscribe {
                router.openPlayStoreUpdate(this@ForceUpdateActivity)
            }.disposedBy(disposable)
        }
    }

    override fun observeViewModel(viewModel: ForceUpdateVM) {

    }

    override fun bindViewModel(): ForceUpdateVM {
        return ViewModelProvider(this, viewModelFactory)[ForceUpdateVM::class.java]
    }

    companion object {
        fun createIntent(caller: Context): Intent {
            return Intent(caller, ForceUpdateActivity::class.java)

        }
    }
}