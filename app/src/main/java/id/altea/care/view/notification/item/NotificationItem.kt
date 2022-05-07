package id.altea.care.view.notification.item

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.mikepenz.fastadapter.binding.AbstractBindingItem
import id.altea.care.R
import id.altea.care.core.ext.loadImage
import id.altea.care.databinding.ItemNotificationBinding

class NotificationItem(val notifDummy: NotificationDummy) :
    AbstractBindingItem<ItemNotificationBinding>() {
    override val type: Int get() = hashCode()

    override fun createBinding(
        inflater: LayoutInflater,
        parent: ViewGroup?
    ): ItemNotificationBinding {
        return ItemNotificationBinding.inflate(inflater, parent, false)
    }

    override fun bindView(binding: ItemNotificationBinding, payloads: List<Any>) {
        binding.run {
            itemNotifImgIcon.loadImage(notifDummy.imagePath)
            itemNotifTxtContent.text = notifDummy.content
            itemNotifTxtTitle.text = notifDummy.title
            itemNotifTxtDate.text = notifDummy.date
            binding.root.also {
                it.setBackgroundColor(
                    ContextCompat.getColor(
                        it.context,
                        if (notifDummy.isRead) android.R.color.transparent else R.color.subtle
                    )
                )
            }
        }
    }
}
