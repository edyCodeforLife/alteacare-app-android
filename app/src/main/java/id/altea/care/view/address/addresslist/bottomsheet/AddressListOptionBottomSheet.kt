package id.altea.care.view.address.addresslist.bottomsheet

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import dagger.android.support.AndroidSupportInjection
import id.altea.care.core.base.BaseBottomSheet
import id.altea.care.core.ext.disposedBy
import id.altea.care.core.ext.onSingleClick
import id.altea.care.databinding.BottomsheetOptionAddressBinding
import id.altea.care.view.address.addresslist.AddressListVM
import javax.inject.Inject

class AddressListOptionBottomSheet : BaseBottomSheet<BottomsheetOptionAddressBinding>() {

    private val idAddress by lazy { arguments?.getString(EXTRA_ADDRESS_ID) }
    private val position by lazy { arguments?.getInt(EXTRA_ADDRESS_POSITION) }
    lateinit var viewModel: AddressListVM

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel =
            ViewModelProvider(requireActivity(), viewModelFactory)[AddressListVM::class.java]
        super.onViewCreated(view, savedInstanceState)
    }

    override fun getUiBinding(): BottomsheetOptionAddressBinding {
        return BottomsheetOptionAddressBinding.inflate(layoutInflater)
    }

    override fun onFirstLaunch(view: View, savedInstanceState: Bundle?) {
        viewBinding?.run {
            addressBottomSheetBtnDelete
                .onSingleClick()
                .doAfterNext { dismiss() }
                .subscribe { idAddress?.let { viewModel.deleteAddress(it, position!!) } }
                .disposedBy(disposable)

            addressBottomSheetBtnPrimary
                .onSingleClick()
                .doAfterNext { dismiss() }
                .subscribe { idAddress?.let { viewModel.setPrimaryAddress(it, position!!) } }
                .disposedBy(disposable)
        }

    }

    companion object {
        private const val EXTRA_ADDRESS_ID = "BottomSheetAddressList.addressId"
        private const val EXTRA_ADDRESS_POSITION = "BottomSheetAddressList.adapterPosition"
        fun newInstance(idAddress: String, position: Int): AddressListOptionBottomSheet {
            return AddressListOptionBottomSheet().apply {
                val bundle = Bundle()
                bundle.putString(EXTRA_ADDRESS_ID, idAddress)
                bundle.putInt(EXTRA_ADDRESS_POSITION, position)
                arguments = bundle
            }
        }
    }

}
