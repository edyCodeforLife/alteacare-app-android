package id.altea.care.view.onboarding

import android.app.Activity
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import id.altea.care.view.login.LoginActivityRouter
import id.altea.care.view.main.MainActivity
import id.altea.care.view.register.contactinfo.RegisterContactRouter
import id.altea.care.view.register.contactinfo.RegisterContactState
import id.altea.care.view.splashscreen.SplashActivity

class OnboardingRouter {

   fun openOnboarding(source : Activity){
        source.startActivity(OnboardingActivity.createIntent(source))
        source.finish()
   }

    fun openMainScreen(source: Activity) {
        source.startActivity(MainActivity.createIntent(source))
        source.finish()
    }

    fun openOnboardingStartNow(source: Activity) {
        source.startActivity(OnboardingStartNowActivty.createIntent(source))
        source.finish()
    }

    fun openLogin(source: Activity) {
        source.startActivity(LoginActivityRouter.createIntent(source))
        source.finish()
    }


    fun openRegisterActivity(source: AppCompatActivity) {
        source.startActivity(
            RegisterContactRouter.createIntent(
            source,
            null,
            null,
            RegisterContactState.PAGE_REGISTER,
            null
        ))
        source.finish()
    }
}