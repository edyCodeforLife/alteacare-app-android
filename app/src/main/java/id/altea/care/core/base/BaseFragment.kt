package id.altea.care.core.base

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.viewbinding.ViewBinding
import dagger.android.support.DaggerFragment
import id.altea.care.R
import id.altea.care.core.exception.Failure
import id.altea.care.view.login.LoginActivityRouter
import io.reactivex.disposables.CompositeDisposable
import io.sentry.Sentry

/**
 * Created by trileksono on 02/03/21.
 */
abstract class BaseFragment<VB : ViewBinding> : DaggerFragment() {

    var viewBinding: VB? = null
    protected val disposable = CompositeDisposable()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewBinding = getUiBinding()
        return viewBinding?.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(initMenu(), menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onFirstLaunch(savedInstanceState, view)
        initUiListener()
    }

    override fun onStart() {
        super.onStart()
        onReExecute()
    }

    abstract fun getUiBinding(): VB
    abstract fun onFirstLaunch(savedInstanceState: Bundle?, view: View)
    abstract fun onReExecute()
    abstract fun initUiListener()
    abstract fun initMenu(): Int

    fun getParentFm() = requireActivity().supportFragmentManager

    fun getChildFm() = childFragmentManager

    fun onBackPressed() {
        requireActivity().onBackPressed()
    }

    fun showProgress() {
        (requireActivity() as BaseActivity<*>).showProgress()
    }

    fun showProgressReconect(value : String){
        (requireActivity() as BaseActivity<*>).showProgressReconect(value)
    }

    fun hideProgressReconect(){
        (requireActivity() as BaseActivity<*>).hideProgressReconecting()
    }

    fun hideProgress() {
        (requireActivity() as BaseActivity<*>).hideProgress()
    }

    fun showToast(message: String) {
        (requireActivity() as BaseActivity<*>).showToast(message)
    }

    open fun handleFailure(failure: Failure?) {
        when (failure) {
            is Failure.NetworkConnection -> (activity as BaseActivity<*>).showErrorSnackbar(
                getString(R.string.error_disconnect)
            )
            is Failure.ServerError ->{
                (activity as BaseActivity<*>).showErrorSnackbar(failure.message)
                Sentry.captureMessage(failure.message ?: "-")
            }
            is Failure.ExpiredSession -> {
                showToast(getString(R.string.session_expired_error_toast))
                startActivity(
                    LoginActivityRouter.createIntent(requireContext())
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                )
                requireActivity().finish()
            }
            else -> {
                (activity as BaseActivity<*>).showErrorSnackbar(getString(R.string.error_default))
                Sentry.captureMessage(failure.toString() ?: "-")
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewBinding = null
        disposable.clear()
    }
}
