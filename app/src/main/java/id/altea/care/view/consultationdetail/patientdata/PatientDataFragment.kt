package id.altea.care.view.consultationdetail.patientdata

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.Intent.ACTION_PICK
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.hbisoft.pickit.PickiT
import com.hbisoft.pickit.PickiTCallbacks
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.adapters.ItemAdapter
import com.mikepenz.fastadapter.binding.BindingViewHolder
import com.mikepenz.fastadapter.listeners.ClickEventHook
import com.tonyodev.fetch2.Error
import com.tonyodev.fetch2.FetchConfiguration
import com.tonyodev.fetch2.Status
import id.altea.care.R
import id.altea.care.core.base.BaseActivity
import id.altea.care.core.base.BaseFragmentVM
import id.altea.care.core.domain.event.AppointmentStatusUpdateEvent
import id.altea.care.core.domain.event.UploadProgressAdapter
import id.altea.care.core.domain.model.*
import id.altea.care.core.exception.Failure
import id.altea.care.core.ext.*
import id.altea.care.core.helper.RxBus
import id.altea.care.core.helper.util.Constant
import id.altea.care.core.helper.validator.TimeConversionValidator.Companion.countDownCalculate
import id.altea.care.databinding.FragmentPatientDataBinding
import id.altea.care.databinding.ItemPatientUploadFileBinding
import id.altea.care.view.common.bottomsheet.TypeFileSource
import id.altea.care.view.common.enums.TypeAppointment
import id.altea.care.view.consultationdetail.ConsultationDetailSharedVM
import id.altea.care.view.consultationdetail.patientdata.failure.FileProcessFailure
import id.altea.care.view.consultationdetail.patientdata.item.UploadDocumentItem
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.content_patient_file_upload.*
import timber.log.Timber
import java.io.File
import java.util.*

