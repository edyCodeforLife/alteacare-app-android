package id.altea.care.core.base

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.viewbinding.ViewBinding
import dagger.android.AndroidInjection
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import id.altea.care.view.deeplink.DeepLinkManager
import javax.inject.Inject

/**
 * Created by trileksono on 02/03/21.
 */
abstract class BaseActivityVM<VB : ViewBinding, VM : BaseViewModel> : BaseActivity<VB>(), HasAndroidInjector {

    protected var baseViewModel: VM? = null

    @Inject
    lateinit var androidInjector : DispatchingAndroidInjector<Any>

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var mDeepLinkManager: DeepLinkManager

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        baseViewModel = bindViewModel()
        super.onCreate(savedInstanceState)
        baseViewModel?.let { observeViewModel(it) }
    }

    abstract fun bindViewModel(): VM

    abstract fun observeViewModel(viewModel: VM)

    protected fun handleLoading(showLoading: Boolean?) {
        if (showLoading == true) showProgress() else hideProgress()
    }

    override fun androidInjector(): AndroidInjector<Any> = androidInjector

    override fun onDestroy() {
        super.onDestroy()
        baseViewModel = null
    }
}
