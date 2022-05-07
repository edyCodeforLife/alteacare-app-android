package id.altea.care.view.onboarding

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import id.altea.care.core.base.BaseActivity
import id.altea.care.core.ext.disposedBy
import id.altea.care.core.ext.onSingleClick
import id.altea.care.databinding.ActivityOnboardingStartNowBinding
import javax.inject.Inject

class OnboardingStartNowActivty : BaseActivity<ActivityOnboardingStartNowBinding>(), HasAndroidInjector {

    @Inject
    lateinit var androidInjector: DispatchingAndroidInjector<Any>
    private val router = OnboardingRouter()

    override fun bindToolbar(): Toolbar? {
        return null
    }

    override fun enableBackButton(): Boolean {
       return false
    }

    override fun getUiBinding(): ActivityOnboardingStartNowBinding {
        return ActivityOnboardingStartNowBinding.inflate(layoutInflater)
    }

    override fun onFirstLaunch(savedInstanceState: Bundle?) {

    }

    override fun initUiListener() {
        viewBinding?.run {
            onBoardingBtnRegisterNow.onSingleClick().subscribe {
                router.openRegisterActivity(this@OnboardingStartNowActivty)
            }.disposedBy(disposable)

            onBoardingBtnLet.onSingleClick().subscribe {
                router.openMainScreen(this@OnboardingStartNowActivty)
            }.disposedBy(disposable)
        }
    }

    override fun androidInjector(): AndroidInjector<Any> {
        return androidInjector
    }

    companion object {
        fun createIntent(caller: Context): Intent {
            return Intent(caller, OnboardingStartNowActivty::class.java)

        }
    }
}