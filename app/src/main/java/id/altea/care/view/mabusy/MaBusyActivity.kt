package id.altea.care.view.mabusy

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import id.altea.care.core.base.BaseActivityVM
import id.altea.care.core.domain.model.UserProfile
import id.altea.care.core.exception.Failure
import id.altea.care.core.ext.disposedBy
import id.altea.care.core.ext.observe
import id.altea.care.core.ext.onSingleClick
import id.altea.care.databinding.ActivityCallBusyBinding
import id.altea.care.view.login.LoginActivity


class MaBusyActivity : BaseActivityVM<ActivityCallBusyBinding,MaBusyVM>() {

    val router = MaBusyRouter()

    val appointmentId by lazy {
        intent.getIntExtra(EXTRA_APPOINTMENT_ID,0)
    }
    override fun bindToolbar(): Toolbar?  = null


    override fun enableBackButton(): Boolean = false

    override fun getUiBinding(): ActivityCallBusyBinding {
        return ActivityCallBusyBinding.inflate(layoutInflater)
    }

    override fun onFirstLaunch(savedInstanceState: Bundle?) {

    }

    override fun initUiListener() {
        viewBinding?.run {

        }
    }

    override fun onBackPressed() {

    }

    override fun handleFailure(failure: Failure?) {
        when (failure) {
            is Failure.ServerError -> router.openCancelStatus(this@MaBusyActivity,false)
            is Failure.NetworkConnection -> router.openCancelStatus(this@MaBusyActivity,false)
            else -> super.handleFailure(failure)
        }
    }

    override fun bindViewModel(): MaBusyVM {
        return ViewModelProvider(this, viewModelFactory).get(MaBusyVM::class.java)
    }

    override fun observeViewModel(viewModel: MaBusyVM) {
        observe(viewModel.isLoadingLiveData, ::handleLoading)
        observe(viewModel.failureLiveData, ::handleFailure)
        observe(viewModel.resultEvent, { router.openCancelStatus(this@MaBusyActivity,it) })
    }

    companion object {
        val EXTRA_APPOINTMENT_ID = "MaBusy.AppointmentId"
        fun createIntent(caller: Context,appointmentId : Int): Intent {
            return Intent(caller, MaBusyActivity::class.java)
                .putExtra(EXTRA_APPOINTMENT_ID, appointmentId)
        }
    }

}