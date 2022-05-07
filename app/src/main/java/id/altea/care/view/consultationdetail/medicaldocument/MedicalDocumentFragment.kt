package id.altea.care.view.consultationdetail.medicaldocument

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.IItem
import com.mikepenz.fastadapter.adapters.ItemAdapter
import com.mikepenz.fastadapter.binding.BindingViewHolder
import com.mikepenz.fastadapter.listeners.ClickEventHook
import com.tonyodev.fetch2.FetchConfiguration
import com.tonyodev.fetch2.Status
import id.altea.care.R
import id.altea.care.core.base.BaseFragmentVM
import id.altea.care.core.domain.model.ConsultationMedicalDocument
import id.altea.care.core.exception.Failure
import id.altea.care.core.exception.NotFoundFailure
import id.altea.care.core.ext.disposedBy
import id.altea.care.core.ext.getTempFolder
import id.altea.care.core.ext.observe
import id.altea.care.core.ext.openDocument
import id.altea.care.core.helper.util.Constant
import id.altea.care.databinding.FragmentMedicalDocumentBinding
import id.altea.care.databinding.ItemMedicalDocumentContentBinding
import id.altea.care.view.consultationdetail.ConsultationDetailActivity
import id.altea.care.view.consultationdetail.ConsultationDetailSharedVM
import id.altea.care.view.consultationdetail.medicaldocument.item.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import timber.log.Timber
import java.io.File

