package id.altea.care.view.videocall.chat.item

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.mikepenz.fastadapter.binding.AbstractBindingItem
import com.twilio.chat.CallbackListener
import com.twilio.chat.Message
import id.altea.care.core.ext.loadImage
import id.altea.care.databinding.ItemMessageRightBinding
import id.altea.care.view.common.enums.StatusChat
import timber.log.Timber
import java.io.File

class ChatRightItem(
    val message: Message? = null,
    val mimeType: String = "image",
    val file: File? = null,
    val messageBody: String? = null,
    var status: StatusChat = StatusChat.LOADING
) : AbstractBindingItem<ItemMessageRightBinding>() {

    override val type: Int
        get() = hashCode()

    override fun createBinding(
        inflater: LayoutInflater,
        parent: ViewGroup?
    ): ItemMessageRightBinding {
        return ItemMessageRightBinding.inflate(inflater, parent, false)
    }

    override fun bindView(binding: ItemMessageRightBinding, payloads: List<Any>) {
        binding.run {
            if (message != null) {
                if (message.hasMedia()) {
                    message.media.getContentTemporaryUrl(object : CallbackListener<String>() {
                        override fun onSuccess(mediaContentUrl: String?) {
                            if (message.media.type.contains("image")) {
                                itemChatImgRight.visibility = View.VISIBLE
                                Timber.e("UEL IMAGE : $mediaContentUrl")
                                mediaContentUrl?.let {
                                    itemChatImgRight.loadImage(mediaContentUrl)
                                }
                            } else {
                                itemChatRightPdfMessage.visibility = View.VISIBLE
                                itemChatTextRight.text = message.media.fileName
                            }
                        }
                    })
                } else {
                    itemChatRightMessage.visibility = View.VISIBLE
                    itemChatRightMessage.text = message.messageBody
                }
            } else {
                when {
                    mimeType.contains("image") -> {
                        itemChatImgRight.visibility = View.VISIBLE
                        Glide.with(itemChatImgRight)
                            .load(file)
                            .into(itemChatImgRight)
                    }
                    mimeType.contains("text") -> {
                        itemChatRightMessage.run {
                            visibility = View.VISIBLE
                            text = messageBody
                        }
                    }
                    else -> {
                        itemChatRightPdfMessage.visibility = View.VISIBLE
                        itemChatTextRight.text = file?.name
                    }
                }
            }

            when (status) {
                StatusChat.LOADING -> {
                    binding.itemProgressBar.isVisible = true
                    binding.itemErrorStatus.isVisible = false
                }
                StatusChat.ERROR -> {
                    binding.itemProgressBar.isVisible = false
                    binding.itemErrorStatus.isVisible = true
                }
                StatusChat.SUCCESS -> {
                    binding.itemProgressBar.isVisible = false
                    binding.itemErrorStatus.isVisible = false
                }
            }
        }
    }

}
