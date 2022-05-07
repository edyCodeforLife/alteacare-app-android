package id.altea.care.view.videocall.chat.item

import android.view.LayoutInflater
import android.view.ViewGroup
import com.mikepenz.fastadapter.binding.AbstractBindingItem
import id.altea.care.databinding.ItemLoadingChatInitialBinding

class ChatLoadingInitialItem : AbstractBindingItem<ItemLoadingChatInitialBinding>() {

    override val type: Int get() = hashCode()

    override fun createBinding(
        inflater: LayoutInflater,
        parent: ViewGroup?
    ): ItemLoadingChatInitialBinding {
        return ItemLoadingChatInitialBinding.inflate(inflater, parent, false)
    }
}
