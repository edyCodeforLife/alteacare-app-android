package id.altea.care.view.consultationdetail.canceled

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import id.altea.care.R
import id.altea.care.core.base.BaseActivityVM
import id.altea.care.core.domain.model.ConsultationDetail
import id.altea.care.core.ext.loadImage
import id.altea.care.core.ext.observe
import id.altea.care.databinding.ActivityConsultationCancelBinding
import id.altea.care.view.common.enums.TypeAppointment
import kotlin.properties.Delegates

class ConsultationCancelActivity :
    BaseActivityVM<ActivityConsultationCancelBinding, ConsultationCancelVM>() {

    private var appointmentId by Delegates.notNull<Int>()

    override fun enableBackButton(): Boolean = true

    override fun bindToolbar(): Toolbar? = viewBinding?.appbar?.toolbar

    override fun getUiBinding(): ActivityConsultationCancelBinding {
        return ActivityConsultationCancelBinding.inflate(layoutInflater)
    }

    override fun onFirstLaunch(savedInstanceState: Bundle?) {
        appointmentId = intent.getIntExtra(EXTRA_APPOINTMENT_ID, 0)
        baseViewModel?.getConsultationDetail(appointmentId)
    }

    override fun observeViewModel(viewModel: ConsultationCancelVM) {
        observe(viewModel.failureLiveData, ::handleFailure)
        observe(viewModel.isLoadingLiveData, ::handleLoading)
        observe(viewModel.detailAppointmentEvent, ::handleAppointmentDetailEvent)
    }

    override fun bindViewModel(): ConsultationCancelVM {
        return ViewModelProvider(this, viewModelFactory)[ConsultationCancelVM::class.java]
    }

    override fun initUiListener() {}

    private fun handleAppointmentDetailEvent(appointmentDetail: ConsultationDetail?) {
        appointmentDetail?.let {
            viewBinding?.run {
                contentDoctor.docProfileArrow.isVisible = false
                contentDoctor.docProfileTxtDoctorName.text = it.doctor?.name
                contentDoctor.docProfileTxtRsName.text = it.doctor?.hospital?.name
                contentDoctor.docProfileTxtDoctorTitle.text = it.doctor?.specialist?.name
                contentDoctor.docProfileImgDoctor.loadImage(it.doctor?.photo.orEmpty())
                contentDoctor.docProfileImgRsIcon.loadImage(it.doctor?.hospital?.icon.orEmpty())
                paymentOverTxtOrderId.text =
                    String.format(getString(R.string.order_id_s), it.orderCode)
                paymentOverTxtDate.text = it.schedule?.date
                paymentOverTxtTime.text = it.schedule?.schedule

                it.status?.let {
                    when (TypeAppointment.valueOf(it)) {
                        TypeAppointment.REFUND,
                        TypeAppointment.REFUNDED -> {
                            appbar.txtToolbarTitle.text = getString(R.string.refund)
                            paymentOverTxtTitle.text = getString(R.string.refund_payment_title)
                            paymentOverTxtTitle.setTextColor(getColor(R.color.blueDarker))
                            paymentOverTxtNote.text = getString(R.string.refund_payment_note)
                        }
                        TypeAppointment.PAYMENT_EXPIRED -> {
                            appbar.txtToolbarTitle.text = getString(R.string.payment_is_over)
                            paymentOverTxtTitle.text = getString(R.string.over_payment_title)
                            paymentOverTxtNote.text = getString(R.string.over_payment_note)
                        }
                        else -> {
                            appbar.txtToolbarTitle.text = getString(R.string.canceled)
                            paymentOverTxtTitle.text = getString(R.string.appointment_cancel_note)
                            paymentOverTxtNote.isVisible = false
                        }
                    }
                }
            }
        }
    }

    companion object {
        private const val EXTRA_APPOINTMENT_ID = "ConsultationCancel.APPOINTMENT_ID"
        fun createIntent(
            caller: Context,
            appointmentId: Int
        ): Intent {
            return Intent(caller, ConsultationCancelActivity::class.java)
                .putExtra(EXTRA_APPOINTMENT_ID, appointmentId)
        }
    }
}
