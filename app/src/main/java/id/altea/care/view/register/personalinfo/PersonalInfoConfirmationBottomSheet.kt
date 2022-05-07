package id.altea.care.view.register.personalinfo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import id.altea.care.R
import id.altea.care.core.domain.event.CallBackEvent
import id.altea.care.core.domain.event.MainFragmentTabCreatedEvent
import id.altea.care.core.domain.model.RegisterInfo
import id.altea.care.core.ext.cloneDefaultTheme
import id.altea.care.core.helper.RxBus
import id.altea.care.databinding.BottomsheetPersonalInfoConfirmationBinding
import id.altea.care.view.common.enums.Gender
import kotlinx.android.synthetic.main.bottomsheet_personal_info_confirmation.*

/**
 * Created by trileksono on 04/03/21.
 */
class PersonalInfoConfirmationBottomSheet() : BottomSheetDialogFragment() {

    private var viewBinding: BottomsheetPersonalInfoConfirmationBinding? = null
    private lateinit var registerInfo: RegisterInfo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.BottomSheetDialogTopRounded)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (viewBinding == null) {
            viewBinding = BottomsheetPersonalInfoConfirmationBinding.inflate(
                inflater.cloneDefaultTheme(requireActivity())
            )
        }
        return viewBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.getParcelable<RegisterInfo>(EXTRA_INFO)?.let {
            registerInfo = it
        }

        dialog?.setOnShowListener {
            val bottomSheetDialog = it as BottomSheetDialog
            val parentLayout =
                bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            parentLayout?.let { root ->
                val behaviour = BottomSheetBehavior.from(root)
                setupFullHeight(root)
                behaviour.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }

        viewBinding.apply {
            personalInfoConfirmationTxtFullName.text = registerInfo.firstName
                .plus(" ${registerInfo.lastName}")
            personalInfoConfirmationTxtBirthDate.text = registerInfo.birthDate
            personalInfoConfirmationTxtGender.text = if (registerInfo.gender == Gender.MALE) {
                getString(R.string.gender_male)
            } else {
                getString(R.string.gender_female)
            }
            personalInfoConfirmationTxtBirthCounty.text = registerInfo.resultFilter?.text
            personalInfoConfirmationTxtBirthPlace.text = registerInfo.cityOfBirth
            personalInfoConfirmationBtnNext.setOnClickListener {
               // Event Callback
                RxBus.post(CallBackEvent(callBackEventCreated = true,registerInfo))
            }
            personalInfoConfirmationBtnEdit.setOnClickListener { dismiss() }
        }
    }

    private fun setupFullHeight(bottomSheet: View) {
        val layoutParams = bottomSheet.layoutParams
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT
        bottomSheet.layoutParams = layoutParams
    }

    companion object {
        private const val EXTRA_INFO = "PersonalInfo.registerInfo"
        fun newInstance(
            registerInfo: RegisterInfo
        ): PersonalInfoConfirmationBottomSheet {
            val argument = Bundle().apply {
                putParcelable(EXTRA_INFO, registerInfo)
            }
            return PersonalInfoConfirmationBottomSheet().apply {
                arguments = argument
            }
        }
    }
}
