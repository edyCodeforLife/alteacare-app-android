package id.altea.care.view.account

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import com.moe.pushlibrary.MoEHelper
import id.altea.care.BuildConfig
import id.altea.care.R
import id.altea.care.core.base.BaseFragmentVM
import id.altea.care.core.domain.model.General
import id.altea.care.core.domain.model.Profile
import id.altea.care.core.ext.*
import id.altea.care.databinding.FragmentAccountBinding
import kotlinx.android.synthetic.main.content_profile.*

class AccountFragment : BaseFragmentVM<FragmentAccountBinding, AccountFragmentVM>() {

    val router = AccountRouter()
    private var userId: String? = null

    override fun getUiBinding(): FragmentAccountBinding =
        FragmentAccountBinding.inflate(layoutInflater)

    @SuppressLint("SetTextI18n")
    override fun onFirstLaunch(savedInstanceState: Bundle?, view: View) {
        viewModel?.getProfile(true)
        viewBinding?.profileProfileLayout?.accountVersion?.text =
            String.format(getString(R.string.version), BuildConfig.VERSION_NAME)
    }

    override fun onReExecute() {}

    override fun initUiListener() {
        viewBinding?.run {
            account.accountBtnChangeProfile.onSingleClick().subscribe {
                router.openChangeProfileActivity(requireActivity())
            }.disposedBy(disposable)

            account.accountBtnSetting.onSingleClick().subscribe {
                router.openChangePasswordActivity(requireActivity())
            }.disposedBy(disposable)

            about.accountBtnFaq.onSingleClick().subscribe {
                router.openFaqActivity(requireActivity())
            }.disposedBy(disposable)

            about.accountBtnContact.onSingleClick().subscribe {
                router.openContactServicesActivity(requireActivity())
            }.disposedBy(disposable)

            account.accountBtnFamily.onSingleClick().subscribe {
                router.openFamilyContactActivity(requireActivity())
            }.disposedBy(disposable)

            about.accountBtnTermAndCondition.onSingleClick().subscribe {
                router.openTermConditionAccountActivity(requireActivity())
            }.disposedBy(disposable)

            accountSwipeLayout.let {
                it.setOnRefreshListener {
                    viewModel?.getProfile(false)
                    Handler(Looper.getMainLooper()).postDelayed({
                        it.isRefreshing = false
                    }, 200)
                }
            }

            account.accountBtnAddAccount.onSingleClick()
                .subscribe {
                    router.openBottomSheetAccount(childFragmentManager,
                        addAccountCallback = { router.openLoginActivity(requireActivity()) },
                        createAccountCallback = { router.openRegistration(requireContext()) })
                }
                .disposedBy(disposable)

            accountBtnExit.onSingleClick().subscribe {
                showDialogBackConfirmationPayment(requireContext(),R.string.str_dialog_logout) {
                    viewModel?.logout()
                }
            }.disposedBy(disposable)


        }
    }

    override fun initMenu(): Int = 0

    override fun observeViewModel(viewModel: AccountFragmentVM) {
        observe(viewModel.failureLiveData, ::handleFailure)
        observe(viewModel.isLoadingLiveData, ::handleLoadingProfile)
        observe(viewModel.generalEvent, ::getLogout)
        observe(viewModel.profile, ::getProfileResponse)
        observe(viewModel.loadingLottieEvent, ::handleLottieLoading)
    }

    private fun getLogout(general: General?) {
        if (general?.status == true) {
            router.openMainActivity(requireActivity())
        }
    }

    private fun handleLoadingProfile(showLoading: Boolean?) {
        viewBinding?.run {
            profileProfileLayout.accountProgressBar.isVisible = showLoading == true
            profileProfileLayout.groupUserName.isVisible = showLoading == false
        }
    }

    private fun handleLottieLoading(showLoading: Boolean?) {
        viewBinding?.run {
            accountLoading.isVisible = showLoading == true
            accountScrollView.isVisible = showLoading != true
        }
    }

    @SuppressLint("SetTextI18n")
    private fun getProfileResponse(profile: Profile?) {
        viewBinding?.run {
            profile?.let { profile ->
                accountimgProfile.loadImage(
                    profile.userDetails?.avatar?.formats?.small.orEmpty(),
                    R.drawable.ic_change_photo_profile,
                    R.drawable.ic_logo_loading
                )
                accountTxtProfileName.text = "${profile.firstName}  ${profile.lastName}"
                accountTxtProfileEmail.text = profile.email
                userId = profile.id
            }
        }

    }

    override fun bindViewModel(): AccountFragmentVM {
        return ViewModelProvider(this, viewModelFactory)[AccountFragmentVM::class.java]
    }
}