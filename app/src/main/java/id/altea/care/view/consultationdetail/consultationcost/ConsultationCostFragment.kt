package id.altea.care.view.consultationdetail.consultationcost

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.adapters.ItemAdapter

import com.tonyodev.fetch2.FetchConfiguration
import com.tonyodev.fetch2.Status
import id.altea.care.core.base.BaseFragmentVM
import id.altea.care.core.domain.model.ConsultationFee
import id.altea.care.core.domain.model.CostConsul
import id.altea.care.core.domain.model.Invoice
import id.altea.care.core.domain.model.TransactionConsulDetail
import id.altea.care.core.ext.*
import id.altea.care.core.helper.util.Constant
import id.altea.care.databinding.FragmentConsultationCostBinding
import id.altea.care.view.consultationdetail.ConsultationDetailActivity
import id.altea.care.view.consultationdetail.ConsultationDetailSharedVM
import id.altea.care.view.consultationdetail.consultationcost.item.ContentConsultationPriceItem
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.fragment_consultation_cost.*
import java.io.File

class ConsultationCostFragment :
    BaseFragmentVM<FragmentConsultationCostBinding, ConsultationDetailSharedVM>() {
    private val itemAdapter = ItemAdapter<ContentConsultationPriceItem>()
    private val fastAdapter = FastAdapter.with(itemAdapter)
    private var isLoading = false
    private var consultationId: Int = 0
    private var fetchConfiguration: FetchConfiguration? = null
    private var invoice : Invoice? = null
    private val router = ConsultationCostRouter()

    override fun observeViewModel(viewModel: ConsultationDetailSharedVM) {
        observe(viewModel.failureLiveData, ::handleFailure)

        viewModel.isShowLoading
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                isLoading = it
                handleLoadingLivedataEvent(isLoading)
            }
            .disposedBy(disposable)

        viewModel.statusDownloadPatientCost
            .subscribe { getStatusDownload(it) }
            .disposedBy(disposable)

        viewModel.progressDownloadCostPatient
            .subscribe { getProgressDownload(it) }
            .disposedBy(disposable)

        viewModel.appointmentDetail
            .subscribe {
                getTransactionConsulDetail(it.transactionConsulDetail)
                getTotalPriceResult(it.totalPrice)
                getFeesConsulDetail(it.fees)
                getInvoice(it.invoice)
            }
            .disposedBy(disposable)
    }

    private fun getProgressDownload(progress : Pair<Int, Int>?) {
        viewBinding?.costConsultationProgressBar?.progress = progress?.first ?: 0
    }

    private fun getStatusDownload(status: Pair<Status, Int>?) {
        viewBinding?.run {
            status?.first?.let {
                when(it){
                    Status.DOWNLOADING, Status.QUEUED, Status.ADDED ->{
                        costConsultationProgressBar.isVisible = true
                        download_receipt_button.isVisible = false
                    }
                    Status.COMPLETED ->{
                        costConsultationProgressBar.isVisible = false
                        download_receipt_button.isVisible = true
                        val filePath = File(getPathLocal() + invoice?.originalName.orEmpty())

                        router.openViewDocument(
                            requireActivity(),
                            filePath.path.orEmpty(),
                            invoice?.originalName.orEmpty()
                        )

                    }
                }
            }
        }
    }


    private fun getInvoice(invoice: Invoice?) {
        if (!invoice?.originalName.isNullOrEmpty()) {
            this.invoice = invoice
            download_receipt_button.isVisible = true
        }else{
            download_receipt_button.isVisible = false
        }
    }

    private fun getPathLocal(): String {
        return requireActivity().getTempFolder().absolutePath
    }

    private fun pathToDownload(nameFile: String): Boolean {
        val filePath = File(getPathLocal() + nameFile)
        return filePath.isFile
    }

    override fun onResume() {
        super.onResume()
        handleLoadingLivedataEvent(isLoading)
    }

    override fun onPause() {
        super.onPause()
        handleLoadingLivedataEvent(false)
    }

    private fun getTotalPriceResult(totalPrice: Int?) {
        viewBinding?.consultationCostTxtTotalPrice?.text =
            (totalPrice ?: 0).toLong().toRupiahFormat()
    }


    override fun bindViewModel(): ConsultationDetailSharedVM {
        return ViewModelProvider(
            requireActivity(),
            viewModelFactory
        )[ConsultationDetailSharedVM::class.java]
    }

    override fun getUiBinding(): FragmentConsultationCostBinding {
        return FragmentConsultationCostBinding.inflate(layoutInflater)
    }

    override fun onFirstLaunch(savedInstanceState: Bundle?, view: View) {
        viewBinding?.run {
            rcyrlConsultationCost.let {
                it.layoutManager = LinearLayoutManager(requireContext())
                it.adapter = fastAdapter
            }
        }
        fetchConfiguration = FetchConfiguration.Builder(requireContext())
            .createDownloadFileOnEnqueue(false)
            .enableAutoStart(true)
            .enableFileExistChecks(true)
            .enableRetryOnNetworkGain(true)
            .setAutoRetryMaxAttempts(2)
            .build()
        consultationId = (activity as ConsultationDetailActivity).consultationId
    }

    private fun getTransactionConsulDetail(transactionConsulDetail: TransactionConsulDetail?) {
        if (transactionConsulDetail?.detailTransaction != null) {
            costConsultationContentEmpty.isVisible = false
            viewBinding?.run {
                consultationCostTxtPaymentMethod.text =
                    transactionConsulDetail.detailTransaction.name ?: "-"
                consultationCostTxtPaymentMethod.loadImage(transactionConsulDetail.detailTransaction.icon.orEmpty())
            }
        } else {
            costConsultationContentEmpty.isVisible = true
        }
    }

    private fun getFeesConsulDetail(consultationFees: List<ConsultationFee>?) {
        if (consultationFees?.isNullOrEmpty() == false) {
            costConsultationContentEmpty.isVisible = false
            itemAdapter.clear()
            itemAdapter.add(consultationFees.map {
                ContentConsultationPriceItem(
                    CostConsul(
                        it.label,
                        (it.amount ?: 0).toRupiahFormat()
                    )
                )
            })

        } else {
            costConsultationContentEmpty.isVisible = true
        }
    }

    override fun onReExecute() {

    }

    private fun handleLoadingLivedataEvent(showLoading: Boolean?) {
        viewBinding?.run { onConsultationCostSwipe.isRefreshing = showLoading == true }
    }

    override fun initUiListener() {
        viewBinding?.run {
            onConsultationCostSwipe.setOnRefreshListener {
                viewModel?.getConsultationDetail(consultationId)
            }
            download_receipt_button.onSingleClick().subscribe {
                invoice?.let { item ->
                    if (!pathToDownload(item.originalName.orEmpty()) && item.url.orEmpty().contains(
                            "pdf"
                        )
                    ) {
                        viewModel?.startDownload(
                            item.url.orEmpty(), getPathLocal(),
                            item.originalName.orEmpty(), 0, fetchConfiguration!!,
                            false, true
                        )
                    }else{
                        val filePath = File(getPathLocal() + item.originalName.orEmpty())
                        router.openViewDocument(
                            requireActivity(),
                            filePath.path.orEmpty(),
                            item.originalName.orEmpty()
                        )

                    }
                }
            }.disposedBy(disposable)
        }
    }

    override fun initMenu(): Int = 0

    companion object {
        fun newInstance(): ConsultationCostFragment {
            return ConsultationCostFragment().apply {
            }
        }
    }
}