package id.altea.care.view.myconsultation.item

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.core.view.marginTop
import com.mikepenz.fastadapter.binding.AbstractBindingItem
import id.altea.care.R
import id.altea.care.core.domain.model.MyAppointment
import id.altea.care.core.ext.loadImage
import id.altea.care.core.ext.toNewFormat
import id.altea.care.databinding.ItemMyConsultationScheduleBinding
import id.altea.care.view.common.enums.TypeAppointment
import timber.log.Timber

class MyConsultationItem(
    val myAppointment: MyAppointment,
    private val inOperationalHour: Boolean? = false
) :
    AbstractBindingItem<ItemMyConsultationScheduleBinding>() {

    override val type: Int
        get() = hashCode()

    override fun createBinding(
        inflater: LayoutInflater,
        parent: ViewGroup?
    ): ItemMyConsultationScheduleBinding {
        return ItemMyConsultationScheduleBinding.inflate(inflater, parent, false)
    }

    @SuppressLint("SetTextI18n")
    override fun bindView(binding: ItemMyConsultationScheduleBinding, payloads: List<Any>) {
        Timber.d("inOperationalHour is -> $inOperationalHour")
        myAppointment.patient?.let {
            binding.itemGroupPatientTxtName.text =
                "${it.familyRelationType?.name} - ${it.firstName} ${it.lastName}"
        }

        binding.layoutConsul.run {
            when (myAppointment.status){
                TypeAppointment.UNVERIFIED, TypeAppointment.NEW, TypeAppointment.PROCESS_GP -> {
                    if (inOperationalHour == true) {
                        txtInOperationalHour.isVisible = true
                        txtInOperationalHour.text = root.context.getString(R.string.connect_to_ma)
                        txtInOperationalHour.setBackgroundColor(root.context.getColor(R.color.primary))
                    } else {
                        txtInOperationalHour.isVisible = true
                        txtInOperationalHour.text = root.context.getString(R.string.waiting_operational_ma)
                    }
                }

                else -> txtInOperationalHour.isVisible = false
            }

            itemScheduleTxtOrderId.text =
                String.format(root.context.getString(R.string.order_id_s), myAppointment.orderCode)

            itemScheduleTxtDate.text = myAppointment.scheduleDate.toNewFormat(
                oldFormat = "yyyy-MM-dd", newFormat = "EEEE, dd MMM yyyy", true
            )
            itemScheduleTxtTime.text =
                "${myAppointment.scheduleStart} - ${myAppointment.scheduleEnd}"

            contentDoctor.run {
                docProfileTxtRsName.text = myAppointment.hospitalName
                docProfileImgDoctor.loadImage(myAppointment.doctorImage)
                docProfileTxtDoctorName.text = myAppointment.doctorName
                docProfileTxtDoctorTitle.text = myAppointment.specialization
                docProfileImgRsIcon.loadImage(myAppointment.hospitalImage)
            }
            myAppointment.statusDetail?.let {
                itemScheduleTxtStatus.text = it.label
                itemScheduleTxtStatus.setBackgroundColor(Color.parseColor(it.bgColor))
                itemScheduleTxtStatus.setTextColor(Color.parseColor(it.textColor))
            }
        }
    }
}
