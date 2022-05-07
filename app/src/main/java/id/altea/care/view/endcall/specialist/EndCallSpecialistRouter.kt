package id.altea.care.view.endcall.specialist

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import id.altea.care.core.domain.model.Consultation
import id.altea.care.core.domain.model.ConsultationDetail
import id.altea.care.core.helper.util.ConstantIndexMenu
import id.altea.care.view.common.bottomsheet.OrderMedicineBottomSheet
import id.altea.care.view.common.enums.TypeAppointment
import id.altea.care.view.consultationdetail.ConsultationDetailActivity
import id.altea.care.view.main.MainActivity

class EndCallSpecialistRouter {

    fun openOrderBottomSheetMedicine(source : AppCompatActivity){
       OrderMedicineBottomSheet.newInstance().show(source.supportFragmentManager,"orderMedicine")
    }

    fun openMyConsultation(source: AppCompatActivity,consultationId : Int){
        source.startActivity(ConsultationDetailActivity.createIntent(source, TypeAppointment.WATCH_MEMO_ALTEA,consultationId).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        })
        source.finish()
    }

}