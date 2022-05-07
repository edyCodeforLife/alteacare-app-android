package id.altea.care.core.ext

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.*
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import id.altea.care.R
import id.altea.care.core.domain.model.InfoDetail
import kotlinx.android.synthetic.main.activity_view_document.*
import kotlinx.android.synthetic.main.activity_view_document.view.*
import kotlinx.android.synthetic.main.content_detail_info.view.*
import kotlinx.android.synthetic.main.dialog_change_patient.view.*
import kotlinx.android.synthetic.main.dialog_delete_img_profile.view.*
import kotlinx.android.synthetic.main.dialog_payment_back_confirmation.view.*
import kotlinx.android.synthetic.main.dialog_payment_back_confirmation.view.dialogPaymentTxtContent
import kotlinx.android.synthetic.main.dialog_payment_back_confirmation.view.dialogPaymentTxtContentCenter
import kotlinx.android.synthetic.main.dialog_remove_family.view.*
import permissions.dispatcher.PermissionRequest
import java.io.File
import java.util.*


fun showDatePickerDialog(
    caller: Context,
    year: Int,
    month: Int,
    date: Int,
    onDateSelected: (String) -> Unit,
    onDismissListener: (() -> Unit)? = null
) {
    val datePicker = DatePickerDialog(
        caller,
        R.style.DatePicker,
        { datePicker, dYear, dMonth, dDate ->
            val monthString = if (dMonth + 1 < 10) "0${dMonth + 1}" else "${dMonth + 1}"
            val dayString = if (dDate < 10) "0$dDate" else "$dDate"
            onDateSelected("$dayString/$monthString/$dYear")
        },
        year,
        month,
        date
    )
    datePicker.datePicker.maxDate = Date().time - 1000
    val window = datePicker.window
    window?.let {
        it.setBackgroundDrawableResource(android.R.color.transparent)
        it.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        it.setGravity(Gravity.CENTER)
    }
    datePicker.show()
    datePicker.setOnDismissListener { onDismissListener?.let { it() } }

}

fun showRationaleDialog(context: Context, @StringRes message: Int, permission: PermissionRequest) {
    AlertDialog.Builder(context)
        .setTitle(R.string.permission_allow)
        .setPositiveButton(R.string.permission_allow) { _, _ -> permission.proceed() }
        .setCancelable(false)
        .setMessage(message)
        .show()
}

fun showPermissionSettingDialog(context: Activity, @StringRes message: Int) {
    AlertDialog.Builder(context)
        .setPositiveButton(R.string.permission_allow) { _, _ ->
            context.startActivity(Intent(android.provider.Settings.ACTION_APPLICATION_SETTINGS))
        }
        .setCancelable(false)
        .setMessage(message)
        .show()
}


fun showDialogChangePatient(caller: Context, onAcceptSelected: () -> Unit) {
    val dialogView = LayoutInflater.from(caller).inflate(R.layout.dialog_change_patient, null)
    val alertDialogView = AlertDialog.Builder(caller).create()
    alertDialogView.setView(dialogView)



    dialogView.dialogBtnCancel.setOnClickListener {
        alertDialogView.dismiss()
    }

    dialogView.dialogBtnExit.setOnClickListener {
        onAcceptSelected()
    }

    alertDialogView.show()
}


fun showDialog(caller : Context,infoDetail: InfoDetail?){
    val dialogView = LayoutInflater.from(caller).inflate(
        R.layout.content_detail_info,
        null
    )
    val alertDialogView = AlertDialog.Builder(caller).create()
    alertDialogView.setView(dialogView)
    alertDialogView.requestWindowFeature(Window.FEATURE_NO_TITLE)
    alertDialogView.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    val wmlp = alertDialogView.window?.attributes
    wmlp?.gravity = Gravity.TOP or Gravity.LEFT
    wmlp?.x = 20 //x position
    wmlp?.y = 150 //y position

    infoDetail?.let {
        dialogView.ctnDetailInfoTextName.text = it.name ?: "-"
        dialogView.ctnDetailInfoTextSpesialis.text = it.specialist ?: "-"
        dialogView.ctnDetailInfoTextSchedule.text = it.schedule ?: "-"
    }

    alertDialogView.show()

}


fun showDialogImage(caller: Context,file : File?,url : String?){
    val dialogView = LayoutInflater.from(caller).inflate(R.layout.activity_view_document,null)
    val alertDialogView = AlertDialog.Builder(caller).create()
    alertDialogView.setView(dialogView)


    dialogView.viewDocumentBtnClose.setOnClickListener {
        alertDialogView.dismiss()
    }
    if (file != null) {
        Glide.with(dialogView.viewDocumentImage)
            .load(file)
            .placeholder(R.drawable.ic_img_placeholder)
            .into(dialogView.viewDocumentImage)
    }else{
        url?.let { dialogView.viewDocumentImage.loadImage(it) }
    }

    alertDialogView.show()

}

fun showDialogRemoveFamily(caller : Context,onAcceptSelected: () -> Unit){
    val dialogView = LayoutInflater.from(caller).inflate(R.layout.dialog_remove_family,null)
    val alertDialogView = AlertDialog.Builder(caller).create()
    alertDialogView.setView(dialogView)

    dialogView.dialogRemoveFamilyYes.setOnClickListener {
        onAcceptSelected()
        alertDialogView.dismiss()
    }

    dialogView.dialogRemoveFamilyBtnCancel.setOnClickListener {
        alertDialogView.dismiss()
    }

    alertDialogView.show()
}

fun showDialogDeletePhotoProfile(caller: Context,onRemoveClicked : () -> Unit) {
    val dialogView = LayoutInflater.from(caller).inflate(R.layout.dialog_delete_img_profile, null)
    val alertDialogView = AlertDialog.Builder(caller).create()
    alertDialogView.setView(dialogView)

    dialogView.dialogBtnRemove.setOnClickListener {
        onRemoveClicked()
        alertDialogView.dismiss()
    }

    dialogView.dialogBtnCancelImgProfile.setOnClickListener {
        alertDialogView.dismiss()
    }
    alertDialogView.show()
}

fun showDialogBackConfirmationPayment(
    caller: Context,
    @StringRes stringRes: Int = R.string.payment_back_dialog_confirmation,
    confirmCallback: () -> Unit
) {
    val dialogView =
        LayoutInflater.from(caller).inflate(R.layout.dialog_payment_back_confirmation, null)
    val alertDialogView = AlertDialog.Builder(caller).create()
    dialogView.run {
        dialogPaymentTxtContent.text = context.getText(stringRes)
        if (dialogPaymentTxtContent.text.equals(context.getText(R.string.payment_back_dialog_confirmation))){
            dialogPaymentTxtContentCenter.isVisible =true
        }
    }
    alertDialogView.setView(dialogView)

    val window = alertDialogView.window
    window?.run {
        setLayout(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    dialogView.dialogPaymentBtnCancel.setOnClickListener {
        alertDialogView.dismiss()
    }

    dialogView.dialogPaymentBtnClose.setOnClickListener {
        alertDialogView.dismiss()
        confirmCallback()
    }
    alertDialogView.show()
}
