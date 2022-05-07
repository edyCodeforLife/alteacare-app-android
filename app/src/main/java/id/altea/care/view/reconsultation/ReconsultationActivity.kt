package id.altea.care.view.reconsultation


import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import dagger.android.AndroidInjection
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import id.altea.care.core.base.BaseActivity
import id.altea.care.core.base.BaseActivityVM
import id.altea.care.core.base.BaseViewModel
import id.altea.care.core.domain.model.CountDownState
import id.altea.care.core.domain.model.InfoDetail
import id.altea.care.core.domain.model.UserProfile
import id.altea.care.core.exception.Failure
import id.altea.care.core.ext.disposedBy
import id.altea.care.core.ext.observe
import id.altea.care.core.ext.onSingleClick
import id.altea.care.databinding.ActivityReconsultationBinding
import id.altea.care.view.cancelstatusorder.CancelStatusActvity
import id.altea.care.view.consultation.ConsultationFragment
import id.altea.care.view.transition.TransitionActivity
import id.altea.care.view.transition.TransitionVM
import javax.inject.Inject

class ReconsultationActivity : BaseActivityVM<ActivityReconsultationBinding,ReconsultationVM>() {

    private val appointmentId: Int by lazy { intent.getIntExtra(EXTRA_APPOINTMENT_ID, 0) }
    private val orderCode by lazy { intent?.getStringExtra(EXTRA_ORDER_CODE) ?: "" }
    private val userProfile by lazy { intent.getParcelableExtra(ConsultationFragment.KEY_DATA_PATIENT) as UserProfile? }
    private val infoDetail by  lazy { intent.getParcelableExtra(EXTRA_INFO_DETAIL) as InfoDetail? }


    val router = ReconsultationRouter()

    override fun bindToolbar(): Toolbar?  = null

    override fun enableBackButton(): Boolean  = false

    override fun getUiBinding(): ActivityReconsultationBinding  = ActivityReconsultationBinding.inflate(layoutInflater)

    override fun onFirstLaunch(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)


    }

    override fun initUiListener() {
        viewBinding?.run {
            reconsultationBtnCallAgain.onSingleClick().subscribe {
                router.openCallWithMA(this@ReconsultationActivity,appointmentId,orderCode,userProfile ?: UserProfile("",""),infoDetail)
            }.disposedBy(disposable)

            reconsultationBtnCancelCall.onSingleClick().subscribe {
                router.openCancelCallBottomsheet(this@ReconsultationActivity.supportFragmentManager,{submitCallBack ->
                    if (submitCallBack){
                        router.openOptionCallBottomSheet(this@ReconsultationActivity.supportFragmentManager,{ submitCallBack ->
                            baseViewModel?.setCancelReasonOrder(appointmentId,submitCallBack.toString())
                        },{dissmissCallback ->

                        })
                    }
                },{ dissmissCallBack ->

                })
            }.disposedBy(disposable)
        }
    }

    override fun handleFailure(failure: Failure?) {
        when (failure) {
            is Failure.ServerError -> router.openCancelStatus(this,false)
            is Failure.NetworkConnection -> router.openCancelStatus(this,false)
            else -> super.handleFailure(failure)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }

    override fun androidInjector(): AndroidInjector<Any> = androidInjector

    companion object {
        private const val EXTRA_APPOINTMENT_ID = "ReconsultationExtra.AppointmentId"
        private const val EXTRA_ORDER_CODE = "ReconsultationExtra.OrderCode"
        private const val EXTRA_INFO_DETAIL = "ReconsultationExtra.InfoDetail"
        fun createIntent(
            caller: Context,
            appointmentId: Int,
            orderCode: String?,
            profile: UserProfile,
            infoDetail: InfoDetail?
        ): Intent {
            return Intent(caller, ReconsultationActivity::class.java)
                .putExtra(EXTRA_APPOINTMENT_ID, appointmentId)
                .putExtra(EXTRA_ORDER_CODE, orderCode)
                .putExtra(ConsultationFragment.KEY_DATA_PATIENT, profile)
                .putExtra(EXTRA_INFO_DETAIL,infoDetail)
        }
    }

    override fun bindViewModel(): ReconsultationVM {
        return ViewModelProvider(this, viewModelFactory)[ReconsultationVM::class.java]
    }

    override fun observeViewModel(viewModel: ReconsultationVM) {
        observe(viewModel.isLoadingLiveData, ::handleLoading)
        observe(viewModel.failureLiveData, ::handleFailure)
        observe(viewModel.resultEvent, { router.openCancelStatus(this@ReconsultationActivity,it) })
    }
}