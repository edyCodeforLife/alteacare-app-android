package id.altea.care.view.transition

import android.os.CountDownTimer
import android.util.Log
import com.google.gson.Gson
import id.altea.care.BuildConfig
import id.altea.care.core.base.BaseViewModel
import id.altea.care.core.data.response.GeneralErrorResponse
import id.altea.care.core.domain.model.CountDownState
import id.altea.care.core.domain.model.Empty
import id.altea.care.core.domain.model.RoomInfo
import id.altea.care.core.domain.model.RoomsInfo
import id.altea.care.core.domain.usecase.CancelReasonOrderUseCase
import id.altea.care.core.domain.usecase.GetAuthUseCase
import id.altea.care.core.domain.usecase.GetOperationalHourMaUseCase
import id.altea.care.core.exception.Failure
import id.altea.care.core.ext.TagInjection
import id.altea.care.core.ext.disposedBy
import id.altea.care.core.ext.getGeneralErrorServer
import id.altea.care.core.helper.NetworkHandler
import id.altea.care.core.helper.SingleLiveEvent
import id.altea.care.core.helper.util.Constant
import io.reactivex.Scheduler
import io.socket.client.IO
import io.socket.client.Socket
import retrofit2.HttpException
import timber.log.Timber
import java.net.URI
import java.util.*
import javax.inject.Inject
import javax.inject.Named
import kotlin.collections.ArrayList

class TransitionVM @Inject constructor(
    @Named(TagInjection.UI_Scheduler) uiSchedulers: Scheduler,
    @Named(TagInjection.IO_Scheduler) ioScheduler: Scheduler,
    networkHandler: NetworkHandler,
    private val getAuthUseCase: GetAuthUseCase,
    private val operationalHourMaUseCase: GetOperationalHourMaUseCase,
    private val cancelReasonOrderUseCase: CancelReasonOrderUseCase
) : BaseViewModel(uiSchedulers, ioScheduler, networkHandler) {

    private var socket: Socket? = null
    val answeredSocket = SingleLiveEvent<Empty>()
    val isSocketConnectedEvent = SingleLiveEvent<Boolean>()
    val isSocketMaBusyEvent = SingleLiveEvent<Boolean>()

    private var countDownTimer: CountDownTimer? = null
    val firstcountDownState = SingleLiveEvent<CountDownState>()
    val delayCountDownState = SingleLiveEvent<Pair<CountDownState, CountDownState>>()
    val connectEvent = SingleLiveEvent<Boolean>()
    var first: Boolean = true
    val resultEvent = SingleLiveEvent<Boolean>()
    fun connectSocket(appointmentId: Int) {
        val option = IO.Options.builder()
            .setAuth(mapOf(Constant.TOKEN to "${Constant.BEARER} ${getAuthUseCase.getToken()}"))
            .setQuery("${Constant.QUERY_METHOD}=${Constant.QUERY_CALL_MA}&${Constant.QUERY_APPOINTMENT}=$appointmentId")
            .setReconnection(true)
            .setReconnectionAttempts(Integer.MAX_VALUE)
            .setReconnectionDelay(1_000)
            .setReconnectionDelayMax(5_000)
            .build()

        val uri = URI(BuildConfig.BASE_SOCKET_URL)
        try {
            socket = IO.socket(uri, option)
            socket?.connect()
        } catch (e: Exception) {
            Timber.e(e)
        }

        socket?.on(Socket.EVENT_CONNECT) {
            isSocketConnectedEvent.postValue(true)
        }

        socket?.on(Socket.EVENT_CONNECT_ERROR) {
            isSocketConnectedEvent.postValue(false)
        }

        socket?.on(Constant.EXTRA_APPOINTMENT_INFO){
           val dataJson =  Gson().toJson(it)
           val eventDataJson = Gson().fromJson(
               dataJson.toString(),
               RoomsInfo::class.java
           )

            Log.e("TAGED", "connectSocket: "+dataJson)

            eventDataJson.forEach { roomInfo ->
                when(roomInfo.nameValuePairs?.type){
                    "MA_AVAILABILITY" ->{
                        isSocketMaBusyEvent.postValue(roomInfo.nameValuePairs.data?.nameValuePairs?.isMaAvailable ?: false)
                    }
                }

            }

        }

        socket?.on(Constant.SOCKET_EVENT_ME) { Timber.e(Gson().toJson(it)) }

        socket?.on(Constant.SOCKET_EVENT_CALL_MA_ANSWERED) {
            Timber.e("Call MA Answered")
            answeredSocket.postValue(Empty())
        }
    }

    fun getCountDown(countDownState: CountDownState) {
        operationalHourMaUseCase
            .getOperationalHourMA()
            .compose(applySchedulers())
            .subscribe({
                if (first) {
                    it.firstDelaySocketConnect?.let { first_delay_socket_connect ->
                        startCountDown(first_delay_socket_connect, countDownState)
                    }
                } else {
                    it.delaySocketConnect?.let { delaySocketConnect ->
                        startCountDown(delaySocketConnect, countDownState)
                    }
                }
            }, {
                it.getGeneralErrorServer()
            }).disposedBy(disposable)


    }

    fun disconnect() {
        socket?.close()
        socket = null
    }

    private fun startCountDown(countdowntimer: Int, countDownState: CountDownState) {
        countDownTimer?.cancel()
        val milis: Int = countdowntimer * 1000
        countDownTimer = object : CountDownTimer(milis.toLong(), 1000L) {
            override fun onTick(millisUntilFinished: Long) {
                val minutes = (millisUntilFinished / 1000).toInt() / 60
                val seconds = (millisUntilFinished / 1000).toInt() % 60
                val timeLeftFormatted: String =
                    String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
                if (first) {
                    firstcountDownState.value = CountDownState.onTick(timeLeftFormatted)
                } else {
                    delayCountDownState.value =
                        countDownState to CountDownState.onTick(timeLeftFormatted)
                }
            }

            override fun onFinish() {
                if (first) {
                    firstcountDownState.value = CountDownState.onFinish
                    first = false
                } else {
                    delayCountDownState.value = countDownState to CountDownState.onFinish
                }
            }
        }.start()

    }

    fun pauseCountDownTimer() {
        countDownTimer?.cancel()
    }

    fun setCancelReasonOrder(appointmentId: Int, note: String) {
        cancelReasonOrderUseCase
            .setCancelReasonOrder(appointmentId, note)
            .compose(applySchedulers())
            .doOnSubscribe { isLoadingLiveData.value = true }
            .doOnTerminate { isLoadingLiveData.value = false }
            .subscribe({
                resultEvent.value = it
            }, {
                if (it is HttpException && it.code() == 400) {
                    val response = Gson().fromJson(
                        it.response()?.errorBody()?.charStream(),
                        GeneralErrorResponse::class.java
                    )
                    resultEvent.value = response.status
                    return@subscribe
                }
                handleFailure(it.getGeneralErrorServer())
            }).disposedBy(disposable)
    }

    override fun onCleared() {
        super.onCleared()
        countDownTimer?.cancel()
    }
}
