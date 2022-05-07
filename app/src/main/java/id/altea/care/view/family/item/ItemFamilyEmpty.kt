package id.altea.care.view.family.item

import android.view.LayoutInflater
import android.view.ViewGroup
import com.mikepenz.fastadapter.binding.AbstractBindingItem
import id.altea.care.databinding.ContentEmptyFamilyBinding
import id.altea.care.databinding.ItemAddressEmptyBinding
import id.altea.care.databinding.ItemListFamilyBinding

class ItemFamilyEmpty : AbstractBindingItem<ContentEmptyFamilyBinding>() {

    override val type: Int
        get() = hashCode()

    override fun createBinding(
            inflater: LayoutInflater,
            parent: ViewGroup?
    ): ContentEmptyFamilyBinding {
        return ContentEmptyFamilyBinding.inflate(inflater, parent, false)
    }

}