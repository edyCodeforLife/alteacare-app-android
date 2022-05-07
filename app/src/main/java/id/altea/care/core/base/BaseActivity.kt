package id.altea.care.core.base

import android.annotation.SuppressLint
import android.app.ActionBar
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.Constraints
import androidx.viewbinding.ViewBinding
import com.moe.pushlibrary.MoEHelper
import id.altea.care.R
import id.altea.care.core.exception.Failure
import id.altea.care.core.helper.util.CustomSnackbarView
import id.altea.care.view.login.LoginActivityRouter
import io.reactivex.disposables.CompositeDisposable
import io.sentry.Sentry
import kotlinx.android.synthetic.main.progress_loading_reconnecting.*

/**
 * Created by trileksono on 02/03/21.
 */
abstract class BaseActivity<VB : ViewBinding> : AppCompatActivity() {

    private var mToolbar: Toolbar? = null
    protected var disposable = CompositeDisposable()

    var viewBinding: VB? = null

    private lateinit var progress: Dialog
    private lateinit var progressReconnecting : Dialog
    private lateinit var rootView: ViewGroup

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = getUiBinding()
        setContentView(viewBinding?.root)
        rootView = window.decorView.findViewById(android.R.id.content)
        setupToolbar()
        onFirstLaunch(savedInstanceState)
        initProgressDialog()
        initProgressReconectingDialog()
        initUiListener()
    }

    abstract fun bindToolbar(): Toolbar?
    abstract fun enableBackButton(): Boolean
    abstract fun getUiBinding(): VB
    abstract fun onFirstLaunch(savedInstanceState: Bundle?)
    abstract fun initUiListener()

    override fun onDestroy() {
        disposable.clear()
        super.onDestroy()
        viewBinding = null
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        android.R.id.home -> {
            onBackPressed()
            true
        }
        else -> false
    }

    private fun setupToolbar() {
        bindToolbar()?.let {
            mToolbar = it
            setSupportActionBar(mToolbar)
            supportActionBar?.apply {
                setDisplayShowTitleEnabled(false)
                setDisplayHomeAsUpEnabled(enableBackButton())
                setHomeAsUpIndicator(R.drawable.ic_arrow_back)
            }
        }
    }

    @SuppressLint("InflateParams")
    private fun initProgressDialog() {
        if (!::progress.isInitialized) {
            progress = Dialog(this)
            val inflate = LayoutInflater.from(this).inflate(R.layout.progress_default, null)
            progress.setContentView(inflate)
            progress.setCancelable(false)
            progress.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            progress.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
    }

    private fun initProgressReconectingDialog() {
        if (!::progressReconnecting.isInitialized) {
            progressReconnecting = Dialog(this)
            val inflate = LayoutInflater.from(this).inflate(R.layout.progress_loading_reconnecting, null)
            progressReconnecting.setContentView(inflate)
            progressReconnecting.setCancelable(false)
            progressReconnecting.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            progressReconnecting.window?.setGravity(Gravity.TOP)
            progressReconnecting.window?.setLayout(ActionBar.LayoutParams.MATCH_PARENT,ActionBar.LayoutParams.WRAP_CONTENT)
        }
    }

    fun showProgress() {
        if (!progress.isShowing) {
            progress.show()
        }
    }

    fun showProgressReconect(value : String?){
        if (!progressReconnecting.isShowing){
            progressReconnecting.loadingText.text = value
            progressReconnecting.show()
        }
    }

    fun hideProgressReconecting(){
        if (progressReconnecting.isShowing) progressReconnecting.dismiss()
    }

    fun hideProgress() {
        if (progress.isShowing) progress.dismiss()
    }

    fun showInfoSnackbar(text: String, onDismissListener: (() -> Unit) = { }) {
        CustomSnackbarView.make(
            rootView,
            text,
            CustomSnackbarView.SnackbarType.INFO,
            true,
            onDismissListener
        ).show()
    }

    fun showSuccessSnackbar(text: String,screenAtTop : Boolean?=false, onDismissListener: (() -> Unit) = { }) {
        val snackbarview = CustomSnackbarView.make(
            rootView,
            text,
            CustomSnackbarView.SnackbarType.SUCCESS,
            true,
            onDismissListener
        )
      if (screenAtTop == true) {
          snackbarview.view
          val params = snackbarview.view.layoutParams as FrameLayout.LayoutParams
          params.gravity  = Gravity.TOP
          snackbarview.view.layoutParams = params
          snackbarview.show()
      }else{
          snackbarview.show()
      }
    }

    fun showErrorSnackbar(text: String,screenAtTop : Boolean?=false, onDismissListener: (() -> Unit) = { }) {
        val snackbarview = CustomSnackbarView.make(
            rootView,
            text,
            CustomSnackbarView.SnackbarType.ERROR,
            true,
            onDismissListener
        )
        if (screenAtTop == true){
            snackbarview.view
            val params = snackbarview.view.layoutParams as FrameLayout.LayoutParams
            params.gravity  = Gravity.TOP
            snackbarview.view.layoutParams = params
            snackbarview.show()
        }else{
            snackbarview.show()
        }
    }

    fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    open fun handleFailure(failure: Failure?) {
        when (failure) {
            is Failure.NetworkConnection -> showErrorSnackbar(getString(R.string.error_disconnect))
            is Failure.ServerError -> {
                showErrorSnackbar(failure.message)
                Sentry.captureMessage(failure.message)
            }
            is Failure.ExpiredSession -> {
                //set logout moengage
                MoEHelper.getInstance(application).logoutUser()
                showToast(getString(R.string.session_expired_error_toast))
                startActivity(
                    LoginActivityRouter.createIntent(this)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                )
                finish()
            }
            else -> {
                showErrorSnackbar(getString(R.string.error_default))
                Sentry.captureMessage(failure.toString() ?: "-")
            }
        }

    }
}