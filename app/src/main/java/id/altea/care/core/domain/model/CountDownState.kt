package id.altea.care.core.domain.model

/**
 * Created by trileksono on 09/03/21.
 */
sealed class CountDownState {
    data class onTick(val time: String) : CountDownState()
    object onFinish : CountDownState()
    object onConnect : CountDownState()
    object onDisconnect : CountDownState()
}
