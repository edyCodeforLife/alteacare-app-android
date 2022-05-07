package id.altea.care.view.family.item

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.mikepenz.fastadapter.binding.AbstractBindingItem
import id.altea.care.R
import id.altea.care.core.domain.model.DetailPatient
import id.altea.care.core.ext.loadImage
import id.altea.care.core.ext.toNewFormat
import id.altea.care.databinding.ContentDetailFamilyBinding
import id.altea.care.databinding.ItemListFamilyBinding

class ItemDetailFamily(val context : Context,val detailPatient : DetailPatient?) : AbstractBindingItem<ContentDetailFamilyBinding>() {
    override val type: Int
        get() = hashCode()

    override fun createBinding(inflater: LayoutInflater, parent: ViewGroup?): ContentDetailFamilyBinding {
        return ContentDetailFamilyBinding.inflate(inflater, parent, false)
    }

    override fun bindView(binding: ContentDetailFamilyBinding, payloads: List<Any>) {
        binding.run {
            val ageYear = detailPatient?.age?.year ?: 0
            val ageMonth = detailPatient?.age?.month ?: 0
            binding.patientTxtName.text = "${detailPatient?.firstName} ${detailPatient?.lastName}"
            binding.patientTxtAge.text =  String.format(context.getString(R.string.s_age), ageYear, ageMonth)
            binding.patientTxtBirthDate.text = detailPatient?.birthDate.toString()
                    .toNewFormat("yyyy-MM-dd", "dd/MM/yyyy")
            binding.patientTxtGender.text = detailPatient?.gender
            binding.patientTxtIdentity.text = detailPatient?.cardId
            binding.patientTxtAddress.text = detailPatient?.completeAddress() ?: "-"
        }
    }
}
