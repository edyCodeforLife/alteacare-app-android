package id.altea.care.view.videocall

import android.app.Application
import android.app.KeyguardManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.net.TrafficStats
import android.os.Looper
import com.tonyodev.fetch2.*
import com.tonyodev.fetch2core.DownloadBlock
import com.tonyodev.fetch2core.Func
import com.twilio.audioswitch.AudioSwitch
import com.twilio.chat.*
import com.twilio.video.*
import id.altea.care.core.base.BaseViewModel
import id.altea.care.core.domain.model.*
import id.altea.care.core.exception.Failure
import id.altea.care.core.ext.TagInjection
import id.altea.care.core.ext.disposedBy
import id.altea.care.core.helper.AlteaTwillioListener
import id.altea.care.core.helper.NetworkHandler
import id.altea.care.core.helper.SingleLiveEvent
import id.altea.care.core.helper.util.CameraCapturerCompat
import id.altea.care.core.helper.util.ConstantExtra.LOCAL_AUDIO_TRACK_NAME
import id.altea.care.core.helper.util.ConstantExtra.LOCAL_VIDEO_TRACK_NAME
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.subjects.BehaviorSubject
import timber.log.Timber
import java.io.File
import javax.inject.Inject
import javax.inject.Named
import id.altea.care.core.analytics.AnalyticManager
import id.altea.care.core.analytics.payload.EndCallMaPayloadBuilder
import android.util.DisplayMetrics
import android.util.Log
import android.widget.LinearLayout
import com.google.gson.Gson
import io.sentry.Sentry
import com.twilio.video.NetworkQualityVerbosity

import com.twilio.video.NetworkQualityConfiguration
import id.altea.care.BuildConfig
import id.altea.care.R
import id.altea.care.core.domain.usecase.*
import id.altea.care.core.helper.util.Constant
import id.altea.care.view.common.enums.ProviderVideo
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.socket.client.IO
import io.socket.client.Socket
import kotlinx.android.synthetic.main.content_disconnect.view.*
import kotlinx.android.synthetic.main.fragment_video_call.*
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.OkHttpClient
import okhttp3.Response
import java.io.IOException
import java.net.URI
import java.util.concurrent.TimeUnit
import java.util.logging.Handler


typealias IsChatError = Boolean
typealias IsChatConnected = Boolean