class MedicalDocumentFragment :
    BaseFragmentVM<FragmentMedicalDocumentBinding, ConsultationDetailSharedVM>() {

    private val itemAdapter = ItemAdapter<IItem<*>>()
    private val fastAdapter = FastAdapter.with(itemAdapter)
    private var consultationId: Int? = 0
    private val consultitle = ArrayList<String>()
    private var fetchConfiguration: FetchConfiguration? = null
    private var isLoading = false
    private val router = MedicalDocumentRouter()
    private val downloadDisposable = CompositeDisposable()

    override fun observeViewModel(viewModel: ConsultationDetailSharedVM) {
        observe(viewModel.failureLiveData, ::handleFailure)

        val progressObservable = viewModel.progressDownloadMedicalDoc
            .subscribe { }

        viewModel.statusDownloadMedicalDoc
            .subscribe { getStatusDownload(it) }
            .disposedBy(disposable)

        viewModel.isShowLoading
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                isLoading = it
                handleLoadingLivedataEvent(isLoading)
            }
            .disposedBy(disposable)

        viewModel.appointmentDetail
            .subscribe { getMedicalDocument(it.medicalDocument) }
            .disposedBy(disposable)
    }

    override fun onResume() {
        super.onResume()
        handleLoadingLivedataEvent(isLoading)
    }

    override fun onPause() {
        super.onPause()
        handleLoadingLivedataEvent(false)
    }

    private fun getMedicalDocument(medicalDocuments: List<ConsultationMedicalDocument>?) {
        if (medicalDocuments?.size != 0) {
            itemAdapter.clear()
            consultitle.clear()
            medicalDocuments?.sortedBy { it.uploadByUser }
            itemAdapter.add(TitleMedicalDocumentItem("Dokumen dari AlteaCare"))
            medicalDocuments?.map { document ->
                when (document.uploadByUser) {
                    0 -> {
                        itemAdapter.add(
                            ContentMedicalDocumentItem(
                                DummyFile(
                                    document.originalName.toString(),
                                    document.size.toString(),
                                    document.url.toString(),
                                    document.date.toString()
                                ), StatusUpload.SUCCESS
                            )
                        )
                    }
                    else -> {
                    }
                }
            }
            itemAdapter.add(TitleMedicalDocumentItem("Unggahan Saya"))
            medicalDocuments?.map { document ->
                when (document.uploadByUser) {
                    1 -> {
                        itemAdapter.add(
                            ContentMedicalDocumentItem(
                                DummyFile(
                                    document.originalName.toString(),
                                    document.size.toString(),
                                    document.url.toString(),
                                    document.date.toString()
                                ), StatusUpload.SUCCESS
                            )
                        )
                    }
                    else -> {
                    }
                }
            }
        } else {
            itemAdapter.clear()
            itemAdapter.add(EmptyMedicalDocumentItem())
        }
    }

    override fun bindViewModel(): ConsultationDetailSharedVM {
        return ViewModelProvider(
            requireActivity(),
            viewModelFactory
        )[ConsultationDetailSharedVM::class.java]
    }

    override fun getUiBinding(): FragmentMedicalDocumentBinding {
        return FragmentMedicalDocumentBinding.inflate(layoutInflater)
    }

    override fun onFirstLaunch(savedInstanceState: Bundle?, view: View) {
        viewBinding?.run {
            medDocumentRecycler.let {
                it.layoutManager = LinearLayoutManager(requireContext())
                it.adapter = fastAdapter
            }
        }

        consultationId = (activity as ConsultationDetailActivity).consultationId
        fetchConfiguration = FetchConfiguration.Builder(requireContext())
            .createDownloadFileOnEnqueue(false)
            .enableAutoStart(true)
            .enableFileExistChecks(true)
            .enableRetryOnNetworkGain(true)
            .setAutoRetryMaxAttempts(2)
            .build()
    }

    private fun handleLoadingLivedataEvent(showLoading: Boolean?) {
        viewBinding?.run { onMedicalDocumentSwipe.isRefreshing = showLoading == true }
    }

    override fun onReExecute() {

    }

    override fun initUiListener() {
        viewBinding?.run {
            onMedicalDocumentSwipe.setOnRefreshListener {
                viewModel?.getConsultationDetail(consultationId!!)
            }


            fastAdapter.addEventHook(object : ClickEventHook<ContentMedicalDocumentItem>() {

                override fun onBind(viewHolder: RecyclerView.ViewHolder): View? {
                    //return the views on which you want to bind this event
                    if (viewHolder is BindingViewHolder<*> && viewHolder.binding is ItemMedicalDocumentContentBinding) {
                        return (viewHolder.binding as ItemMedicalDocumentContentBinding).itemMedicalDocTxtShow

                    }
                    return null
                }

                override fun onClick(
                    v: View,
                    position: Int,
                    fastAdapter: FastAdapter<ContentMedicalDocumentItem>,
                    item: ContentMedicalDocumentItem
                ) {
                    when (v.id) {
                        R.id.itemMedicalDocTxtShow -> {
                            if (!pathToDownload(item.dummyFile.fileName) && item.dummyFile.filePath.contains(
                                    "pdf"
                                )
                            ) {
                                item.let {
                                    viewModel?.startDownload(
                                        it.dummyFile.filePath,
                                        getPathLocal(),
                                        it.dummyFile.fileName,
                                        position,
                                        fetchConfiguration!!,
                                        false,
                                        false
                                    )
                                }
                            } else {
                                if (item.dummyFile.filePath.contains("pdf")) {
                                    val uri = FileProvider.getUriForFile(
                                        requireContext(),
                                        Constant.FILE_PROVIDER,
                                        File(getPathLocal() + item.dummyFile.fileName)
                                    )
                                    (requireActivity() as AppCompatActivity).openDocument(uri)

                                } else {
                                    router.openViewDocument(
                                        requireActivity(),
                                        item.dummyFile.filePath
                                    )
                                }
                            }
                        }
                    }

                }
            })
        }
    }

    override fun handleFailure(failure: Failure?) {
        when (failure) {
            is NotFoundFailure.DataNotExist -> {
                if (fastAdapter.itemCount == 0) itemAdapter.add(EmptyMedicalDocumentItem())
            }
            else -> super.handleFailure(failure)
        }
    }


    private fun pathToDownload(nameFile: String): Boolean {
        val filePath = File(getPathLocal() + nameFile)
        return filePath.isFile
    }

    private fun getPathLocal(): String {
        return requireActivity().getTempFolder().absolutePath
    }

    private fun getProgressDownload() {
    }

    private fun getStatusDownload(download: Pair<Status, Int>?) {
        download?.let {
            val item = fastAdapter.getItem(it.second) as ContentMedicalDocumentItem
            Timber.e(download.first.name.plus(" ${download.second}"))
            when (it.first) {
                Status.DOWNLOADING, Status.QUEUED, Status.ADDED -> {
                    fastAdapter.notifyAdapterItemChanged(
                        it.second,
                        item.also { it.status = StatusUpload.LOADING })
                }
                Status.COMPLETED -> {
                    fastAdapter.notifyAdapterItemChanged(
                        it.second,
                        item.also { it.status = StatusUpload.SUCCESS })
                }
                else -> {
                }
            }

        }
    }

    override fun initMenu(): Int = 0

    companion object {
        fun newInstance(): MedicalDocumentFragment {
            return MedicalDocumentFragment().apply {
            }
        }
    }
}