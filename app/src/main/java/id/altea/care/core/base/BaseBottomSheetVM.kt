package id.altea.care.core.base

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.viewbinding.ViewBinding
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject

abstract class BaseBottomSheetVM<VB : ViewBinding, VM : BaseViewModel> : BaseBottomSheet<VB>() {

    protected var viewModel: VM? = null

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel = bindViewModel()
        super.onViewCreated(view, savedInstanceState)
        observeViewModel(viewModel!!)
    }

    abstract fun observeViewModel(viewModel: VM)
    abstract fun bindViewModel(): VM

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel = null
    }
}
