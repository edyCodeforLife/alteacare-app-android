package id.altea.care.core.base

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import id.altea.care.R
import id.altea.care.core.exception.Failure
import id.altea.care.core.ext.cloneDefaultTheme
import id.altea.care.view.login.LoginActivityRouter
import io.reactivex.disposables.CompositeDisposable

abstract class BaseBottomSheet<VB : ViewBinding> : BottomSheetDialogFragment() {

    protected var viewBinding: VB? = null
    protected val disposable: CompositeDisposable by lazy { CompositeDisposable() }
    protected lateinit var cloneLayoutInflater: LayoutInflater

    abstract fun getUiBinding(): VB
    abstract fun onFirstLaunch(view: View, savedInstanceState: Bundle?)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.BottomSheetDialogTopRounded)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        cloneLayoutInflater = inflater.cloneDefaultTheme(requireActivity())
        if (viewBinding == null) {
            viewBinding = getUiBinding()
        }
        return viewBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onFirstLaunch(view, savedInstanceState)
    }

    open fun handleFailure(failure: Failure?) {
        when (failure) {
            is Failure.ExpiredSession -> {
                startActivity(
                    LoginActivityRouter.createIntent(requireContext())
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                )
                requireActivity().finish()
            }
            else -> {}
        }
    }

    override fun onDestroyView() {
        disposable.dispose()
        viewBinding = null
        super.onDestroyView()
    }
}
