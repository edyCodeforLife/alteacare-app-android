package id.altea.care.view.generalsearch.item

import android.view.LayoutInflater
import android.view.ViewGroup
import com.mikepenz.fastadapter.binding.AbstractBindingItem
import id.altea.care.R
import id.altea.care.core.ext.loadImage
import id.altea.care.databinding.ItemSpecialistBinding
import id.altea.care.view.specialist.model.SpecialistItemModel

class SpecialistCategoryItem(
    val data: SpecialistItemModel
) : AbstractBindingItem<ItemSpecialistBinding>(){

    override val type: Int get() = hashCode()

    override fun createBinding(
        inflater: LayoutInflater,
        parent: ViewGroup?
    ): ItemSpecialistBinding {
        return ItemSpecialistBinding.inflate(inflater,parent,false)
    }

    override fun bindView(binding: ItemSpecialistBinding, payloads: List<Any>) {
        binding.run {
            itemSpecialistTxtName.text = data.name
            itemSpecialistImgIcon.loadImage(
                data.icon.orEmpty(),
                placeholder = R.drawable.ic_logo_loading
            )
        }
    }
}