package id.altea.care.view.account.contact

import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import dagger.android.AndroidInjection
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import id.altea.care.R
import id.altea.care.core.base.BaseActivity
import id.altea.care.core.base.BaseActivityVM
import id.altea.care.core.domain.model.Profile
import id.altea.care.core.ext.disposedBy
import id.altea.care.core.ext.observe
import id.altea.care.core.ext.onSingleClick
import id.altea.care.databinding.ActivityContactServicesBinding
import kotlinx.android.synthetic.main.fragment_on_going.view.*
import timber.log.Timber
import zendesk.support.requestlist.RequestListActivity
import javax.inject.Inject
import zendesk.core.AnonymousIdentity
import zendesk.core.Identity
import zendesk.core.Zendesk


class ContactServicesActivity : BaseActivityVM<ActivityContactServicesBinding, ContactServicesVM>() {

    override fun bindToolbar(): Toolbar? = viewBinding?.appbar?.toolbar

    override fun enableBackButton(): Boolean = true

    override fun getUiBinding(): ActivityContactServicesBinding =
        ActivityContactServicesBinding.inflate(layoutInflater)

    override fun onFirstLaunch(savedInstanceState: Bundle?) {
        viewBinding?.run {
            appbar.txtToolbarTitle.text = getString(R.string.contact_alteacare)
        }
    }

    override fun initUiListener() {
        viewBinding?.run {
            liveChatButton.onSingleClick().subscribe {
                baseViewModel?.getProfile(true)
            }.disposedBy(disposable)
            sendMessageButton.onSingleClick().subscribe {
                ContactServicesRouter.openContactActivity(this@ContactServicesActivity)
            }
        }
    }

    override fun bindViewModel(): ContactServicesVM {
        return ViewModelProvider(this, viewModelFactory)[ContactServicesVM::class.java]
    }

    override fun observeViewModel(viewModel: ContactServicesVM) {
        observe(viewModel.isLoadingLiveData, ::handleLoading)
        observe(viewModel.failureLiveData, ::handleFailure)
        observe(viewModel.profileEvent, ::handleGetProfile)
    }

    private fun handleGetProfile(profile: Profile?) {
        val identity: Identity = AnonymousIdentity.Builder()
            .withNameIdentifier("${profile?.firstName} ${profile?.lastName}")
            .withEmailIdentifier("${profile?.email}")
            .build()
        Zendesk.INSTANCE.setIdentity(identity)
        ContactServicesRouter.openRequestListActivity(this@ContactServicesActivity)
    }

}