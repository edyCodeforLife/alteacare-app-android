package id.altea.care.core.notification

import android.app.Activity
import android.app.Notification
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.moengage.pushbase.model.NotificationPayload
import com.moengage.pushbase.push.PushMessageListener
import id.altea.care.R
import id.altea.care.core.domain.event.AppointmentStatusUpdateEvent
import id.altea.care.core.helper.RxBus
import id.altea.care.view.common.enums.TypeAppointment
import id.altea.care.view.common.enums.TypeNotification
import id.altea.care.view.splashscreen.SplashActivity
import timber.log.Timber


class AlteaNotificationMoeExtension : PushMessageListener() {

    override fun onNotificationReceived(context: Context, payload: Bundle) {
        super.onNotificationReceived(context, payload)
        try {
            val typePayloadData = payload.getString("push_notification_type", "")
            val appointmentId = payload.getString("appointment_id", "")
            val typeNotification = TypeNotification.valueOf(typePayloadData)
            RxBus.post(
                AppointmentStatusUpdateEvent(
                    appointmentId.toInt(),
                    when (typeNotification) {
                        TypeNotification.PUSH_NOTIFICATION_APPOINTMENT_WAITING_FOR_PAYMENT -> {
                            TypeAppointment.WAITING_FOR_PAYMENT
                        }
                        TypeNotification.PUSH_NOTIFICATION_APPOINTMENT_PAYMENT_SUCCESS -> {
                            TypeAppointment.PAID
                        }
                        TypeNotification.PUSH_NOTIFICATION_APPOINTMENT_CANCELED_BY_GP -> {
                            TypeAppointment.CANCELED_BY_GP
                        }
                        TypeNotification.PUSH_NOTIFICATION_APPOINTMENT_CANCELED_BY_SYSTEM -> {
                            TypeAppointment.CANCELED_BY_SYSTEM
                        }
                        TypeNotification.PUSH_NOTIFICATION_APPOINTMENT_REFUNDED -> {
                            TypeAppointment.REFUNDED
                        }
                        TypeNotification.PUSH_NOTIFICATION_APPOINTMENT_MEET_SPECIALIST -> {
                            TypeAppointment.MEET_SPECIALIST
                        }
                        TypeNotification.PUSH_NOTIFICATION_APPOINTMENT_COMPLETED -> {
                            TypeAppointment.COMPLETED
                        }
                        TypeNotification.PUSH_NOTIFICATION_APPOINTMENT_WILL_ENDED_IN_10MINUTES ->{
                            TypeAppointment.NOTIFICATIONWILLENDEDIN10MINUTES
                        }
                        else -> null
                    }
                )
            )
        } catch (e: Exception) {
            Timber.e(e)
        }

    }


    override fun onHandleRedirection(activity: Activity, payload: Bundle) {
        val urlDataLink = payload.getString("gcm_webUrl")
        val intent = activity.applicationContext?.let {
            SplashActivity.createIntent(it).apply {
                action = Intent.ACTION_VIEW
                data = Uri.parse(urlDataLink)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
        }
        activity.startActivity(intent)
    }

}