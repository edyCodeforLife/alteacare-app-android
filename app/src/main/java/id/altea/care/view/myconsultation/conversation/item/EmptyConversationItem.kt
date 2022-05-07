package id.altea.care.view.myconsultation.conversation.item

import android.view.LayoutInflater
import android.view.ViewGroup
import com.mikepenz.fastadapter.binding.AbstractBindingItem
import id.altea.care.databinding.ItemConversationEmptyBinding

class EmptyConversationItem : AbstractBindingItem<ItemConversationEmptyBinding>() {

    override val type: Int
        get() = hashCode()

    override fun createBinding(
        inflater: LayoutInflater,
        parent: ViewGroup?
    ): ItemConversationEmptyBinding {
        return ItemConversationEmptyBinding.inflate(inflater, parent, false)
    }
}
