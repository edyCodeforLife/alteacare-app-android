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
import id.altea.care.databinding.BottomsheetDesicionFamilyBinding


class DecisionFamilyBottomSheet(private val submitAsAccount: () -> Unit,
                                private val submitAsFamilyProfile : () -> Unit) : BottomSheetDialogFragment() {

    private var viewBinding: BottomsheetDesicionFamilyBinding? = null

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
            viewBinding = BottomsheetDesicionFamilyBinding.inflate(
                    inflater.cloneDefaultTheme(requireActivity())
            )
        }
        return viewBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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

        viewBinding?.run {
            decissionFamilyBtnAccount.setOnClickListener {
                submitAsAccount()
                dismiss()
            }

            decissionFamilyBtnProfilInfo.setOnClickListener {
                submitAsFamilyProfile()
                dismiss()
            }
        }


    }

    override fun onDestroyView() {
        viewBinding = null
        super.onDestroyView()
    }
    private fun setupFullHeight(bottomSheet: View) {
        val layoutParams = bottomSheet.layoutParams
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT
        bottomSheet.layoutParams = layoutParams
    }

    companion object {
        fun newInstance(
                submitAsAccount: () -> Unit,
                submitAsFamilyProfile : () -> Unit
        ): DecisionFamilyBottomSheet {

           return DecisionFamilyBottomSheet(submitAsAccount,submitAsFamilyProfile)
        }
    }
}
