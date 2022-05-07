package id.altea.care.view.splashscreen

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import dagger.android.AndroidInjection
import id.altea.care.core.base.BaseActivityVM
import id.altea.care.core.domain.cache.OnboardingCache
import id.altea.care.core.domain.model.CheckApp
import id.altea.care.core.ext.disposedBy
import id.altea.care.core.ext.observe
import id.altea.care.databinding.ActivitySplashBinding
import id.altea.care.view.deeplink.DeepLinkManager
import io.reactivex.Observable
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * Created by trileksono on 12/03/21.
 */
class SplashActivity : BaseActivityVM<ActivitySplashBinding,SplashVM>() {


    @Inject
    lateinit var deepLinkManager: DeepLinkManager

    @Inject
    lateinit var onboardingCache: OnboardingCache

    private val DEFAULT_DELAY: Long = 1_000
    private val router = SplashRouter()

    override fun enableBackButton(): Boolean = false

    override fun bindToolbar(): Toolbar? = null

    override fun getUiBinding(): ActivitySplashBinding =
        ActivitySplashBinding.inflate(layoutInflater)

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onFirstLaunch(savedInstanceState: Bundle?) {
       baseViewModel?.getApplicationVersion()
    }

    // if splash run from deeplink, save deeplink
    private fun saveDeepLinkIfAny() {
        intent?.data?.let { deepLinkManager.setDeepLinkUri(it) }
    }

    override fun initUiListener() {}


    override fun observeViewModel(viewModel: SplashVM) {
        observe(viewModel.failureLiveData,::handleFailure)
        observe(viewModel.appVersionEvent,::handleAppVersion)
    }

    private fun handleAppVersion(checkApp: CheckApp?) {
        checkApp?.let {
            if (it.isUpdateRequired == true){
                router.openForceUpdate(this)
            }else if(onboardingCache.getIsOnBoarding() == false){
                router.openOnBoarding(this)
            }else{
                Observable.just("DELAY")
                    .delay(DEFAULT_DELAY, TimeUnit.MILLISECONDS)
                    .subscribe {
                        saveDeepLinkIfAny()
//                        deepLinkManager.setDeepLinkUri(Uri.parse("alteacare://doctor/list?specializationIds=607d01ca3925ca001231fae3&hospitalIds=60824324e955130012728faa"))
                        router.openMainScreen(this)
                    }
                    .disposedBy(disposable)
            }
        }
    }

    override fun bindViewModel(): SplashVM {
       return ViewModelProvider(this, viewModelFactory)[SplashVM::class.java]
    }

    companion object {
        fun createIntent(source: Context): Intent {
            return Intent(source, SplashActivity::class.java)
        }
    }
}
