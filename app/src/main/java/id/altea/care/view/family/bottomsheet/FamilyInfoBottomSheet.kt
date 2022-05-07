package id.altea.care.view.family.bottomsheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import id.altea.care.R
import id.altea.care.core.domain.model.FamilyInfo
import id.altea.care.core.ext.cloneDefaultTheme
import id.altea.care.databinding.BottomsheetFamilyInfoConfirmationBinding
import id.altea.care.view.common.enums.Gender
import kotlinx.android.synthetic.main.bottomsheet_personal_info_confirmation.*

class FamilyInfoBottomSheet(private val submitCallback: () -> Unit
) : BottomSheetDialogFragment() {

    private var viewBinding: BottomsheetFamilyInfoConfirmationBinding? = null
    private lateinit var familyInfo: FamilyInfo

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
            viewBinding = BottomsheetFamilyInfoConfirmationBinding.inflate(
                    inflater.cloneDefaultTheme(requireActivity())
            )
        }
        return viewBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.getParcelable<FamilyInfo>(EXTRA_INFO)?.let {
            familyInfo = it
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

        viewBinding?.apply {
            familyInfoConfirmationTxtContactName.text = familyInfo.contactFamily
            familyInfoConfirmationTxtFullName.text = "${familyInfo.firstName} ${familyInfo.lastName}"
            familyInfoConfirmationTxtGender.text = if (familyInfo.gender == Gender.MALE) {
                getString(R.string.gender_male)
            } else {
                getString(R.string.gender_female)
            }
            familyInfoConfirmationTxtBirthDate.text = familyInfo.birthDate
            familyInfoConfirmationTxtBirthCounty.text = "${familyInfo.birthCountry},"
            familyInfoConfirmationTxtBirthPlace.text = familyInfo.birthPlace
            familylInfoConfirmationTxtCitizenShip.text = familyInfo.nationality
            familyInfoConfirmationBtnNext.setOnClickListener {
                submitCallback()
                dismiss()
            }
            familyInfoConfirmationBtnEdit.setOnClickListener { dismiss() }
        }
    }

    private fun setupFullHeight(bottomSheet: View) {
        val layoutParams = bottomSheet.layoutParams
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT
        bottomSheet.layoutParams = layoutParams
    }

    companion object {
        private const val EXTRA_INFO = "FamilyInfo.confirmationInfo"
        fun newInstance(
                familyInfo: FamilyInfo,
                submitCallback: () -> Unit,
        ): FamilyInfoBottomSheet {
            val argument = Bundle().apply {
                putParcelable(EXTRA_INFO, familyInfo)
            }
            return FamilyInfoBottomSheet(submitCallback).apply {
                arguments = argument
            }
        }
    }
}
