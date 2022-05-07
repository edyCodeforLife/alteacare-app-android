package id.altea.care.view.consultation.payment

import android.app.Activity
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.adapters.ItemAdapter
import id.altea.care.R
import id.altea.care.core.base.BaseActivity
import id.altea.care.core.base.BaseFragmentVM
import id.altea.care.core.domain.model.ConsultationDetail
import id.altea.care.core.domain.model.PaymentTypes
import id.altea.care.core.domain.model.Voucher
import id.altea.care.core.exception.Failure
import id.altea.care.core.ext.*
import id.altea.care.databinding.FragmentPaymentMethodBinding
import id.altea.care.view.common.enums.TypeAppointment
import id.altea.care.view.consultation.failure.PaymentPageFailure
import id.altea.care.view.consultation.item.PaymentFeeItem
import id.altea.care.view.consultation.payment.item.ItemVoucher
import timber.log.Timber

class PaymentFragment : BaseFragmentVM<FragmentPaymentMethodBinding, PaymentFragmentVM>() {

    private val router by lazy { PaymentFragmentRouter() }
    private val appointmentId by lazy { arguments?.getInt(EXTRA_APPOINTMENT_ID) }

    private val itemAdapter = ItemAdapter<PaymentFeeItem>()
    private val fastAdapter = FastAdapter.with(itemAdapter)

    private val itemVoucherAdapter = ItemAdapter<ItemVoucher>()
    private val fastVoucherAdapter = FastAdapter.with(itemVoucherAdapter)
    private var voucher : Voucher? = null


    override fun observeViewModel(viewModel: PaymentFragmentVM) {
        observe(viewModel.failureLiveData, ::handleFailure)
        observe(viewModel.isLoadingLiveData, ::handleLoading)
        observe(viewModel.paymentTypesEvent, ::handlePaymentTypesResponse)
        observe(viewModel.appointmentDetailEvent, ::handleAppointmentDetailEvent)
    }

    private fun handleAppointmentDetailEvent(consultationDetail: ConsultationDetail?) {
        consultationDetail?.let {
            if (consultationDetail.status != null) {
                try {
                    val activity = requireActivity() as BaseActivity<*>
                    val typeAppointment = TypeAppointment.valueOf(consultationDetail.status)
                    if (typeAppointment.value >= TypeAppointment.CANCELED_BY_SYSTEM.value) {
                        showToast(activity.getString(R.string.description_canceled_end_call))
                        router.openCancelDetail(activity, appointmentId!!)
                        return
                    } else if (typeAppointment.value >= TypeAppointment.PAID.value) {
                        showToast(activity.getString(R.string.payment_success_info_snackbar))
                        router.openPaymentSuccess(activity, appointmentId!!)
                        return
                    }
                } catch (e: Exception) {
                    Timber.e(e)
                }
            }
            viewBinding?.run {
                viewDoctor.paymentImgDoctor.loadImage(it.doctor?.photo.orEmpty())
                viewDoctor.paymentImgRs.loadImage(it.doctor?.hospital?.icon.orEmpty())
                viewDoctor.paymentTxtNameDoctor.text = it.doctor?.name
                viewDoctor.paymentTxtHospital.text = it.doctor?.hospital?.name
                viewDoctor.paymentTxtSpesialis.text = it.doctor?.specialist?.name
                viewDoctor.paymentTxtCalendar.text = it.schedule?.date
                    ?.toNewFormat("yyyy-MM-dd", "EEEE, dd MMMM yyyy", true)

                viewDoctor.paymentTxtTime.text = it.schedule?.schedule

                paymentTextOrderIdNumber.text = it.orderCode
                paymentTextPriceTotal.text = (it.totalPrice ?: 0).toLong().toRupiahFormat()
                itemAdapter.clear()
                it.fees?.map {
                    itemAdapter.add(PaymentFeeItem(it))
                }
                contentErrorRetry.root.isVisible = false
                contentConstraint.isVisible = true
            }
        }
    }

