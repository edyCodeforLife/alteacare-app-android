package id.altea.care.view.transition.specialist

import id.altea.care.core.domain.model.InfoDetail
import id.altea.care.core.helper.util.Constant
import id.altea.care.view.videocall.VideoCallActivity

class TransitionSpecialistRouter {

    fun openVideoCallActivity(
        source: TransitionSpecialistActivity,
        appointmentId: Int,
        infoDetail: InfoDetail?
    ) {
        source.startActivity(
            VideoCallActivity.createIntent(
                source, appointmentId,
                "",
                Constant.EXTRA_CALL_METHOD_SPECIALIST,
                null,
                infoDetail
            )
        )
        source.finish()
    }

}