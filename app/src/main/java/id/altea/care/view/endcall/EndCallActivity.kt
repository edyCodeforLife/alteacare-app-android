package id.altea.care.view.endcall

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import id.altea.care.R
import id.altea.care.core.base.BaseActivityVM
import id.altea.care.core.domain.model.ConsultationDetail
import id.altea.care.core.ext.disposedBy
import id.altea.care.core.ext.observe
import id.altea.care.core.ext.onSingleClick
import id.altea.care.core.ext.showDialogBackConfirmationPayment
import id.altea.care.databinding.ActivityEndCallBinding
import id.altea.care.view.common.enums.TypeAppointment
import kotlinx.android.synthetic.main.activity_end_call.*


class EndCallActivity : BaseActivityVM<ActivityEndCallBinding, EndCallVM>() {

    private val viewModel by viewModels<EndCallVM> { viewModelFactory }
    private val router = EndCallActivityRouter()
    private val textTimer: String? by lazy {
        intent.getStringExtra(KEY_COUNT_TIME)
    }

    private val appointmentId by lazy { intent.getIntExtra(EXTRA_APPOINTMENT_ID, 0) }
    private val orderCode by lazy { intent.getStringExtra(EXTRA_ORDER_CODE) }

    override fun observeViewModel(viewModel: EndCallVM) {
        observe(viewModel.failureLiveData, ::handleFailure)
        observe(viewModel.isLoadingLiveData, ::handleLoadingBar)
        observe(viewModel.detailAppointmentEvent, ::handleEventAppointmentDetail)
    }

    private fun handleLoadingBar(showLoading: Boolean?) {
        viewBinding?.progress?.isVisible = showLoading == true
    }

    private fun handleEventAppointmentDetail(consultationDetail: ConsultationDetail?) {
        consultationDetail?.let {
            if (it.status.equals(TypeAppointment.WAITING_FOR_PAYMENT.name, true)) {
                handleViewToPayment()
            } else if (it.status.equals(TypeAppointment.CANCELED_BY_GP.name, true)) {
                handleViewCancel()
            }
        }
    }

    private fun handleViewToPayment() {
        viewBinding?.run {
            endCallConsultationTxtMessage.text = getText(R.string.description_waiting_end_call)
            endCallBtnToMyConsultation.isVisible = false
            endCallBtnPayment.isEnabled = true
        }
        viewModel.endScheduler()
    }

    private fun handleViewCancel() {
        viewBinding?.run {
            endCallConsultationTxtMessage.text = getText(R.string.description_canceled_end_call)
            endCallBtnPayment.isVisible = false
            endCallConsultationTxtMessage.setTextColor(
                ContextCompat.getColor(
                    this@EndCallActivity,
                    R.color.red
                )
            )
        }
        viewModel.endScheduler()
    }

    override fun bindViewModel(): EndCallVM = viewModel

    override fun enableBackButton(): Boolean = false

    override fun bindToolbar(): Toolbar? = null

    override fun getUiBinding(): ActivityEndCallBinding =
        ActivityEndCallBinding.inflate(layoutInflater)

    override fun onFirstLaunch(savedInstanceState: Bundle?) {
        viewBinding?.run {
            endCallTextTimer.text = textTimer
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.startScheduler(appointmentId)
        viewModel.schedulerPaymentCheck(appointmentId)
    }

    override fun onPause() {
        super.onPause()
        viewModel.endScheduler()
    }

    override fun initUiListener() {
        viewBinding?.run {
            endCallBtnPayment.onSingleClick()
                .subscribe {
                    router.openPayment(
                        this@EndCallActivity,
                        orderCode,
                        appointmentId
                    )
                }.disposedBy(disposable)

            endCallBtnToMyConsultation.onSingleClick()
                .subscribe {
                    if (endCallConsultationTxtMessage.text.equals(getText(R.string.description_canceled_end_call))){
                        router.openMainActivity(this@EndCallActivity,2)
                    }else{
                        router.openMainActivity(this@EndCallActivity)
                    }
                }.disposedBy(disposable)
        }


    }

    override fun onBackPressed() {
        showDialogBackConfirmationPayment(this) {
            router.openMainActivity(this)
        }
    }

    companion object {
        private const val KEY_COUNT_TIME = "KEY_COUNT_TIME"
        private const val EXTRA_ORDER_CODE = "EndCallActivity.OrderCode"
        private const val EXTRA_APPOINTMENT_ID = "EndCallActivity.AppointmentId"

        fun createIntent(
            caller: Context,
            value: String,
            orderCode: String,
            appointmentId: Int
        ): Intent {
            return Intent(caller, EndCallActivity::class.java)
                .putExtra(KEY_COUNT_TIME, value)
                .putExtra(EXTRA_ORDER_CODE, orderCode)
                .putExtra(EXTRA_APPOINTMENT_ID, appointmentId)

        }
    }
}