class VideoCallVM @Inject constructor(
    @Named(TagInjection.UI_Scheduler) uiSchedulers: Scheduler,
    @Named(TagInjection.IO_Scheduler) ioScheduler: Scheduler,
    networkHandler: NetworkHandler,
    private val getAuthUseCase: GetAuthUseCase,
    private val appointmentUseCase: AppointmentUseCase,
    private val createShareUseCase: CreateShareUseCase,
    private val getShareScreenUseCase: GetShareScreenUseCase,
    private val analyticManager: AnalyticManager,
    private val getAppointmentRoomProviderUseCase: GetAppointmentRoomProviderUseCase
) : BaseViewModel(uiSchedulers, ioScheduler, networkHandler) {
    private var mKeyguardManager: KeyguardManager? = null
    private var mLastRxBytes: Long = 0
    private var mLastTxBytes: Long = 0
    private var mLastTime: Long = 0
    private var mSpeed: Speed? = null
    private var socket: Socket? = null

    private val videoCallProviderTwillioSubject = BehaviorSubject.create<Twilio>()
    private val videoCallProviderJitsiSubject = BehaviorSubject.create<Jitsi>()
    private val chatProviderTwillioSubject = BehaviorSubject.create<Twilio>()

    val videoCallProviderTwillio : Observable<Twilio>
        get() = videoCallProviderTwillioSubject

    val videoCallProviderJitsi : Observable<Jitsi>
        get() = videoCallProviderJitsiSubject

    val chatProviderTwillio : Observable<Twilio>
        get() = chatProviderTwillioSubject

    var appointmentChatRoomTwillio: Twilio? = null
    val appointmentRoomEvent = SingleLiveEvent<Twilio>()
    val appointmentRoomProviderEvent = SingleLiveEvent<AppointmentProvider>()
    val localVideoTrackEvent = SingleLiveEvent<LocalVideoTrack>()
    val localAudioTrackEvent = SingleLiveEvent<LocalAudioTrack>()
    val remoteVideoTrack = SingleLiveEvent<RemoteVideoTrack>()
    val remoteAudioTrack = SingleLiveEvent<RemoteAudioTrack>()
    val roomTrackEvent = SingleLiveEvent<Pair<Room,String>>()
    val statusEventTrack = SingleLiveEvent<String>()
    val remoteParticipant = SingleLiveEvent<RemoteParticipant>()
    val cameraCapturerTrackEvent = SingleLiveEvent<CameraCapturerCompat>()
    val endCallConfirmationEvent = SingleLiveEvent<Empty>()
    val enableEvent = SingleLiveEvent<Enable>()
    val statusDownloadLiveData = SingleLiveEvent<Status>()
    val errorEvent = SingleLiveEvent<Error>()
    val progressDownloadLiveData = SingleLiveEvent<Int>()
    val stateVideo = SingleLiveEvent<Boolean>()
    val speedTrackEvent  = SingleLiveEvent<TrackingSpeed>()
    var  isMeetStarted : Boolean? =false

    private var cameraCapture: CameraCapturerCompat? = null
    private var localAudioTrack: LocalAudioTrack? = null
    private var localVideoTrack: LocalVideoTrack? = null
    private val audioSwitch: AudioSwitch? = null
    private var connectOptionsBuilder: ConnectOptions.Builder? = null
    private var encodingParameters: EncodingParameters? = null

    val senderMessageCallbackEvent = SingleLiveEvent<Pair<Boolean, Int>>()
    val senderMessageProgressEvent = SingleLiveEvent<Triple<Boolean, Long, Int>>()
    val message = SingleLiveEvent<Pair<Message,Boolean>>()
    val reInitEvent = SingleLiveEvent<Empty>()
    var reInitStatus : Boolean = false

    private val chatConnectionStatusSubject =
        BehaviorSubject.create<Pair<IsChatError, IsChatConnected>>()
    val chatConnectionStatus: Observable<Pair<IsChatError, IsChatConnected>>
        get() = chatConnectionStatusSubject

    var chatClient: ChatClient? = null
    private lateinit var mChannel: Channel
    var appoimentId: Int = -1
    var orderCode: String? = null
    var userProfile: UserProfile? = null
    var infoDetail: InfoDetail? = null
    var typeConsultation: String? = null



    fun onReinit(appoimentId: Int){
        doGetAppointmentRoom(appoimentId)
        reInitEvent.value = Empty()
        reInitStatus = true
    }


    fun doCalculate(context : Context){
        mLastRxBytes = TrafficStats.getTotalRxBytes()
        mLastTxBytes = TrafficStats.getTotalTxBytes()
        mLastTime = System.currentTimeMillis()
        mSpeed = Speed(context)
        mKeyguardManager = context.getSystemService(Service.KEYGUARD_SERVICE) as KeyguardManager
        Observable.interval(2000, TimeUnit.MILLISECONDS)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext {
                doCounting()
            }.subscribe().disposedBy(disposable)
    }

    private fun doCounting() {
        doPingConnection()
        val currentRxBytes = TrafficStats.getTotalRxBytes()
        val currentTxBytes = TrafficStats.getTotalTxBytes()
        val usedRxBytes = currentRxBytes - mLastRxBytes
        val usedTxBytes = currentTxBytes - mLastTxBytes
        val currentTime = System.currentTimeMillis()
        val usedTime = currentTime - mLastTime
        mLastRxBytes = currentRxBytes
        mLastTxBytes = currentTxBytes
        mLastTime = currentTime
        mSpeed?.calcSpeed(usedTime, usedRxBytes, usedTxBytes)
        mSpeed?.let { mSpeed ->
            val speedToShow = mSpeed.getHumanSpeed("total");
            speedTrackEvent.postValue(TrackingSpeed(speedToShow.speedValue,speedToShow.speedUnit,mSpeed.mTotalSpeed))
        }
    }

    fun bindArgument(
        appoimentId: Int,
        orderCode: String?,
        typeConsultation: String?,
        userProfile: UserProfile?,
        infoDetail: InfoDetail?
    ) {
        this.appoimentId = appoimentId
        this.orderCode = orderCode
        this.typeConsultation = typeConsultation
        this.userProfile = userProfile
        this.infoDetail = infoDetail
    }

    // region twilio videocall
    fun doGetAppointmentRoom(appoimentId: Int) {
        executeJob {
            isLoadingLiveData.value = true
            getAppointmentRoomProviderUseCase
                .getAppointmetRoomProvider(appoimentId)
                .compose(applySchedulers())
                .doOnSubscribe { isLoadingLiveData.value = true }
                .doOnTerminate { isLoadingLiveData.value = false }
                .subscribe({ appointmentRoomProvider ->
                    appointmentRoomProviderEvent.value = appointmentRoomProvider
                    when(appointmentRoomProvider.videoCallProvider){
                       "TWILIO" ->{
                           appointmentRoomProvider.videoCallProviderDataResponse?.twilio?.let {
                               videoCallProviderTwillioSubject.onNext(it)
                           }
                       }
                       "JITSI_WEB" ->{
                           appointmentRoomProvider.videoCallProviderDataResponse?.jitsi?.let {
                               videoCallProviderJitsiSubject.onNext(it)
                           }
                       }
                    }
                    when(appointmentRoomProvider.chatProvider){
                        "TWILIO" ->{
                            appointmentRoomProvider.chatProviderDataResponse?.twilio?.let {
                                if (reInitStatus != true) {
                                    chatProviderTwillioSubject.onNext(it)
                                    this.appointmentChatRoomTwillio = it
                                }
                            }

                        }
                    }

                }, {
                    isLoadingLiveData.value = false
                    handleFailure(Failure.ServerError(it.message ?: ""))
                }).disposedBy(disposable)
        }
    }

    fun onReceiveToken(
        context: Context,
        value_room: String,
        token: String,
        enableResponse: Enable?
    ) {
        this.enableEvent.value = enableResponse
        localAudioTrack = LocalAudioTrack.create(context, true, LOCAL_AUDIO_TRACK_NAME)
        cameraCapture = CameraCapturerCompat.newInstance(context)
        if (enableResponse?.video == true) {
            localVideoTrack =
                LocalVideoTrack.create(context, true, cameraCapture!!, LOCAL_VIDEO_TRACK_NAME)
            localVideoTrack?.let {
                localVideoTrackEvent.value = it
            }

        }
        localAudioTrack?.let {
            localAudioTrackEvent.value = it
        }
        cameraCapture?.let {
            cameraCapturerTrackEvent.value = it
        }
        audioSwitch?.activate()
        encodingParameters = EncodingParameters(0, 0)

        connectOptionsBuilder = ConnectOptions
            .Builder(token)
            .region("sg1")
            .roomName(value_room)
            .apply {
                bandwidthProfile(
                    BandwidthProfileOptions(
                        VideoBandwidthProfileOptions.Builder()
                            .dominantSpeakerPriority(TrackPriority.HIGH)
                            .renderDimensions(
                                mutableMapOf(
                                    TrackPriority.LOW to VideoDimensions.CIF_VIDEO_DIMENSIONS,
                                    TrackPriority.STANDARD to VideoDimensions.VGA_VIDEO_DIMENSIONS,
                                    TrackPriority.HIGH to VideoDimensions.HD_540P_VIDEO_DIMENSIONS
                                )
                            )
                            .build()
                    )
                )
                if (localAudioTrack != null) {
                    audioTracks(listOf(localAudioTrack))
                }
                if (enableResponse?.video == true) {
                    preferVideoCodecs(listOf(Vp8Codec(), H264Codec()))
                    if (localVideoTrack != null) {
                        videoTracks(listOf(localVideoTrack))
                    }
                }
                preferAudioCodecs(listOf(OpusCodec()))
                encodingParameters(encodingParameters!!)
                enableAutomaticSubscription(true)

                try {
                build().let { connectOptionBuilder ->
                    Video.connect(context, connectOptionBuilder, listenerRoom)
                }

                } catch (e: Exception) {
                    Log.i("TAGED", "onReceiveToken: "+e.printStackTrace())
                    Sentry.captureException(e)
                }

            }
    }

    fun connectSocket(appointmentId: Int,callMethod : String) {
        var option : IO.Options? = null
        if (callMethod.equals(Constant.EXTRA_CALL_METHOD_MA)) {
            option = IO.Options.builder()
                .setAuth(mapOf(Constant.TOKEN to "${Constant.BEARER} ${getAuthUseCase.getToken()}"))
                .setQuery("${Constant.QUERY_METHOD}=${Constant.QUERY_IN_ROOM_MA}&${Constant.QUERY_APPOINTMENT}=$appointmentId")
                .setReconnection(true)
                .setReconnectionAttempts(Integer.MAX_VALUE)
                .setReconnectionDelay(1_000)
                .setReconnectionDelayMax(5_000)
                .build()
        } else {
            option = IO.Options.builder()
                .setAuth(mapOf(Constant.TOKEN to "${Constant.BEARER} ${getAuthUseCase.getToken()}"))
                .setQuery("${Constant.QUERY_METHOD}=${Constant.QUERY_IN_ROOM_SP}&${Constant.QUERY_APPOINTMENT}=$appointmentId")
                .setReconnection(true)
                .setReconnectionAttempts(Integer.MAX_VALUE)
                .setReconnectionDelay(1_000)
                .setReconnectionDelayMax(5_000)
                .build()
        }
        val uri = URI(BuildConfig.BASE_SOCKET_URL)
        try {
            socket = IO.socket(uri, option)
            socket?.connect()
        } catch (e: Exception) {
            Timber.e(e)
        }

        socket?.on(Socket.EVENT_CONNECT) {
            Timber.e("Connect IN_ROOM_MA/IN_ROOM_SP")
        }

        socket?.on(Socket.EVENT_CONNECT_ERROR) {
            Timber.e("Error Connect  IN_ROOM_MA/IN_ROOM_SP ")
        }
    }

    fun disconnect() {
        socket?.close()
        socket = null
    }

    private val listenerRoom = object :  Room.Listener {
        override fun onConnected(room: Room) {
            roomTrackEvent.value = room to "onConnected"
        }

        override fun onConnectFailure(room: Room, twilioException: TwilioException) {
            Log.e(
                "TAGED",
                room.sid+
                        room.state+
                        twilioException.code+
                        twilioException.message)
            statusEventTrack.value = "onConnectFailure"
        }

        override fun onDisconnected(room: Room, twilioException: TwilioException?) {
            if (twilioException?.code == 53405
                || twilioException?.code == 20104
                || twilioException?.code == 53000
                || twilioException?.code == 53002 ) {
                Log.i("TAGED", "onDisconnected: ${room.localParticipant?.state}")
                Log.i("TAGED", "onDisconnected: ${room.remoteParticipants.firstOrNull()?.state}")

                roomTrackEvent.value = room to "onDisconnected"
            }
        }

        override fun onParticipantConnected(room: Room, remoteParticipant: RemoteParticipant) {
            roomTrackEvent.value = room to "onParticipantConnected"
            addRemoteParticipant(remoteParticipant)
            this@VideoCallVM.statusEventTrack.value = "onParticipantConnected"
        }

        override fun onParticipantDisconnected(
            room: Room,
            remoteParticipant: RemoteParticipant
        ) {
            this@VideoCallVM.remoteParticipant.value = remoteParticipant
            roomTrackEvent.value = room to "onParticipantDisconnected"
            this@VideoCallVM.statusEventTrack.value = "onParticipantDisconnected"
        }

        override fun onReconnected(room: Room) {
            statusEventTrack.value = "onReconnected"
        }

        override fun onParticipantReconnected(room: Room, remoteParticipant: RemoteParticipant) {
            statusEventTrack.value = "onParticipantReconnected"
        }

        override fun onReconnecting(room: Room, twilioException: TwilioException) {
            if (twilioException.code == 53001 || twilioException.code == 5304) {
                statusEventTrack.value = "onReconnectingLocal"
            }
        }

        override fun onParticipantReconnecting(room: Room, remoteParticipant: RemoteParticipant) {
            statusEventTrack.value = "onReconnectingRemote"
        }

        override fun onRecordingStarted(room: Room) {

        }

        override fun onRecordingStopped(room: Room) {

        }
    }

    fun addRemoteParticipant(remoteParticipant: RemoteParticipant) {
        remoteParticipant.remoteVideoTracks.firstOrNull()?.let { remoteVideoTrackPublication ->
            if (remoteVideoTrackPublication.isTrackSubscribed) {
                remoteVideoTrackPublication.remoteVideoTrack?.let {
                    remoteVideoTrack.value = it
                }
            }
        }
        remoteParticipant.setListener(remoteParticipantListener)
    }

    fun setVideoState(status : Boolean){
        stateVideo.value = status
    }

    private val remoteParticipantListener =
        object : RemoteParticipant.Listener {
            override fun onAudioTrackPublished(
                remoteParticipant: RemoteParticipant,
                remoteAudioTrackPublication: RemoteAudioTrackPublication
            ) {

            }

            override fun onAudioTrackUnpublished(
                remoteParticipant: RemoteParticipant,
                remoteAudioTrackPublication: RemoteAudioTrackPublication
            ) {

            }

            override fun onAudioTrackSubscribed(
                remoteParticipant: RemoteParticipant,
                remoteAudioTrackPublication: RemoteAudioTrackPublication,
                remoteAudioTrack: RemoteAudioTrack
            ) {
                this@VideoCallVM.remoteAudioTrack.value = remoteAudioTrack
            }

            override fun onAudioTrackSubscriptionFailed(
                remoteParticipant: RemoteParticipant,
                remoteAudioTrackPublication: RemoteAudioTrackPublication,
                twilioException: TwilioException
            ) {

            }

            override fun onAudioTrackUnsubscribed(
                remoteParticipant: RemoteParticipant,
                remoteAudioTrackPublication: RemoteAudioTrackPublication,
                remoteAudioTrack: RemoteAudioTrack
            ) {

            }

            override fun onVideoTrackPublished(
                remoteParticipant: RemoteParticipant,
                remoteVideoTrackPublication: RemoteVideoTrackPublication
            ) {

            }

            override fun onVideoTrackUnpublished(
                remoteParticipant: RemoteParticipant,
                remoteVideoTrackPublication: RemoteVideoTrackPublication
            ) {
                statusEventTrack.value = "onVideoTrackUnpublished"
            }

            override fun onVideoTrackSubscribed(
                remoteParticipant: RemoteParticipant,
                remoteVideoTrackPublication: RemoteVideoTrackPublication,
                remoteVideoTrack: RemoteVideoTrack
            ) {
                this@VideoCallVM.remoteVideoTrack.value = remoteVideoTrack
            }

            override fun onVideoTrackSubscriptionFailed(
                remoteParticipant: RemoteParticipant,
                remoteVideoTrackPublication: RemoteVideoTrackPublication,
                twilioException: TwilioException
            ) {

            }

            override fun onVideoTrackUnsubscribed(
                remoteParticipant: RemoteParticipant,
                remoteVideoTrackPublication: RemoteVideoTrackPublication,
                remoteVideoTrack: RemoteVideoTrack
            ) {
                this@VideoCallVM.statusEventTrack.value = "onVideoTrackUnsubscribed"
            }

            override fun onDataTrackPublished(
                remoteParticipant: RemoteParticipant,
                remoteDataTrackPublication: RemoteDataTrackPublication
            ) {

            }

            override fun onDataTrackUnpublished(
                remoteParticipant: RemoteParticipant,
                remoteDataTrackPublication: RemoteDataTrackPublication
            ) {

            }

            override fun onDataTrackSubscribed(
                remoteParticipant: RemoteParticipant,
                remoteDataTrackPublication: RemoteDataTrackPublication,
                remoteDataTrack: RemoteDataTrack
            ) {

            }

            override fun onDataTrackSubscriptionFailed(
                remoteParticipant: RemoteParticipant,
                remoteDataTrackPublication: RemoteDataTrackPublication,
                twilioException: TwilioException
            ) {

            }

            override fun onDataTrackUnsubscribed(
                remoteParticipant: RemoteParticipant,
                remoteDataTrackPublication: RemoteDataTrackPublication,
                remoteDataTrack: RemoteDataTrack
            ) {

            }

            override fun onVideoTrackEnabled(
                remoteParticipant: RemoteParticipant,
                remoteVideoTrackPublication: RemoteVideoTrackPublication
            ) {
                statusEventTrack.value = "onVideoTrackEnabled"
            }

            override fun onVideoTrackDisabled(
                remoteParticipant: RemoteParticipant,
                remoteVideoTrackPublication: RemoteVideoTrackPublication
            ) {
                statusEventTrack.value = "onVideoTrackDisabled"
            }

            override fun onAudioTrackEnabled(
                remoteParticipant: RemoteParticipant,
                remoteAudioTrackPublication: RemoteAudioTrackPublication
            ) {
                statusEventTrack.value = "onAudioTrackEnabled"
            }

            override fun onAudioTrackDisabled(
                remoteParticipant: RemoteParticipant,
                remoteAudioTrackPublication: RemoteAudioTrackPublication
            ) {
                statusEventTrack.value = "onAudioTrackDisabled"
            }

            override fun onNetworkQualityLevelChanged(
                remoteParticipant: RemoteParticipant,
                networkQualityLevel: NetworkQualityLevel
            ) {
                statusEventTrack.value = networkQualityLevel.name
            }

        }

    fun setShareScreenFlag(flag: Boolean) {
        createShareUseCase.setShareScreen(flag)
    }

    fun getShareScreenFlag(): Boolean {
        return getShareScreenUseCase.getShareScreen()
    }

    fun invalidateShareScreen() {
        getShareScreenUseCase.invalidate()
    }

    fun onAcceptEndVideo() {
        endCallConfirmationEvent.value = Empty()
    }
    // endregion

    // region twilio chat
    /**
     * create channel by roomcode
     */
    private fun createChannel() {
        if (reInitStatus != true) {
            chatClient?.channels?.channelBuilder()
                ?.withFriendlyName(appointmentChatRoomTwillio?.identity)
                ?.withUniqueName(appointmentChatRoomTwillio?.roomCode)
                ?.withType(Channel.ChannelType.PUBLIC)
                ?.build(object : CallbackListener<Channel>() {
                    override fun onSuccess(channel: Channel) {
                        mChannel = channel.apply {
                            join(object : StatusListener() {
                                override fun onSuccess() {
                                    getLastMessage()
                                    chatConnectionStatusSubject.onNext(false to true)
                                    channel.addListener(channelListener)
                                }
                            })
                        }
                    }

                    override fun onError(errorInfo: ErrorInfo?) {
                        super.onError(errorInfo)
                        Timber.e(errorInfo?.message)
                        Timber.e("${errorInfo?.status}")
                        errorInfo?.message?.let { Sentry.captureMessage(it) }
                        Sentry.captureMessage(errorInfo?.status.toString())
                        chatConnectionStatusSubject.onNext(false to true)
                    }
                })
        }
    }

    /**
     * after client is connected, get channel by room code.
     * if channel is empty, create channel else join to channel
     */
    fun loadChannels() {
        chatClient?.channels?.getChannel(appointmentChatRoomTwillio?.roomCode,
            object : CallbackListener<Channel>() {
                override fun onSuccess(channel: Channel?) {
                    if (channel != null) {
                        mChannel = channel
                        if (channel.status != Channel.ChannelStatus.JOINED) {
                            channel.join(object : StatusListener() {
                                override fun onSuccess() {
                                    getLastMessage()
                                    chatConnectionStatusSubject.onNext(false to true)
                                }

                                override fun onError(errorInfo: ErrorInfo?) {
                                    super.onError(errorInfo)
                                    if (errorInfo?.code == 50404) {
                                        chatConnectionStatusSubject.onNext(false to true)
                                    }
                                    Timber.e(errorInfo?.message)
                                    Timber.e((errorInfo?.code ?: 0).toString())
                                }
                            })
                        } else {
                            chatConnectionStatusSubject.onNext(false to true)
                        }
                        channel.addListener(channelListener)
                    } else {
                        createChannel()
                    }
                }

                override fun onError(errorInfo: ErrorInfo?) {
                    super.onError(errorInfo)
                    createChannel()
                    Sentry.captureMessage(errorInfo?.message.toString())
                }
            })
    }

    fun setTwilioChatError() {
        chatConnectionStatusSubject.onNext(true to false)
    }

    fun getChatClientProperties(): ChatClient.Properties {
        return ChatClient.Properties.Builder()
            .setRegion("us1")
            .setDeferCertificateTrustToPlatform(false)
            .createProperties()
    }

    fun sendMessage(message: Message.Options, isUsingProgress: Boolean, adapterPosition: Int) {
        if (::mChannel.isInitialized) {
            mChannel.messages?.sendMessage(message, object : CallbackListener<Message>() {
                override fun onSuccess(message: Message) {
                    senderMessageCallbackEvent.value = true to adapterPosition
                }

                override fun onError(errorInfo: ErrorInfo?) {
                    Timber.e(errorInfo?.message)
                    Sentry.captureMessage(errorInfo?.message.toString())
                    senderMessageCallbackEvent.value = false to adapterPosition
                }
            })

            if (isUsingProgress) {
                message.withMediaProgressListener(object : ProgressListener() {
                    override fun onStarted() {}

                    override fun onProgress(bytes: Long) {
                        senderMessageProgressEvent.value = Triple(false, bytes, adapterPosition)
                    }

                    override fun onCompleted(p0: String?) {
                        senderMessageProgressEvent.value = Triple(true, -1L, adapterPosition)
                    }
                })
            }
        } else {
            senderMessageCallbackEvent.value = false to adapterPosition
            senderMessageProgressEvent.value = Triple(true, -1L, adapterPosition)
        }
    }

    val channelListener = object : AlteaTwillioListener.ChannelListener {
        override fun onMessageAdded(p0: Message?) {
            statusEventTrack.value = "onMessageAdded"
            p0?.let {
                message.value = it  to false
            }
        }
    }

    fun getLastMessage(){
        if (::mChannel.isInitialized) mChannel.run {
            mChannel.messages.getLastMessages(50,object : CallbackListener<List<Message>>(){
                override fun onSuccess(p0: List<Message>?) {
                    p0?.map {
                        message.value = it to true
                    }
                }

            })
        }
    }

    fun leaveChannelChat() {
        if (::mChannel.isInitialized) mChannel.run {
            leave(object : StatusListener() {
                override fun onSuccess() {
                    Timber.e("Leave channel")
                    removeAllListeners()
                }
            })
        }
    }

    fun startDownload(
        url: String,
        pathFolder: String,
        nameFile: String,
        fetchConfiguration: FetchConfiguration
    ) {
        val file = File(pathFolder)
        if (!file.exists()) {
            file.mkdirs()
        }

        val downloadPath = pathFolder + nameFile
        val fetch = Fetch.getInstance(fetchConfiguration!!)
        var request = Request(url, downloadPath)
        request.priority = Priority.HIGH
        request.networkType = NetworkType.ALL

        fetch.enqueue(request, { result ->
            request = result
        }) {
        }
        fetch.addListener(fetchListener())
    }

    private fun fetchListener(): FetchListener {
        return (object : FetchListener {
            override fun onAdded(download: Download) {

            }

            override fun onCancelled(download: Download) {
                statusDownloadLiveData.postValue(download.status)
            }

            override fun onCompleted(download: Download) {
                statusDownloadLiveData.postValue(download.status)
            }

            override fun onDeleted(download: Download) {
                statusDownloadLiveData.postValue(download.status)
            }

            override fun onDownloadBlockUpdated(
                download: Download,
                downloadBlock: DownloadBlock,
                totalBlocks: Int
            ) {
                statusDownloadLiveData.postValue(download.status)
            }

            override fun onError(
                download: Download,
                error: com.tonyodev.fetch2.Error,
                throwable: Throwable?
            ) {
                statusDownloadLiveData.postValue(download.status)
                errorEvent.postValue(error)
            }


            override fun onPaused(download: Download) {
                statusDownloadLiveData.postValue(download.status)
            }

            override fun onProgress(
                download: Download,
                etaInMilliSeconds: Long,
                downloadedBytesPerSecond: Long
            ) {
                statusDownloadLiveData.value = download.status
                progressDownloadLiveData.postValue(download.progress)
            }

            override fun onQueued(download: Download, waitingOnNetwork: Boolean) {
                statusDownloadLiveData.postValue(download.status)
            }

            override fun onRemoved(download: Download) {
                statusDownloadLiveData.postValue(download.status)
            }

            override fun onResumed(download: Download) {
                statusDownloadLiveData.postValue(download.status)
            }

            override fun onStarted(
                download: Download,
                downloadBlocks: List<DownloadBlock>,
                totalBlocks: Int
            ) {
                statusDownloadLiveData.postValue(download.status)
            }

            override fun onWaitingNetwork(download: Download) {
                statusDownloadLiveData.postValue(download.status)
            }

        })
    }

    fun sendEventEndCallMaToAnalytics(appointmentId: String, orderCode: String, roomCode: String) {
        analyticManager.trackingEventEndCallMa(
            EndCallMaPayloadBuilder(
                appointmentId,
                orderCode,
                roomCode
            )
        )
    }

    fun sendTrackingLastDoctorChatName(doctorName: String?) {
        analyticManager.trackingLastDoctorChatName(doctorName)
    }

    private fun doPingConnection()  {
        var response  : Response? = null
        Single.fromCallable {
            try {
                val client = OkHttpClient()

                val urlBuilder = "https://twilio.com".toHttpUrlOrNull()?.newBuilder()

                val url = urlBuilder?.build().toString()

                val request = okhttp3.Request.Builder().url(url).build()
                response =  client.newCall(request).execute()


            }catch (e : Exception){
                e.printStackTrace()
            }catch (ex: IOException) {
                ex.printStackTrace()
            } catch (ex: NullPointerException) {
                ex.printStackTrace()
            }finally {

            }
        }.subscribeOn(Schedulers.io())
            .subscribe({},{}).disposedBy(disposable)

    }


    // endregion
    override fun onCleared() {
        super.onCleared()
        chatClient?.removeAllListeners()
        chatClient?.shutdown()
        localAudioTrack?.release()
        localVideoTrack?.release()
        cameraCapture?.stopCapture()
        cameraCapture?.dispose()
        remoteParticipant
    }
}

