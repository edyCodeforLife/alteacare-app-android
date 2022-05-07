package id.altea.care.view.home.bottomsheet

import android.os.Bundle
import android.view.View
import id.altea.care.R
import id.altea.care.core.base.BaseBottomSheet
import id.altea.care.core.domain.model.Profile
import id.altea.care.core.ext.disposedBy
import id.altea.care.core.ext.isHtmlText
import id.altea.care.core.ext.onSingleClick
import id.altea.care.databinding.BottomSheetLiveChatBinding
import id.altea.care.view.home.HomeRouter
import zendesk.core.AnonymousIdentity
import zendesk.core.Identity
import zendesk.core.Zendesk

class LiveChatBottomSheet : BaseBottomSheet<BottomSheetLiveChatBinding>() {

    private val router by lazy {
        HomeRouter()
    }
    private val profile by lazy {
        arguments?.getParcelable<Profile>(EXTRA_PROFILE)
    }

    override fun getUiBinding(): BottomSheetLiveChatBinding {
      return BottomSheetLiveChatBinding.inflate(cloneLayoutInflater)
    }

    override fun onFirstLaunch(view: View, savedInstanceState: Bundle?) {
       viewBinding?.run {
           txtChatMa.text = getString(R.string.text_chat_ma).isHtmlText()
           txtChatCs.text = getString(R.string.text_chat_cs).isHtmlText()

           cardCustomerService.onSingleClick()
               .subscribe {
                   openLiveChat(profile = profile,tag = "cs")
                }.disposedBy(disposable)

           cardGeneralDoctor.onSingleClick()
               .subscribe{
                   openLiveChat(profile = profile, tag = "ma")
               }.disposedBy(disposable)
       }
    }

    private fun openLiveChat(profile: Profile?,tag : String){
        if (profile != null){
            val identity: Identity = AnonymousIdentity.Builder()
                .withNameIdentifier("${profile.firstName} ${profile.lastName}")
                .withEmailIdentifier("${profile.email}")
                .build()
            Zendesk.INSTANCE.setIdentity(identity)
            router.openRequestListLiveChatActivity(requireActivity(),tag)
        }else {
            val identity = AnonymousIdentity()
            Zendesk.INSTANCE.setIdentity(identity)
            router.openRequestListLiveChatActivity(requireActivity(),tag)
        }
    }

    companion object {
        private const val EXTRA_PROFILE = "profile"
        fun newInstance(
            profile: Profile?
        ): LiveChatBottomSheet {
            return LiveChatBottomSheet().apply {
                val bundle = Bundle().apply {
                    putParcelable(EXTRA_PROFILE,profile)
                }
                arguments = bundle
            }
        }
    }

}