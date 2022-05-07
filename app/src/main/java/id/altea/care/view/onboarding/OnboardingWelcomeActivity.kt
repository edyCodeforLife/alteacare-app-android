package id.altea.care.view.onboarding

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.widget.Toolbar
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import id.altea.care.R
import id.altea.care.core.base.BaseActivity
import id.altea.care.core.ext.disposedBy
import id.altea.care.core.ext.onSingleClick
import id.altea.care.databinding.ActivityWelcomePageBinding
import javax.inject.Inject

class OnboardingWelcomeActivity  : BaseActivity<ActivityWelcomePageBinding>(),HasAndroidInjector {
    @Inject
    lateinit var androidInjector: DispatchingAndroidInjector<Any>


    private val router = OnboardingRouter()

    override fun bindToolbar(): Toolbar? {
        return null
    }

    override fun enableBackButton(): Boolean {
      return false
    }

    override fun getUiBinding(): ActivityWelcomePageBinding {
       return ActivityWelcomePageBinding.inflate(layoutInflater)
    }

    override fun onFirstLaunch(savedInstanceState: Bundle?) {
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = getColor(R.color.blueDarker)

    }

    override fun initUiListener() {
        viewBinding?.run {
            contentWelcomePageBtn.onSingleClick().subscribe {
                router.openOnboarding(this@OnboardingWelcomeActivity)
            }.disposedBy(disposable)
        }
    }

    override fun androidInjector(): AndroidInjector<Any> {
        return androidInjector
    }

    companion object {
        fun createIntent(caller: Context): Intent {
            return Intent(caller, OnboardingWelcomeActivity::class.java)

        }
    }
}