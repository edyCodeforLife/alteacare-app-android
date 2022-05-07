package id.altea.care.view.onboarding

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.adapters.ItemAdapter
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import id.altea.care.R
import id.altea.care.core.base.BaseActivity
import id.altea.care.core.base.BaseActivityVM
import id.altea.care.core.domain.cache.OnboardingCache
import id.altea.care.core.ext.delay
import id.altea.care.core.ext.disposedBy
import id.altea.care.core.ext.onSingleClick
import id.altea.care.databinding.ActivityOnboardingBinding
import id.altea.care.view.login.LoginViewModel
import id.altea.care.view.onboarding.item.ItemOnBoarding
import javax.inject.Inject

class OnboardingActivity : BaseActivityVM<ActivityOnboardingBinding,OnBoardingVM>(), HasAndroidInjector {

    private val itemAdapter = ItemAdapter<ItemOnBoarding>()
    private val fastAdapter = FastAdapter.with(itemAdapter)

    private val router = OnboardingRouter()

    private val viewModel by viewModels<OnBoardingVM> { viewModelFactory }


    override fun bindToolbar(): Toolbar? {
        return null
    }

    override fun enableBackButton(): Boolean {
        return false
    }

    override fun getUiBinding(): ActivityOnboardingBinding {
        return ActivityOnboardingBinding.inflate(layoutInflater)
    }

    override fun onFirstLaunch(savedInstanceState: Bundle?) {
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)

        viewBinding?.run {
            itemAdapter.add(ItemOnBoarding(R.drawable.ic_onboard_1))
            itemAdapter.add(ItemOnBoarding(R.drawable.ic_onboard_2))
            itemAdapter.add(ItemOnBoarding(R.drawable.ic_onboard_3))

            viewPager2.adapter = fastAdapter
            viewPager2.isUserInputEnabled = false
            TabLayoutMediator(onboardingTablayout, viewPager2) { tab, position ->

            }.attach()

        }
    }

    override fun initUiListener() {
        viewBinding?.run {
            var currentPosition = 0
            onBoardingBtn.onSingleClick().subscribe {
                when (onBoardingBtn.text) {
                    getString(R.string.str_welcome_next) -> {
                        currentPosition++
                        disposable.delay(300) {
                            viewPager2.currentItem = currentPosition

                        }
                    }
                    getString(R.string.str_start_now) -> {
                        viewModel.setOnBoarding(true)
                        router.openOnboardingStartNow(this@OnboardingActivity)
                    }
                }
            }.disposedBy(disposable)

            onBoardingBtnIn.onSingleClick().subscribe {
                viewModel.setOnBoarding(true)
                router.openLogin(this@OnboardingActivity)
            }.disposedBy(disposable)

            viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {

                override fun onPageSelected(position: Int) {
                    when (position) {
                        0 -> {
                            onBoardingParent.setBackgroundColor(getColor(R.color.blueDarker))
                            window.statusBarColor = getColor(R.color.blueDarker)
                            onboardingTablayout.setBackgroundColor(getColor(R.color.blueDarker))
                            onBoardingBtn.text = getString(R.string.str_welcome_next)
                            onBoardingTitleText.text = getString(R.string.str_make_consultation)
                            onBoardingDescriptionText.text = "Pilih sendiri dokter spesialis Anda sesuai \njadwal yang diinginkan"
                            onBoardingHaveAccountText.isVisible = false
                            onBoardingBtnIn.isVisible = false
                        }
                        1 -> {
                            onBoardingParent.setBackgroundColor(getColor(R.color.primary))
                            window.statusBarColor = getColor(R.color.primary)
                            onboardingTablayout.setBackgroundColor(getColor(R.color.primary))
                            onBoardingBtn.text = getString(R.string.str_welcome_next)
                            onBoardingTitleText.text = getString(R.string.str_welcome_regis_vaksin)
                            onBoardingDescriptionText.text = "Jadwalkan vaksinasi COVID-19 di rumah sakit \nterdekat secara online. Simpel!"
                            onBoardingHaveAccountText.isVisible = false
                            onBoardingBtnIn.isVisible = false
                        }
                        2 -> {
                            onBoardingParent.setBackgroundColor(getColor(R.color.blueDarker))
                            window.statusBarColor = getColor(R.color.blueDarker)
                            onboardingTablayout.setBackgroundColor(getColor(R.color.blueDarker))
                            onBoardingBtn.text = getString(R.string.str_start_now)
                            onBoardingTitleText.text = getString(R.string.str_online_drugstore)
                            onBoardingDescriptionText.text = "Pesan obat dan vitamin lewat aplikasi, \nlangsung dikirim sampai rumah!"
                            onBoardingHaveAccountText.isVisible = true
                            onBoardingBtnIn.isVisible = true
                        }
                    }

                }
            })

        }
    }

    override fun androidInjector(): AndroidInjector<Any> {
        return androidInjector
    }

    companion object {
        fun createIntent(caller: Context): Intent {
            return Intent(caller, OnboardingActivity::class.java)

        }
    }

    override fun bindViewModel(): OnBoardingVM {
       return viewModel
    }

    override fun observeViewModel(viewModel: OnBoardingVM) {

    }
}