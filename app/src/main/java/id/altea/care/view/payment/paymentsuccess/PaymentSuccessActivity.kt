package id.altea.care.view.payment.paymentsuccess

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.adapters.ItemAdapter
import id.altea.care.R
import id.altea.care.core.base.BaseActivityVM
import id.altea.care.core.domain.model.ConsultationDetail
import id.altea.care.core.exception.Failure
import id.altea.care.core.ext.*
import id.altea.care.databinding.ActivityPaymentSuccessBinding
import id.altea.care.view.consultation.failure.PaymentPageFailure
import id.altea.care.view.consultation.item.PaymentFeeItem

class PaymentSuccessActivity : BaseActivityVM<ActivityPaymentSuccessBinding, PaymentSuccessVM>() {

    private val router by lazy { PaymentSuccessRouter() }
    private var appointmentId: Int? = null

    private val itemAdapter = ItemAdapter<PaymentFeeItem>()
    private val fastAdapter = FastAdapter.with(itemAdapter)

    override fun enableBackButton(): Boolean = false

    override fun bindToolbar(): Toolbar? = viewBinding?.appbar?.toolbar

    override fun getUiBinding(): ActivityPaymentSuccessBinding {
        return ActivityPaymentSuccessBinding.inflate(layoutInflater)
    }

    override fun bindViewModel(): PaymentSuccessVM {
        return ViewModelProvider(this, viewModelFactory)[PaymentSuccessVM::class.java]
    }

    override fun onFirstLaunch(savedInstanceState: Bundle?) {
        appointmentId = intent.getIntExtra(EXTRA_APPOINTMENT_ID, 0)
        viewBinding?.run {
            appbar.txtToolbarTitle.text = getString(R.string.payment_string)

            paymentSuccessRecycler.let {
                it.layoutManager = LinearLayoutManager(this@PaymentSuccessActivity)
                it.adapter = fastAdapter
            }
        }
        baseViewModel?.doRequestDetailAppointment(appointmentId!!)
    }

    override fun initUiListener() {
        viewBinding?.run {
            paymentBtnHome.onSingleClick()
                .subscribe { router.openHome(this@PaymentSuccessActivity) }
                .disposedBy(disposable)

            paymentBtnMyAppointment.onSingleClick()
                .subscribe { router.openMyConsultation(this@PaymentSuccessActivity) }
                .disposedBy(disposable)

            contentErrorRetry.contentErrorBtnRetry.onSingleClick()
                .subscribe { baseViewModel?.doRequestDetailAppointment(appointmentId!!) }
                .disposedBy(disposable)
        }
    }

    override fun onBackPressed() {
        showDialogBackConfirmationPayment(this, R.string.payment_success_back_dialog_confirmation) {
            router.openMyConsultation(this)
        }
    }

    override fun observeViewModel(viewModel: PaymentSuccessVM) {
        observe(viewModel.failureLiveData, ::handleFailure)
        observe(viewModel.isLoadingLiveData, ::handleLoading)
        observe(viewModel.appointmentDetailEvent, ::handleAppointmentDetailEvent)
    }

    override fun handleFailure(failure: Failure?) {
        if (failure is PaymentPageFailure.DetailAppointmentUnload) {
            viewBinding?.contentErrorRetry?.root?.isVisible = true
            viewBinding?.contentConstraint?.isVisible = false
        } else {
            super.handleFailure(failure)
        }
    }

    private fun handleAppointmentDetailEvent(consultationDetail: ConsultationDetail?) {
        consultationDetail?.let {
            viewBinding?.run {
                viewDoctor.consultationImgDoctor.loadImage(it.doctor?.photo.orEmpty())
                viewDoctor.consultationImgRs.loadImage(it.doctor?.hospital?.icon.orEmpty())
                viewDoctor.consultationTxtNameDoctor.text = it.doctor?.name
                viewDoctor.consultationTxtHospital.text = it.doctor?.hospital?.name
                viewDoctor.consultationTxtSpesialis.text = it.doctor?.specialist?.name
                viewDoctor.consultationTxtCalendar.text = it.schedule?.date
                    ?.toNewFormat("yyyy-MM-dd", "EEEE, dd MMMM yyyy", true)

                viewDoctor.consultationTxtTime.text = it.schedule?.schedule
                paymentTextOrderIdNumber.text = it.orderCode

                paymentTextPriceTotal.text = (it.totalPrice ?: 0).toLong().toRupiahFormat()

                it.fees?.map { itemAdapter.add(PaymentFeeItem(it)) }

                contentErrorRetry.root.isVisible = false
                contentConstraint.isVisible = true
            }
        }
    }

    companion object {
        private const val EXTRA_APPOINTMENT_ID = "PaymentSuccess.appointmentId"
        fun createIntent(caller: Context, appointmentId: Int): Intent {
            return Intent(caller, PaymentSuccessActivity::class.java)
                .putExtra(EXTRA_APPOINTMENT_ID, appointmentId)
        }
    }

}
