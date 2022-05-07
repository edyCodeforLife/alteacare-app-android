package id.altea.care.view.family.listfamily.bottomsheet

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import dagger.android.support.AndroidSupportInjection
import id.altea.care.core.base.BaseBottomSheet
import id.altea.care.core.ext.disposedBy
import id.altea.care.core.ext.onSingleClick
import id.altea.care.databinding.BottomsheetOptionFamilyBinding
import id.altea.care.view.address.addresslist.bottomsheet.AddressListOptionBottomSheet
import id.altea.care.view.family.listfamily.ListFamilyVM
import javax.inject.Inject

class FamilyListOptionBottomSheet : BaseBottomSheet<BottomsheetOptionFamilyBinding>() {

    private val idFamily by lazy { arguments?.getString(EXTRA_FAMILY_ID) }
    private val position by lazy { arguments?.getInt(EXTRA_FAMILY_POSITION) }
    lateinit var viewModel: ListFamilyVM

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel =
                ViewModelProvider(requireActivity(), viewModelFactory)[ListFamilyVM::class.java]
        super.onViewCreated(view, savedInstanceState)
    }

    override fun getUiBinding(): BottomsheetOptionFamilyBinding {
        return BottomsheetOptionFamilyBinding.inflate(layoutInflater)
    }

    override fun onFirstLaunch(view: View, savedInstanceState: Bundle?) {
        viewBinding?.run {
            familyBottomSheetBtnPrimary
                    .onSingleClick()
                    .doAfterNext { dismiss() }
                    .subscribe { idFamily?.let { viewModel.setPrimaryFamily(it, position!!) } }
                    .disposedBy(disposable)
        }
    }

    companion object {
        private const val EXTRA_FAMILY_ID = "BottomSheetFamilyList.addressId"
        private const val EXTRA_FAMILY_POSITION = "BottomSheetFamilyList.adapterPosition"
        fun newInstance(idFamily: String, position: Int): FamilyListOptionBottomSheet {
            return FamilyListOptionBottomSheet().apply {
                val bundle = Bundle()
                bundle.putString(EXTRA_FAMILY_ID, idFamily)
                bundle.putInt(EXTRA_FAMILY_POSITION, position)
                arguments = bundle
            }
        }
    }

}
