package id.altea.care.view.transition.specialist

import com.google.gson.Gson
import id.altea.care.BuildConfig
import id.altea.care.core.analytics.AnalyticManager
import id.altea.care.core.base.BaseViewModel
import id.altea.care.core.domain.model.ConsultationDoctor
import id.altea.care.core.domain.model.Empty
import id.altea.care.core.domain.usecase.GetAuthUseCase
import id.altea.care.core.ext.TagInjection
import id.altea.care.core.helper.NetworkHandler
import id.altea.care.core.helper.SingleLiveEvent
import id.altea.care.core.helper.util.Constant
import io.reactivex.Scheduler
import io.socket.client.IO
import io.socket.client.Socket
import timber.log.Timber
import java.net.URI
import javax.inject.Inject
import javax.inject.Named

class TransitionSpecialistVM @Inject constructor(
    @Named(TagInjection.UI_Scheduler) uiSchedulers: Scheduler,
    @Named(TagInjection.IO_Scheduler) ioScheduler: Scheduler,
    networkHandler: NetworkHandler,
    private val getAuthUseCase: GetAuthUseCase,
    private val analyticManager: AnalyticManager
) : BaseViewModel(uiSchedulers, ioScheduler, networkHandler) {

    private var socket: Socket? = null
    val answeredSocket = SingleLiveEvent<Empty>()
    val isSocketConnectedEvent = SingleLiveEvent<Boolean>()

    fun trackingLastDoctorChatName(consultationDoctor: ConsultationDoctor?) {
        analyticManager.trackingLastDoctorChatName(consultationDoctor?.name)
    }

    fun connectSocket(appointmentId: Int) {
        val option = IO.Options.builder()
            .setAuth(mapOf(Constant.TOKEN to "${Constant.BEARER} ${getAuthUseCase.getToken()}"))
            .setQuery("${Constant.QUERY_METHOD}=${Constant.QUERY_CALL_CONSULTATION}&${Constant.QUERY_APPOINTMENT}=$appointmentId")
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

        socket?.on(Constant.SOCKET_EVENT_ME) { Timber.e(Gson().toJson(it)) }

        socket?.on(Constant.SOCKET_EVENT_CALL_CONSULTATION_ANSWERED) {
            answeredSocket.postValue(Empty())
        }
    }

    fun disconnect() {
        socket?.close()
        socket = null
    }
}