class PatientDataFragment :
    BaseFragmentVM<FragmentPatientDataBinding, ConsultationDetailSharedVM>(),
    PickiTCallbacks {

    private val router by lazy { PatientDataRouter() }
    private var imagePath: File? = null
    private val pickIt by lazy { PickiT(requireContext(), this, requireActivity()) }
    private val consultationId by lazy { arguments?.getInt(EXTRA_CONSULTATION_ID) }
    private val itemAdapter = ItemAdapter<UploadDocumentItem>()
    private val fastAdapter = FastAdapter.with(itemAdapter)
    private var fetchConfiguration: FetchConfiguration? = null
    private var typeAppointment: TypeAppointment? = null
    private var isLoading = false
    private var doctor: ConsultationDoctor? = null

    override fun onResume() {
        super.onResume()
        handleLoadingLivedata(isLoading)
    }

    override fun onPause() {
        super.onPause()
        handleLoadingLivedata(false)
    }

    override fun bindViewModel(): ConsultationDetailSharedVM {
        return ViewModelProvider(requireActivity()).get(ConsultationDetailSharedVM::class.java)
    }

    override fun getUiBinding(): FragmentPatientDataBinding {
        return FragmentPatientDataBinding.inflate(layoutInflater)
    }

    override fun onFirstLaunch(savedInstanceState: Bundle?, view: View) {
        typeAppointment = arguments?.getSerializable(EXTRA_TYPE_SCHEDULE) as TypeAppointment
        listenRxBus()
        viewBinding?.layoutUpload?.run {
            contentUploadRecycler.run {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = fastAdapter
            }
        }

        updateView(typeAppointment)
        fetchConfiguration = FetchConfiguration.Builder(requireContext())
            .createDownloadFileOnEnqueue(false)
            .enableAutoStart(true)
            .enableFileExistChecks(true)
            .enableRetryOnNetworkGain(true)
            .setAutoRetryMaxAttempts(2)
            .build()
    }

    @SuppressLint("SetTextI18n")
    private fun updateView(typeAppointment: TypeAppointment?) {
        viewBinding?.run {
            when (typeAppointment) {
                TypeAppointment.MEET_SPECIALIST -> {
                    layoutUpload.root.isVisible = true
                    patientDataBtnCreateSchedule.run {
                        text = getString(R.string.meet_doctor)
                        isEnabled = true
                        setBackgroundColor(
                            ContextCompat.getColor(requireContext(), R.color.blueDark)
                        )
                    }
                }
                TypeAppointment.PAID -> {
                    layoutUpload.root.isVisible = true
                    patientDataBtnCreateSchedule.run {
                        setBackgroundColor(
                            ContextCompat.getColor(requireContext(), R.color.blueDark)
                        )
                        text = "Menunggu Telekonsultasi"
                        disposable.delay(1000) {
                            viewModel?.consultationDetail?.let { consultationDetail ->
                                val dateFormat =
                                    "${consultationDetail.schedule?.date} ${consultationDetail.schedule?.startTime}"
                                viewModel?.setupTimer(
                                    millisInFuture = dateFormat.countDownCalculate(),
                                    onTick = { millisUntilFinished ->
                                        text = "Mulai Telekonsultasi $millisUntilFinished"
                                        isEnabled = true
                                    }, onFinishTimer = {
                                        viewModel?.getConsultationDetail(consultationId ?: 0)
                                    })
                            }
                        }
                    }
                }
                TypeAppointment.COMPLETED,
                TypeAppointment.WAITING_FOR_MEDICAL_RESUME,
                TypeAppointment.WATCH_MEMO_ALTEA -> {
                    layoutUpload.root.isVisible = false
                    patientDataBtnCreateSchedule.run {
                        text = getString(R.string.re_create_consultation)
                        isEnabled = true
                    }
                }
                else -> {
                }
            }
        }
    }

    override fun onReExecute() {
    }

    override fun initUiListener() {
        viewBinding?.run {
            layoutUpload.contentUploadTxtUpload.onSingleClick()
                .subscribe { openFileChooser() }
                .disposedBy(disposable)

            patientDataBtnCreateSchedule.onSingleClick().subscribe {
                typeAppointment?.let { type ->
                    when (type) {
                        TypeAppointment.COMPLETED -> {
                            viewModel?.consultationDetail?.let {
                                router.openDoctorDetail(
                                    requireActivity(),
                                    ConsultationDoctor.toDoctorModel(it.doctor),
                                    consultationId ?: 0,
                                    it.consultationMethod
                                )
                            }
                            return@subscribe
                        }

                        TypeAppointment.PAID -> {
                            viewModel?.consultationDetail?.let { consultationDetail ->
                                val dateFormat =
                                    "${consultationDetail.schedule?.date} ${consultationDetail.schedule?.startTime}"
                                val dateConsultation =
                                    consultationDetail.schedule?.date?.toNewFormat(
                                        oldFormat = "yyyy-MM-dd",
                                        newFormat = "EEEE, dd MMM yyyy",
                                        isLocale = true
                                    )
                                val timeMinuteConsultation = toStartTime(
                                    consultationDetail.schedule?.startTime,
                                    consultationDetail.schedule?.endTime
                                )
                                router.openBottomSheetCountDownSpecialist(
                                    caller = activity?.supportFragmentManager,
                                    dateConsultation = dateConsultation ?: "",
                                    timeMinuteConsultation = timeMinuteConsultation,
                                    countDownTimer = dateFormat.countDownCalculate(),
                                    onFinish = {
                                        viewModel?.getConsultationDetail(consultationId ?: 0)
                                    }
                                )
                            }
                        }

                        TypeAppointment.MEET_SPECIALIST -> {
                            router.openConfirmCallBottomSheet(activity?.supportFragmentManager, {
                                router.openTransitionSpecialist(
                                    requireActivity(),
                                    consultationId!!,
                                    doctor,
                                    InfoDetail(
                                        doctor?.name,
                                        doctor?.specialist?.name,
                                        viewBinding?.patientDataTxtDate?.text.toString()
                                    )
                                )
                            }, {}, doctor)

                        }

                        else -> {
                        }
                    }
                }
            }.disposedBy(disposable)

            onPatientDataSwipe.setOnRefreshListener {
                viewModel?.getConsultationDetail(consultationId ?: 0)
                disposable.delay(1000){
                    updateView(typeAppointment)
                }
            }

            fastAdapter.addEventHook(object : ClickEventHook<UploadDocumentItem>() {

                override fun onBindMany(viewHolder: RecyclerView.ViewHolder): List<View>? {
                    //return the views on which you want to bind this event
                    if (viewHolder is BindingViewHolder<*> && viewHolder.binding is ItemPatientUploadFileBinding) {
                        return listOf(
                            (viewHolder.binding as ItemPatientUploadFileBinding).patientFileTxtDelete,
                            (viewHolder.binding as ItemPatientUploadFileBinding).patientFileTxtShow,
                            (viewHolder.binding as ItemPatientUploadFileBinding).itemPatientErrorStatus
                        )
                    }
                    return null
                }

                override fun onClick(
                    v: View,
                    position: Int,
                    fastAdapter: FastAdapter<UploadDocumentItem>,
                    item: UploadDocumentItem
                ) {
                    when (v.id) {
                        R.id.patientFileTxtDelete -> {
                            viewModel?.removeDocumentAppointment(consultationId, item.id, position)
                        }
                        R.id.patientFileTxtShow -> {
                            if (item.mimeType.equals("pdf")) {
                                if (isFileExistInLocal(item.name ?: "")) {
                                    item.name?.let { openPdfDocument(it) }
                                } else {
                                    viewModel?.startDownload(
                                        item.urlDownload.orEmpty(),
                                        getPathLocal(),
                                        item.name.orEmpty(),
                                        position,
                                        fetchConfiguration!!,
                                        true,
                                        false
                                    )
                                }
                            } else {
                                router.openViewDocument(
                                    requireActivity(), item.urlDownload.orEmpty()
                                )
                            }
                        }
                        R.id.itemPatientErrorStatus -> {
                            item.filePath?.let {
                                viewModel?.uploadDocumentAppointment(File(it), position)
                            }
                        }
                    }
                }
            })
        }
    }

    override fun observeViewModel(viewModel: ConsultationDetailSharedVM) {
        observe(viewModel.failureLiveData, ::handleFailureUpload)
        observe(viewModel.patientFailureEvent, ::handleFailureUpload)
        observe(viewModel.filesLiveData, ::handleResultFile)
        observe(viewModel.addDocumentLiveData, ::handleDocumentEvent)
        observe(viewModel.resultEvent, ::handleDeleteFile)
        observe(viewModel.errorEvent, ::getErrorEventDownload)

        viewModel.statusDownloadPatient
            .subscribe { getStatusDownload(it) }
            .disposedBy(disposable)

        viewModel.progressDownloadPatient
            .subscribe { getProgressDownload(it) }
            .disposedBy(disposable)

        viewModel.isShowLoading
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                isLoading = it
                handleLoadingLivedata(isLoading)
            }
            .disposedBy(disposable)

        viewModel.appointmentDetail
            .subscribe {
                handleConsultationData(it)
            }
            .disposedBy(disposable)
    }

    private fun openFileChooser() {
        router.openFileChooser(requireActivity()) {
            when (it) {
                TypeFileSource.CAMERA -> openCamera()
                TypeFileSource.GALLERY -> openGallery()
                TypeFileSource.STORAGE -> openStorage()
                else -> {
                }
            }
        }
    }

    private fun listenRxBus() {
        RxBus.getEvents()
            .onErrorReturn { Timber.e(it) }
            .filter { isAdded }
            .subscribe {
                when (it) {
                    is AppointmentStatusUpdateEvent -> {
                        if (it.appointmentType == TypeAppointment.COMPLETED ||
                            it.appointmentType == TypeAppointment.PAID ||
                            it.appointmentType == TypeAppointment.MEET_SPECIALIST
                        ) {
                            typeAppointment = it.appointmentType
                            requireActivity().runOnUiThread {
                                if (isAdded) updateView(typeAppointment)
                            }
                        }
                    }
                    is UploadProgressAdapter -> {
                        requireActivity().runOnUiThread {
                            when (it) {
                                is UploadProgressAdapter.Loading -> {
                                    if (it.adapterPosition >= itemAdapter.adapterItemCount) return@runOnUiThread
                                    val item = itemAdapter.getAdapterItem(it.adapterPosition)
                                    itemAdapter[it.adapterPosition] =
                                        item.apply { setLoadingState(it.progress) }
                                }
                                is UploadProgressAdapter.Error -> {
                                    if (it.adapterPosition >= itemAdapter.adapterItemCount) return@runOnUiThread
                                    val item = itemAdapter.getAdapterItem(it.adapterPosition)
                                    itemAdapter[it.adapterPosition] = item.apply { setErrorState() }
                                }
                            }
                            Timber.e(Gson().toJson(it))
                        }
                    }
                }
            }
            .disposedBy(disposable)
    }

    private fun openStorage() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            type = "*/*"
            action = Intent.ACTION_GET_CONTENT
            putExtra("return-data", true)
            putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("application/msword", "application/pdf"))
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        resultStorage.launch(intent)
    }

    private fun openCamera() {
        imagePath = requireActivity().createDefaultImagePath()
        val providerFile =
            FileProvider.getUriForFile(requireContext(), Constant.FILE_PROVIDER, imagePath!!)
        resultCamera.launch(providerFile)
    }

    private fun openGallery() {
        resultGallery.launch(Intent(ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI))
    }

    private val resultStorage =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK)
                it.data?.data?.let { uri -> pickIt.getPath(uri, Build.VERSION.SDK_INT) }
        }

    private val resultCamera =
        registerForActivityResult(ActivityResultContracts.TakePicture()) {
            if (it) imagePath?.let { file ->
                addAdapterImage(file.compressImage(tempFolder = requireActivity().getTempFolder()))
            }
        }

    private fun addAdapterImage(file: File) {
        if (isFileSizeMaximum(file.length() / 1024)) {
            (requireActivity() as BaseActivity<*>).showErrorSnackbar(getString(R.string.str_file_size))
            return
        }
        itemAdapter.add(
            UploadDocumentItem(
                0,
                if (file.extension.contains("pdf")) "pdf" else "image",
                if (file.name.startsWith("altea-")) file.name else "altea-".plus(file.name),
                file.absolutePath,
                "",
                file.length().convertSizeFile(),
                UploadDocumentItem.StatusUpload.LOADING
            )
        )
        viewModel?.uploadDocumentAppointment(file, itemAdapter.adapterItemCount - 1)
    }

    private fun handleResultFile(files: Pair<Files?, Int>?) {
        if (files?.first?.id != null) {
            viewModel?.addDocumentAppointment(consultationId, files.first!!.id, files.second)
        }
    }

    private val resultGallery =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                try {
                    it?.data?.data?.let { uri ->
                        requireActivity()
                            .getRealPathURI(uri)
                            ?.compressImage(tempFolder = requireActivity().getTempFolder())
                            ?.also { file -> addAdapterImage(file) }
                    }
                } catch (e: Exception) {
                    Timber.e(e)
                    showToast("Gagal mendapatkan berkas, mohon coba kembali")
                }
            }
        }

    override fun PickiTonUriReturned() {}

    override fun PickiTonStartListener() {}

    override fun PickiTonProgressUpdate(progress: Int) {}

    override fun PickiTonCompleteListener(
        path: String?,
        wasDriveFile: Boolean,
        wasUnknownProvider: Boolean,
        wasSuccessful: Boolean,
        Reason: String?
    ) {
        if (wasSuccessful && path != null) {
            addAdapterImage(File(path))
        }
    }

    override fun initMenu(): Int = 0

    @SuppressLint("SetTextI18n")
    private fun handleConsultationData(consultationDetail: ConsultationDetail?) {
        viewBinding?.run {
            patientDataTxtOrderId.text = "Order ID: ${consultationDetail?.orderCode}"
            patientDataTxtDate.text = consultationDetail?.schedule?.date?.toNewFormat(
                oldFormat = "yyyy-MM-dd", newFormat = "EEEE, dd MMM yyyy", true
            )
            patientDataTxtTime.text = toStartTime(
                consultationDetail?.schedule?.startTime,
                consultationDetail?.schedule?.endTime
            )

            val consultationUser = consultationDetail?.user
            val consultationPatient = consultationDetail?.patient
            patientData.run {
                patientTxtName.text =
                    "${consultationUser?.firstName} ${consultationUser?.lastName}"
                patientTxtAge.text =
                    String.format(
                        getString(R.string.s_age),
                        consultationUser?.age?.year ?: "0",
                        consultationUser?.age?.month ?: "0"
                    )
                patientTxtAddress.text = consultationPatient
                    ?.addressRaw
                    ?.firstOrNull()
                    ?.completeAddress()
                patientTxtBirthDate.text = consultationUser?.birthdate
                patientTxtEmail.text = consultationUser?.email
                patientTxtGender.text = consultationUser?.gender
                patientTxtPhone.text = consultationUser?.phoneNumber
                patientTxtIdentity.text = consultationUser?.cardId
            }

            contentDoctor.run {
                docProfileTxtDoctorName.text = consultationDetail?.doctor?.name
                docProfileTxtDoctorTitle.text = consultationDetail?.doctor?.specialist?.name
                docProfileTxtRsName.text = consultationDetail?.doctor?.hospital?.name
                docProfileImgRsIcon.loadImage(consultationDetail?.doctor?.hospital?.icon.orEmpty())
                docProfileImgDoctor.loadImage(consultationDetail?.doctor?.photo.orEmpty())
                docProfileArrow.isVisible = false
            }

            consultationDetail?.medicalDocument?.let {
                if (it.isNotEmpty()) {
                    itemAdapter.clear()
                    it.forEach { medicalDocument ->
                        if (medicalDocument.uploadByUser == 1) {
                            itemAdapter.add(
                                UploadDocumentItem(
                                    medicalDocument.id?.toInt(),
                                    if (medicalDocument.url?.contains("pdf") == true) "pdf" else "image",
                                    medicalDocument.originalName,
                                    "",
                                    medicalDocument.url,
                                    medicalDocument.size,
                                    UploadDocumentItem.StatusUpload.SUCCESS
                                )
                            )
                        }
                    }
                }
            }

            doctor = ConsultationDoctor(
                consultationDetail?.doctor?.id,
                consultationDetail?.doctor?.name,
                consultationDetail?.doctor?.photo,
                consultationDetail?.doctor?.specialist,
                null
            )
        }
    }

    private fun toStartTime(startTime: String?, endTime: String?): String {
        val timeStart = if (startTime != null && startTime.length >= 4)
            startTime.substring(0, 5) else startTime

        val timeEnd = if (endTime != null && endTime.length >= 4)
            endTime.substring(0, 5) else endTime

        return "$timeStart - $timeEnd"
    }

    private fun handleLoadingLivedata(showLoading: Boolean?) {
        viewBinding?.run {
            onPatientDataSwipe.isRefreshing = showLoading == true
        }
    }

    private fun handleDocumentEvent(addDocumentData: Pair<AddDocumentData?, Int>?) {
        val position = addDocumentData?.second
        position?.let {
            val item = itemAdapter.getAdapterItem(it)
            if (addDocumentData.first != null) {
                fastAdapter.notifyAdapterItemChanged(it, item.apply {
                    status = UploadDocumentItem.StatusUpload.SUCCESS
                    id = addDocumentData.first?.id
                    urlDownload = addDocumentData.first?.url
                })
            } else {
                fastAdapter.notifyAdapterItemChanged(it, item.apply { setErrorState() })
            }
        }
    }

    private fun handleDeleteFile(adapterPosition: Int?) {
        adapterPosition?.let { itemAdapter.remove(adapterPosition) }
    }

    private fun openPdfDocument(fileName: String) {
        val uri = FileProvider.getUriForFile(
            requireContext(),
            Constant.FILE_PROVIDER,
            File(getPathLocal() + fileName)
        )
        (requireActivity() as AppCompatActivity).openDocument(uri)
    }

    private fun getProgressDownload(downloadProgress: Pair<Int, Int>?) {
        downloadProgress?.let {
            val item = itemAdapter.getAdapterItem(it.second)
            itemAdapter.set(it.second, item.apply { setLoadingState(it.first) })
        }
    }

    private fun getStatusDownload(downloading: Pair<Status, Int>?) {
        downloading?.let {
            val item = itemAdapter.getAdapterItem(it.second)
            when (it.first) {
                Status.DOWNLOADING, Status.ADDED, Status.QUEUED -> {
                    fastAdapter.notifyAdapterItemChanged(
                        it.second,
                        item.apply { setLoadingState(0) })
                }
                Status.COMPLETED -> {
                    fastAdapter.notifyAdapterItemChanged(
                        it.second,
                        item.apply { setSuccessState() })
                    item.name?.let {name ->  openPdfDocument(name) }
                }
                Status.FAILED -> {
                    fastAdapter.notifyAdapterItemChanged(
                        it.second,
                        item.apply { setSuccessState() }) // if error back to state normal
                }
                else -> {
                }
            }

        }
    }

    private fun getErrorEventDownload(error: Error?) {
        (activity as BaseActivity<*>).showErrorSnackbar(error?.name!!)
    }

    private fun isFileSizeMaximum(sizeFile: Long): Boolean {
        return sizeFile >= 10000
    }

    private fun isFileExistInLocal(nameFile: String): Boolean {
        return File(getPathLocal() + nameFile).exists()
    }

    private fun getPathLocal(): String {
        return requireActivity().getTempFolder().absolutePath
    }

    private fun handleFailureUpload(failure: Failure?) {
        when (failure) {
            is FileProcessFailure.WaitingUploadFailure -> {
                showToast(getString(R.string.error_waiting_upload))
            }
            else -> super.handleFailure(failure)
        }
    }

    companion object {
        private const val EXTRA_TYPE_SCHEDULE = "PatientData.Type"
        private const val EXTRA_CONSULTATION_ID = "PatiendData.ConsultationID"
        fun newInstance(
            typeAppointment: TypeAppointment,
            consultationId: Int
        ): PatientDataFragment {
            return PatientDataFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(EXTRA_TYPE_SCHEDULE, typeAppointment)
                    putSerializable(EXTRA_CONSULTATION_ID, consultationId)
                }
            }
        }
    }
}