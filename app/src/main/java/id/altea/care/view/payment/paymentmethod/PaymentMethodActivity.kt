package id.altea.care.view.payment.paymentmethod

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.midtrans.sdk.corekit.core.MidtransSDK
import com.midtrans.sdk.corekit.core.TransactionRequest
import com.midtrans.sdk.corekit.models.snap.TransactionResult
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.IItem
import com.mikepenz.fastadapter.adapters.ItemAdapter
import id.altea.care.R
import id.altea.care.core.base.BaseActivityVM
import id.altea.care.core.domain.model.Payment
import id.altea.care.core.domain.model.PaymentTypes
import id.altea.care.core.ext.observe
import id.altea.care.core.ext.showPermissionSettingDialog
import id.altea.care.core.ext.showRationaleDialog
import id.altea.care.databinding.ActivityPaymentMethodBinding
import id.altea.care.view.payment.item.PaymentBodyItem
import id.altea.care.view.payment.item.PaymentTitleItem
import kotlinx.android.synthetic.main.activity_payment_method.*
import kotlinx.android.synthetic.main.toolbar_default_center.view.*
import permissions.dispatcher.*

@RuntimePermissions
class PaymentMethodActivity : BaseActivityVM<ActivityPaymentMethodBinding, PaymentMethodVM>() {

    private val paymentItemAdapter = ItemAdapter<IItem<*>>()
    private val paymentAdapter = FastAdapter.with(paymentItemAdapter)
    private val appointmentId by lazy { intent.getIntExtra(EXTRA_APPOINTMENT_ID, 0) }
    private val orderCode by lazy { intent.getStringExtra(EXTRA_ORDER_CODE) }
    private val router by lazy { PaymentMethodRouter() }
    private val voucherCode by lazy { intent.getStringExtra(EXTRA_VOUCHER_CODE) }

    private lateinit var paymentTypes: ArrayList<PaymentTypes>

    override fun observeViewModel(viewModel: PaymentMethodVM) {
        observe(viewModel.failureLiveData, ::handleFailure)
        observe(viewModel.isLoadingLiveData, ::handleLoading)
        observe(viewModel.createPaymentEvent, ::handleResponseCreatePayment)
        observe(viewModel.redirectUrlEvent, ::handleResponseWebViewPayment)
    }

    private fun handleResponseCreatePayment(payment: Payment?) {
        payment?.let {
            val trx = TransactionRequest(it.refId.orEmpty(), it.total ?: (0).toDouble())
            MidtransSDK.getInstance().apply {
                uiKitCustomSetting.isSkipCustomerDetailsPages = true
                transactionRequest = trx
                startPaymentUiFlow(this@PaymentMethodActivity, payment.token)
            }.setTransactionFinishedCallback { handleCallbackMidtrans(it) }
        }
    }

    private fun handleResponseWebViewPayment(url: String?) {
        router.openPaymentInformation(
            this,
            appointmentId,
            url.orEmpty(),
            orderCode!!
        )
    }

    private fun handleCallbackMidtrans(transactionResult: TransactionResult) {
        if (transactionResult.isTransactionCanceled) {
            return
        }
        when (transactionResult.status) {
            TransactionResult.STATUS_SUCCESS -> {
                router.openPaymentSuccess(this, appointmentId)
            }
            else -> {
                showInfoSnackbar(getString(R.string.payment_cancel_message))
            }
        }
    }

    override fun bindViewModel(): PaymentMethodVM {
        return ViewModelProvider(this, viewModelFactory)[PaymentMethodVM::class.java]
    }

    override fun enableBackButton(): Boolean = false

    override fun bindToolbar(): Toolbar? = viewBinding?.appbar?.toolbar

    override fun getUiBinding(): ActivityPaymentMethodBinding {
        return ActivityPaymentMethodBinding.inflate(layoutInflater)
    }

    override fun onFirstLaunch(savedInstanceState: Bundle?) {
        paymentTypes =
            intent.getParcelableArrayListExtra<PaymentTypes>(EXTRA_PAYMENT_TYPES) as ArrayList<PaymentTypes>
        viewBinding.run {
            initRecycleView()
            appbar.txtToolbarTitle.text = getString(R.string.payment_method)
        }
        paymentTypes.map { paymentType ->
            paymentItemAdapter.add(PaymentTitleItem(paymentType.type.orEmpty()))
            paymentType.paymentMethods?.map {
                paymentItemAdapter.add(PaymentBodyItem(it))
            }
        }

        paymentAdapter.onClickListener = { _, _, item, _ ->
            if (item is PaymentBodyItem) {
                // todo when credit card please check permission read phone state
                if (item.item.code == "credit_card" || item.item.code == "gopay") {
                    doCreatePaymentWithPermissionCheck(appointmentId, item.item.code.orEmpty(),voucherCode)
                } else {
                    doCreatePayment(appointmentId, item.item.code.orEmpty(),voucherCode)
                }
            }
            false
        }
    }

    @NeedsPermission(Manifest.permission.READ_PHONE_STATE)
    fun doCreatePayment(appointmentId: Int, code: String,voucherCode: String?) {
        baseViewModel?.doCreatePayment(appointmentId, code,voucherCode)
    }

    @OnShowRationale(Manifest.permission.READ_PHONE_STATE)
    fun onCreatePayment(request: PermissionRequest) {
        showRationaleDialog(this, R.string.permission_phone_state_deny, request)
    }

    @OnPermissionDenied(Manifest.permission.READ_PHONE_STATE)
    fun onPermissionDenied() {
        showPermissionSettingDialog(this, R.string.permission_phone_state_deny)
    }

    @OnNeverAskAgain(Manifest.permission.READ_PHONE_STATE)
    fun onNeverAsk() {
        showPermissionSettingDialog(this, R.string.permission_phone_state_deny)
    }

    @Suppress("DEPRECATION")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        onRequestPermissionsResult(requestCode, grantResults)
    }

    override fun initUiListener() {}

    private fun initRecycleView() {
        paymentRecyclerMethod.let {
            it.layoutManager = LinearLayoutManager(this)
            it.adapter = paymentAdapter
        }
    }

    companion object {
        private const val EXTRA_PAYMENT_TYPES = "PaymentMethod.PaymentTypes"
        private const val EXTRA_APPOINTMENT_ID = "PaymentMethod.AppointmentId"
        private const val EXTRA_ORDER_CODE = "PaymentMethod.OrderCode"
        private const val EXTRA_VOUCHER_CODE = "PaymentMethod.VoucherCode"
        fun createIntent(
            caller: Context,
            paymentTypes: List<PaymentTypes>,
            appointmentId: Int,
            orderCode: String,
            voucherCode : String?
        ): Intent {
            val array = arrayListOf<PaymentTypes>()
            paymentTypes.forEach { array.add(it) }
            return Intent(caller, PaymentMethodActivity::class.java)
                .putParcelableArrayListExtra(EXTRA_PAYMENT_TYPES, array)
                .putExtra(EXTRA_APPOINTMENT_ID, appointmentId)
                .putExtra(EXTRA_ORDER_CODE, orderCode)
                .putExtra(EXTRA_VOUCHER_CODE,voucherCode)
        }
    }
}

