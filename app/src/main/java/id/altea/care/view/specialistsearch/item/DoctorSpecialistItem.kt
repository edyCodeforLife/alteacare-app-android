package id.altea.care.view.specialistsearch.item

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.mikepenz.fastadapter.binding.AbstractBindingItem
import id.altea.care.R
import id.altea.care.core.domain.model.Doctor
import id.altea.care.core.ext.loadImage
import id.altea.care.databinding.ItemDoctorSpecialistBinding

class DoctorSpecialistItem(
    val doctorSpecialist: Doctor
) : AbstractBindingItem<ItemDoctorSpecialistBinding>() {

    override val type: Int get() = hashCode()

    override fun createBinding(
        inflater: LayoutInflater,
        parent: ViewGroup?
    ): ItemDoctorSpecialistBinding {
        return ItemDoctorSpecialistBinding.inflate(inflater, parent, false)
    }

    override fun bindView(binding: ItemDoctorSpecialistBinding, payloads: List<Any>) {
        binding.run {

            itemDoctorSpImgDoctor.loadImage(doctorSpecialist.photo.toString())
            itemDoctorSpTxtPrice.text = doctorSpecialist.price?.formatted
            itemDoctorSpTxtDoctorName.text = doctorSpecialist.doctorName
            itemDoctorSpTxtExperience.text = doctorSpecialist.experience
            itemDoctorSpTxtDoctorTitle.text = doctorSpecialist.specialization?.name
            itemDoctorSpTxtAboutDoctor.text = doctorSpecialist.about
            if (doctorSpecialist.hospital?.size != 0) {
                itemDoctorSpImgRsIcon.loadImage(doctorSpecialist.hospital?.get(0)?.icon.toString())
                itemDoctorSpTxtRsName.text = doctorSpecialist.hospital?.get(0)?.name
            }

            if (doctorSpecialist.price?.raw == 0.toLong()) {
                itemDoctorSpImgBadgeFree.isVisible = true
            } else {
                doctorSpecialist.flatPrice?.let {
                    if (it.raw == 0L) {
                        itemDoctorSpTxtStrikePrice.isVisible = false
                        divider.isVisible = false
                    } else {
                        itemDoctorSpTxtStrikePrice.isVisible = true
                        divider.isVisible = true
                        itemDoctorSpTxtStrikePrice.text = doctorSpecialist.price?.formatted
                        itemDoctorSpTxtStrikePrice.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                        itemDoctorSpTxtPrice.text = it.formatted
                    }
                }
            }

            val context = root.context

            if (doctorSpecialist.isAvailable == true) {
                itemDoctorSpImgStatus.setImageDrawable(
                    ContextCompat.getDrawable(context, R.drawable.ic_available_status))
                itemDoctorSpTxtStatus.text = context.getString(R.string.practice)
            } else {
                itemDoctorSpImgStatus.setImageDrawable(
                    ContextCompat.getDrawable(context, R.drawable.ic_not_available_status))
                itemDoctorSpTxtStatus.text = context.getString(R.string.not_practice)
            }
        }
    }
}