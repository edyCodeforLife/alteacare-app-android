package id.altea.care.view.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.core.net.toUri
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import id.altea.care.R
import id.altea.care.core.base.BaseActivityVM
import id.altea.care.core.domain.event.HomeWidgetsEvent
import id.altea.care.core.domain.event.MainFragmentTabCreatedEvent
import id.altea.care.core.domain.model.MyConsultationDateEvent
import id.altea.care.core.ext.disposedBy
import id.altea.care.core.helper.RxBus
import id.altea.care.databinding.ActivityMainBinding
import id.altea.care.view.account.AccountFragment
import id.altea.care.view.home.HomeFragment
import id.altea.care.view.login.LoginActivityRouter
import id.altea.care.view.myconsultation.MyConsultationFragment
import id.altea.care.view.specialist.SpecialistFragment

/**
 * Created by trileksono on 12/03/21.
 */
class MainActivity : BaseActivityVM<ActivityMainBinding, MainActivityVM>() {

    var viewPagerIndex  : Int? = 0

    var dateMyConsultation  : String? = ""

    override fun enableBackButton(): Boolean = false

    override fun bindToolbar(): Toolbar? = null

    override fun getUiBinding(): ActivityMainBinding {
        return ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onFirstLaunch(savedInstanceState: Bundle?) {
        viewBinding?.run {
            navigation.setOnNavigationItemSelectedListener {
                when (it.itemId) {
                    R.id.menu_home -> changeTab(0)
                    R.id.menu_specialist -> changeTab(1)
                    R.id.menu_consultation -> changeTab(2)
                    R.id.menu_profile -> changeTab(3)
                }
                true
            }
        }
        dateMyConsultation = intent.getStringExtra(EXTRA_DATE_MY_CONSULTATION)
        listenRxBus()
        viewPagerIndex = intent.getIntExtra(EXTRA_VIEW_PAGER_INDEX,0)
        changeTab(intent.getIntExtra(EXTRA_TAB_INDEX, 0))
    }

    /**
     * when open from deeplink, the deeplink will be execute when home fragment has initialize
     * @see MainFragmentTabCreatedEvent
     */
    private fun listenRxBus() {
        RxBus.getEvents()
            .subscribe {
                when (it) {
                    is MainFragmentTabCreatedEvent -> if (it.isFragmentFinishCreated) mDeepLinkManager.executeDeepLink(this)
                    is HomeWidgetsEvent -> {
                        mDeepLinkManager.setDeepLinkUri(it.url.toUri())
                        mDeepLinkManager.executeDeepLink(this)
                    }
                }
            }
            .disposedBy(disposable)
    }

    private fun changeTab(indexTab: Int) {
        when (indexTab) {
            0 -> {
                viewBinding?.navigation?.menu?.get(indexTab)?.isChecked = true
                showFragment(HomeFragment(), getString(R.string.nav_home))
            }
            1 -> {
                viewBinding?.navigation?.menu?.get(indexTab)?.isChecked = true
                showFragment(SpecialistFragment(), getString(R.string.nav_specialist))

            }
            2 -> {
                if (baseViewModel?.isUserLoggedIn() == true) {
                    showFragment(
                        MyConsultationFragment(),
                        getString(R.string.nav_consultation)
                    )
                    viewBinding?.navigation?.menu?.get(indexTab)?.isChecked = true
                } else {
                    startActivity(LoginActivityRouter.createIntent(this@MainActivity))
                }
            }
            3 -> {
                if (baseViewModel?.isUserLoggedIn() == true) {
                    showFragment(AccountFragment(), getString(R.string.str_account))
                    viewBinding?.navigation?.menu?.get(indexTab)?.isChecked = true
                } else {
                    startActivity(LoginActivityRouter.createIntent(this@MainActivity))
                }
            }
        }
    }

    override fun initUiListener() {}

    private fun showFragment(fragment: Fragment, tag: String) {
        val fm = supportFragmentManager
        var currentFragment: Fragment? = null
        val fragments = fm.fragments
        for (f in fragments) {
            if (f.isVisible) {
                currentFragment = f
                break
            }
        }
        val newFragment = fm.findFragmentByTag(tag)
        if (currentFragment != null && newFragment != null && currentFragment == newFragment) return
        val transaction = fm.beginTransaction()
        if (newFragment == null) {
            transaction.add(R.id.fragment_container, fragment, tag)
        }
        if (currentFragment != null) {
            if (currentFragment is HomeFragment || currentFragment is MyConsultationFragment || currentFragment is AccountFragment) {
                transaction.remove(currentFragment)
            } else {
                transaction.hide(currentFragment)
            }
        }
        if (newFragment != null) {
            transaction.show(newFragment)
        }
        transaction.commitNow()
    }

    override fun observeViewModel(viewModel: MainActivityVM) {}

    override fun bindViewModel(): MainActivityVM {
        return ViewModelProvider(this, viewModelFactory)[MainActivityVM::class.java]
    }

    override fun onBackPressed() {
        supportFragmentManager.run {
            val fragment = fragments.filter { it.isVisible }.firstOrNull()
            if (fragment != null && fragment !is HomeFragment) {
                viewBinding?.navigation?.selectedItemId = R.id.menu_home
                return
            }
        }
        super.onBackPressed()
    }


    companion object {
        private const val EXTRA_TAB_INDEX = "MainActivity.tabIndex"
        private const val EXTRA_VIEW_PAGER_INDEX = "MainActivity.viewPagerIndex"
        const val EXTRA_DATE_MY_CONSULTATION = "MainActivity.DateMyConsultation"

        fun createIntent(caller: Context, tabIndex: Int = 0,viewPagerIndex : Int? = 0,dateMyConsultation : String? =""): Intent {
            return Intent(caller, MainActivity::class.java)
                .putExtra(EXTRA_TAB_INDEX, tabIndex)
                .putExtra(EXTRA_VIEW_PAGER_INDEX,viewPagerIndex)
                .putExtra(EXTRA_DATE_MY_CONSULTATION,dateMyConsultation)
        }
    }
}
