package id.altea.care.view.consultationdetail

import android.os.CountDownTimer
import com.tonyodev.fetch2.*
import id.altea.care.core.base.BaseViewModel
import id.altea.care.core.domain.event.UploadProgressAdapter
import id.altea.care.core.domain.model.AddDocumentData
import id.altea.care.core.domain.model.ConsultationDetail
import id.altea.care.core.domain.model.Files
import id.altea.care.core.domain.usecase.*
import id.altea.care.core.exception.Failure
import id.altea.care.core.exception.NotFoundFailure
import id.altea.care.core.ext.TagInjection
import id.altea.care.core.ext.disposedBy
import id.altea.care.core.ext.getGeneralErrorServer
import id.altea.care.core.helper.*
import id.altea.care.view.consultationdetail.patientdata.failure.FileProcessFailure
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import okhttp3.MultipartBody
import retrofit2.HttpException
import java.io.File
import javax.inject.Inject
import javax.inject.Named

@Suppress("UnstableApiUsage")
class ConsultationDetailSharedVM @Inject constructor(
    @Named(TagInjection.UI_Scheduler) uiSchedulers: Scheduler,
    @Named(TagInjection.IO_Scheduler) ioScheduler: Scheduler,
    networkHandler: NetworkHandler,
    private val consultationDetailUseCase: GetConsultationDetailUseCase,
    private val dataUseCase: DataUseCase,
    private val addDocumentAppointmentUseCase: GetAddDocumentAppoinmentUseCase,
    private val removeDocumentUseCase: RemoveDocumentUseCase,
    private val getAuthUseCase: GetAuthUseCase
) : BaseViewModel(uiSchedulers, ioScheduler, networkHandler) {

    var consultationDetail: ConsultationDetail? = null

    private var uploadDisposable: Disposable? = null
    private val isShowLoadingSubject = BehaviorSubject.create<Boolean>()
    private val appointmentDetailSubject = BehaviorSubject.create<ConsultationDetail>()
    private var fetch: Fetch? = null

    val isShowLoading: Observable<Boolean>
        get() = isShowLoadingSubject

    val appointmentDetail: Observable<ConsultationDetail>
        get() = appointmentDetailSubject

    private val progressDownloadPatientSubject = PublishSubject.create<Pair<Int, Int>>()
    private val progressDownloadCostPatientSubject = PublishSubject.create<Pair<Int,Int>>()
    private val progressDownloadMedicalSubject = PublishSubject.create<Pair<Int, Int>>()
    private val statusDownloadPatientSubject = BehaviorSubject.create<Pair<Status, Int>>()
    private val statusDownloadCostPatientSubject = BehaviorSubject.create<Pair<Status,Int>>()
    private val statusDownloadMedicalSubject = BehaviorSubject.create<Pair<Status, Int>>()

    val progressDownloadCostPatient : Observable<Pair<Int,Int>>
        get() = progressDownloadCostPatientSubject.observeOn(uiSchedulers)

    val progressDownloadPatient: Observable<Pair<Int, Int>>
        get() = progressDownloadPatientSubject.observeOn(uiSchedulers)

    val progressDownloadMedicalDoc: Observable<Pair<Int, Int>>
        get() = progressDownloadMedicalSubject.observeOn(uiSchedulers)

    val statusDownloadPatientCost : Observable<Pair<Status,Int>>
        get() = statusDownloadCostPatientSubject.observeOn(uiSchedulers)

    val statusDownloadPatient: Observable<Pair<Status, Int>>
        get() = statusDownloadPatientSubject.observeOn(uiSchedulers)

    val statusDownloadMedicalDoc: Observable<Pair<Status, Int>>
        get() = statusDownloadMedicalSubject.observeOn(uiSchedulers)


    val patientFailureEvent = SingleLiveEvent<Failure>()
    val filesLiveData = SingleLiveEvent<Pair<Files, Int>>()
    val resultEvent = SingleLiveEvent<Int>()
    val addDocumentLiveData = SingleLiveEvent<Pair<AddDocumentData?, Int>>()
    val errorEvent = SingleLiveEvent<Error>()

    fun getConsultationDetail(consultationId: Int) {
        executeJob {
            if (uploadDisposable != null) uploadDisposable?.dispose()
            fetch?.cancelAll()
            consultationDetailUseCase.doRequestConsultationDetaiL(consultationId)
                .compose(applySchedulers())
                .doOnSubscribe { isShowLoadingSubject.onNext(true) }
                .doOnTerminate { isShowLoadingSubject.onNext(false) }
                .subscribe({ result ->
                    consultationDetail = result
                    appointmentDetailSubject.onNext(result)
                    uploadDisposable = null
                }, {
                    if (it is HttpException && it.code() == 404) {
                        handleFailure(NotFoundFailure.DataNotExist())
                    } else {
                        handleFailure(it.getGeneralErrorServer())
                    }
                }).disposedBy(disposable)
        }
    }

    fun uploadDocumentAppointment(file: File, adapterPosition: Int) {
        executeJob {
            val body: MultipartBody.Part =
                MultipartBody.Part.createFormData(
                    "file",
                    if (file.name.startsWith("altea-")) file.name else "altea-".plus(file.name),
                    ProgressEmittingRequestBody(
                        getMimeType(file),
                        file,
                        adapterPosition
                    )
                )
            uploadDisposable = dataUseCase.uploadFiles(body)
                .compose(applySchedulers())
                .doAfterTerminate { uploadDisposable = null }
                .subscribe({ result ->
                    result.data.let { filesLiveData.value = it to adapterPosition }
                }, {
                    RxBus.post(UploadProgressAdapter.Error(adapterPosition))
                })
        }
    }

    fun addDocumentAppointment(appointmentId: Int?, documentId: String?, adapterPosition: Int) {
        executeJob {
            addDocumentAppointmentUseCase
                .addDocumentAppointment(appointmentId, documentId)
                .compose(applySchedulers())
                .subscribe({
                    addDocumentLiveData.value = it to adapterPosition
                }, {
                    addDocumentLiveData.value = null to adapterPosition
                }).disposedBy(disposable)
        }
    }

    fun removeDocumentAppointment(appointmentId: Int?, documentId: Int?, adapterPosition: Int) {
        if (uploadDisposable != null) {
            handlePatientFailure(FileProcessFailure.WaitingUploadFailure)
            return
        }
        executeJob {
            removeDocumentUseCase
                .removeDocument(appointmentId, documentId)
                .compose(applySchedulers())
                .doOnSubscribe { isLoadingLiveData.value = true }
                .doOnTerminate { isLoadingLiveData.value = false }
                .subscribe({
                    resultEvent.value = adapterPosition
                }, {
                    handlePatientFailure(it.getGeneralErrorServer())
                }).disposedBy(disposable)
        }

    }

    fun startDownload(
        url: String,
        pathFolder: String,
        nameFile: String,
        adapterPosition: Int,
        fetchConfiguration: FetchConfiguration,
        isPatientFileDownload: Boolean, // just for flag if download from patient fragment,
        isConstPatientFileDownload : Boolean
    ) {
        if (fetch == null) {
            fetch = Fetch.getInstance(fetchConfiguration)
        }
        val file = File(pathFolder)
        if (!file.exists()) {
            file.mkdirs()
        }

        val downloadPath = pathFolder + nameFile
        var request = Request(url, downloadPath)
        request.priority = Priority.HIGH
        request.networkType = NetworkType.ALL

        fetch?.enqueue(request, { result -> request = result }) {}
        if (isPatientFileDownload) {
            fetch?.addListener(
                fetchListener(
                    request,
                    adapterPosition,
                    statusDownloadPatientSubject,
                    progressDownloadPatientSubject
                )
            )
        }else if(isConstPatientFileDownload){
            fetch?.addListener(
                fetchListener(
                    request,
                    adapterPosition,
                    statusDownloadCostPatientSubject,
                    progressDownloadCostPatientSubject
                )
            )
        } else {
            fetch?.addListener(
                fetchListener(
                    request,
                    adapterPosition,
                    statusDownloadMedicalSubject,
                    progressDownloadMedicalSubject
                )
            )
        }
    }

    private fun handlePatientFailure(failure: Failure) {
        patientFailureEvent.value = failure
    }

    private fun fetchListener(
        request: Request,
        position: Int,
        liveEvent: BehaviorSubject<Pair<Status, Int>>,
        progressEvent: PublishSubject<Pair<Int, Int>>
    ): FetchDownloadListener {
        return (object : FetchDownloadListener {
            override fun onAdded(download: Download) {
                super.onAdded(download)
                if (request.id == download.request.id)
                    liveEvent.onNext(download.status to position)
            }

            override fun onCompleted(download: Download) {
                super.onCompleted(download)
                if (request.id == download.request.id)
                    liveEvent.onNext(download.status to position)
            }

            override fun onError(
                download: Download,
                error: Error,
                throwable: Throwable?
            ) {
                super.onError(download, error, throwable)
                if (request.id == download.request.id)
                    liveEvent.onNext(download.status to position)
                errorEvent.postValue(error)
            }

            override fun onProgress(
                download: Download,
                etaInMilliSeconds: Long,
                downloadedBytesPerSecond: Long
            ) {
                super.onProgress(download, etaInMilliSeconds, downloadedBytesPerSecond)
                if (request.id == download.request.id) {
                     liveEvent.onNext(download.status to position)
                    progressEvent.onNext(download.progress to position)
                }
            }

            override fun onQueued(download: Download, waitingOnNetwork: Boolean) {
                super.onQueued(download, waitingOnNetwork)
                if (request.id == download.request.id)
                    liveEvent.onNext(download.status to position)
            }
        })
    }

    private fun getMimeType(file: File): String {
        return when (val ext = file.extension) {
            "pdf" -> "application/pdf"
            else -> "image/$ext"
        }
    }

    fun setupTimer(
        millisInFuture : Long,
        countdownInterval: Long = 1000, // 1 detik
        onTick: (resultTimer: String) -> Unit,
        onFinishTimer: (() -> Unit)? = null
    ) {
        val countDownTimer =  object : CountDownTimer(millisInFuture, countdownInterval) {
            override fun onTick(millisUntilFinished: Long) {
                var different = millisUntilFinished

                val secondsInMilli = 1000
                val minutesInMilli = secondsInMilli * 60
                val hoursInMilli = minutesInMilli * 60

                val elapsedHours = different / hoursInMilli
                different %= hoursInMilli

                val elapsedMinutes = different / minutesInMilli
                different %= minutesInMilli

                val elapsedSeconds = different / secondsInMilli

                onTick.invoke("${elapsedHours.timerDoubleValue()} : ${elapsedMinutes.timerDoubleValue()} : ${elapsedSeconds.timerDoubleValue()}")
            }

            override fun onFinish() {
                onFinishTimer?.invoke()
            }
        }
        countDownTimer.start()
    }

    private fun Long.timerDoubleValue() : String {
        val timer = this.toString()
        return if (timer.length <= 1) "0$timer" else timer
    }
}