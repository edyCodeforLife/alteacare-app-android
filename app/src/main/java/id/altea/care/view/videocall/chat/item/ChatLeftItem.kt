package id.altea.care.view.videocall.chat.item

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.mikepenz.fastadapter.binding.AbstractBindingItem
import com.twilio.chat.CallbackListener
import com.twilio.chat.Message
import id.altea.care.core.ext.loadImage
import id.altea.care.databinding.ItemMessageLeftBinding
import id.altea.care.view.common.enums.StatusChat

class ChatLeftItem(val message: Message,var status : StatusChat) : AbstractBindingItem<ItemMessageLeftBinding>() {
    var value_progress = 0

    override val type: Int
        get() = hashCode()

    override fun createBinding(
        inflater: LayoutInflater,
        parent: ViewGroup?
    ): ItemMessageLeftBinding {
        return ItemMessageLeftBinding.inflate(inflater, parent, false)
    }


    override fun bindView(binding: ItemMessageLeftBinding, payloads: List<Any>) {
        binding.run {
            if (message.hasMedia()) {
                message.media.getContentTemporaryUrl(object : CallbackListener<String>() {
                    override fun onSuccess(mediaContentUrl: String?) {
                        if (message.media.type.contains("image")) {
                            itemImgLeftMessage.visibility = View.VISIBLE
                            mediaContentUrl?.let {
                                itemImgLeftMessage.loadImage(mediaContentUrl)
                            }
                        } else {
                            itemChatLeftPdfMessage.visibility = View.VISIBLE
                            itemChatProgressLeft.setProgress(value_progress)
                            itemTextPdfLeftMessage.text = message.media.fileName
                        }
                    }
                })
            } else {
                itemTextLeftMessage.visibility = View.VISIBLE
                itemTextLeftMessage.text = message.messageBody
            }
        }

        when (status) {
            StatusChat.LOADING -> {
                binding.itemLeftProgressBar.isVisible = true
                binding.itemLeftErrorStatus.isVisible = false
            }
            StatusChat.ERROR -> {
                binding.itemLeftProgressBar.isVisible = false
                binding.itemLeftErrorStatus.isVisible = true
            }
            StatusChat.SUCCESS -> {
                binding.itemLeftProgressBar.isVisible = false
                binding.itemLeftErrorStatus.isVisible = false
            }
        }
    }


}


