package id.altea.care.view.register.transition

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.widget.Toolbar
import id.altea.care.core.base.BaseActivityVM
import id.altea.care.core.ext.disposedBy
import id.altea.care.databinding.ActivitySuccessRegisterBinding
import io.reactivex.Observable
import timber.log.Timber
import java.util.concurrent.TimeUnit

class SuccessRegisterActivity : BaseActivityVM<ActivitySuccessRegisterBinding, SuccesRegisterVM>() {

    private val DEFAULT_DELAY: Long = 2_000
    private val router = SuccessRegisterRouter()

    private val viewModel by viewModels<SuccesRegisterVM> { viewModelFactory }
    private val token by lazy {
        intent?.getStringExtra(EXTRA_TOKEN)
    }

    override fun enableBackButton(): Boolean = false

    override fun bindToolbar(): Toolbar? = null

    override fun getUiBinding(): ActivitySuccessRegisterBinding =
        ActivitySuccessRegisterBinding.inflate(layoutInflater)

    override fun onFirstLaunch(savedInstanceState: Bundle?) {
        Observable.just("DELAY")
            .delay(DEFAULT_DELAY, TimeUnit.MILLISECONDS)
            .subscribe { router.openLogin(this) }
            .disposedBy(disposable)

        Timber.d("token saya -> $token")

        getProfile(token)
    }

    private fun getProfile(token: String?) {
        viewModel.getProfile(token)
    }

    override fun initUiListener() {}

    override fun bindViewModel(): SuccesRegisterVM = viewModel

    override fun observeViewModel(viewModel: SuccesRegisterVM) {}

    companion object {

        const val EXTRA_TOKEN = "EXTRA_TOKEN"

        fun createIntent(
            caller: Context,
            token: String?
        ): Intent {
            return Intent(caller, SuccessRegisterActivity::class.java).apply {
                putExtra(EXTRA_TOKEN, token)
            }
        }
    }

}