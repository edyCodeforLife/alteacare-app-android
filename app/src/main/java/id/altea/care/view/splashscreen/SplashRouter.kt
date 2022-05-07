package id.altea.care.view.splashscreen

import android.content.Intent
import android.net.Uri
import id.altea.care.BuildConfig
import id.altea.care.view.forceupdate.ForceUpdateActivity
import id.altea.care.view.main.MainActivity
import id.altea.care.view.onboarding.OnboardingActivity
import id.altea.care.view.onboarding.OnboardingWelcomeActivity

/**
 * Created by trileksono on 12/03/21.
 */
class SplashRouter {

    fun openMainScreen(source: SplashActivity) {
        source.startActivity(MainActivity.createIntent(source))
        source.finish()
    }

    fun openForceUpdate(source: SplashActivity){
        source.startActivity(ForceUpdateActivity.createIntent(source))
        source.finish()
    }

    fun openOnBoarding(source: SplashActivity){
        source.startActivity(OnboardingWelcomeActivity.createIntent(source))
        source.finish()
    }

}