    private fun handlePaymentTypesResponse(list: List<PaymentTypes>?) {
        list?.let {
            router.openPaymentMethod(
                requireActivity() as AppCompatActivity,
                it,
                appointmentId ?: 0,
                viewModel?.appointmentDetailEvent?.value?.orderCode ?: "",
                voucher?.voucherCode ?: ""
            )
        }
    }

    override fun bindViewModel(): PaymentFragmentVM {
        return ViewModelProvider(this, viewModelFactory).get(PaymentFragmentVM::class.java)
    }

    override fun getUiBinding(): FragmentPaymentMethodBinding {
        return FragmentPaymentMethodBinding.inflate(layoutInflater)
    }

    override fun onFirstLaunch(savedInstanceState: Bundle?, view: View) {
        viewBinding?.run {
            paymentRecyclerDetail.also {
                it.layoutManager = LinearLayoutManager(requireContext())
                it.adapter = fastAdapter
            }
            paymentRecyclerVoucher.also {
                it.layoutManager = LinearLayoutManager(requireContext())
                it.adapter = fastVoucherAdapter
            }

        }
        if (appointmentId != 0) {
            viewModel?.doRequestDetailAppointment(appointmentId!!)
        }
    }

    override fun handleFailure(failure: Failure?) {
        if (failure is PaymentPageFailure.DetailAppointmentUnload) {
            viewBinding?.contentErrorRetry?.root?.isVisible = true
            viewBinding?.contentConstraint?.isVisible = false
        } else {
            super.handleFailure(failure)
        }
    }

    override fun onReExecute() {}

    override fun initUiListener() {
        viewBinding?.run {
            paymentBtnMethod.onSingleClick()
                .subscribe {
                    viewModel?.doRequestPaymentTypes(
                        appointmentId ?: 0,
                        if (itemVoucherAdapter.adapterItemCount < 1) null
                        else itemVoucherAdapter.getAdapterItem(0).voucher.voucherCode
                    )
                }
                .disposedBy(disposable)

            contentErrorRetry.contentErrorBtnRetry.onSingleClick()
                .subscribe { viewModel?.doRequestDetailAppointment(appointmentId!!) }
                .disposedBy(disposable)

            paymentTextUseVoucher.onSingleClick().subscribe {
                router.openVoucherActivity(requireActivity() as AppCompatActivity,appointmentId.toString(),voucherLaucher)
            }.disposedBy(disposable)
        }

        fastVoucherAdapter.addEventHook(ItemVoucher.ItemVoucherSelectEvent {
            viewBinding?.paymentRecyclerVoucher?.isVisible = false
            viewBinding?.linearLayout5?.isVisible = true
            itemVoucherAdapter.remove(it)
            voucher = null
            if (appointmentId != 0) {
                viewModel?.doRequestDetailAppointment(appointmentId!!)
            }
            (activity as BaseActivity<*>).showInfoSnackbar(getString(R.string.str_delete_voucher))
        })
    }

    val voucherLaucher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        if (it.resultCode == Activity.RESULT_OK){
            viewBinding?.paymentRecyclerVoucher?.isVisible = true
            voucher = it.data?.getParcelableExtra<Voucher>("voucher")
            voucher?.let {
                itemVoucherAdapter.add(ItemVoucher(it))
                viewBinding?.paymentTextPriceTotal?.text = it.grandTotal
                viewBinding?.linearLayout5?.isVisible = false
            }
        }
    }


    override fun initMenu(): Int = 0

    companion object {
        private const val EXTRA_APPOINTMENT_ID = "PaymentFragment.AppointmentId"
        fun newInstance(appointmentId: Int): PaymentFragment {
            val args = Bundle()
            args.putInt(EXTRA_APPOINTMENT_ID, appointmentId)
            val fragment = PaymentFragment()
            fragment.arguments = args
            return fragment
        }
    }
}