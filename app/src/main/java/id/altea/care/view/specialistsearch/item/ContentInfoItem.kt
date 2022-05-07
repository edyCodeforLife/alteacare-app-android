package id.altea.care.view.specialistsearch.item

import android.view.LayoutInflater
import android.view.ViewGroup
import com.mikepenz.fastadapter.binding.AbstractBindingItem
import id.altea.care.R
import id.altea.care.core.ext.setLayoutParamsHeight
import id.altea.care.databinding.ItemContentInfoBinding

class ContentInfoItem(
    private var totalData: String? = null
) : AbstractBindingItem<ItemContentInfoBinding>() {

    override val type: Int get() = hashCode()

    override fun createBinding(
        inflater: LayoutInflater,
        parent: ViewGroup?
    ): ItemContentInfoBinding {
        return ItemContentInfoBinding.inflate(inflater, parent, false)
    }

    fun updateText(totalData: Int?) {
        this.totalData = "${totalData ?: 0}"
    }

    override fun bindView(binding: ItemContentInfoBinding, payloads: List<Any>) {
        binding.run {
            if (totalData.isNullOrBlank()) {
                root.setLayoutParamsHeight(0)
            } else {
                root.setLayoutParamsHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
            }

            txtInfoData.text =
                String.format(root.context.getString(R.string.str_info_specialist), totalData)
        }
    }
}