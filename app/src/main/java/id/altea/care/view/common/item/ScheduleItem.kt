package id.altea.care.view.common.item

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.mikepenz.fastadapter.binding.AbstractBindingItem
import com.mikepenz.fastadapter.binding.BindingViewHolder
import id.altea.care.R
import id.altea.care.core.domain.model.MyAppointment
import id.altea.care.core.ext.loadImage
import id.altea.care.core.ext.toNewFormat
import id.altea.care.databinding.ItemHomeScheduleBinding
import id.altea.care.view.common.enums.TypeAppointment

class ScheduleItem(
    val myAppointment: MyAppointment,
    private val inOperationalHour: Boolean? = false
) : AbstractBindingItem<ItemHomeScheduleBinding>() {
    override val type: Int
        get() = hashCode()

    override fun createBinding(
        inflater: LayoutInflater,
        parent: ViewGroup?
    ): ItemHomeScheduleBinding {
        return ItemHomeScheduleBinding.inflate(inflater, parent, false)
    }

    override fun getViewHolder(parent: ViewGroup): BindingViewHolder<ItemHomeScheduleBinding> {
        val holder = super.getViewHolder(parent)
        holder.setIsRecyclable(true)
        return holder
    }

    override fun bindView(binding: ItemHomeScheduleBinding, payloads: List<Any>) {
        binding.run {
            when (myAppointment.status) {
                TypeAppointment.UNVERIFIED, TypeAppointment.NEW, TypeAppointment.PROCESS_GP -> {
                    if (inOperationalHour == true) {
                        txtInOperationalHour.isVisible = true
                        txtInOperationalHour.text = root.context.getString(R.string.connect_to_ma)
                        txtInOperationalHour.setBackgroundColor(root.context.getColor(R.color.primary))
                    } else {
                        txtInOperationalHour.isVisible = true
                        txtInOperationalHour.text =
                            root.context.getString(R.string.waiting_operational_ma)
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
