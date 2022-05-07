package id.altea.care.view.home.item

import android.view.LayoutInflater
import android.view.ViewGroup
import com.mikepenz.fastadapter.binding.AbstractBindingItem
import id.altea.care.R
import id.altea.care.core.domain.model.Account
import id.altea.care.core.ext.loadImage
import id.altea.care.databinding.ItemAccountBinding

class AccountItem(val account: Account) : AbstractBindingItem<ItemAccountBinding>() {
    override val type: Int
        get() = hashCode()

    override fun createBinding(inflater: LayoutInflater, parent: ViewGroup?): ItemAccountBinding {
        return ItemAccountBinding.inflate(inflater, parent, false)
    }

    override fun bindView(binding: ItemAccountBinding, payloads: List<Any>) {
        binding.run {
            itemAccountImg.loadImage(account.photo, R.drawable.ic_change_photo_profile)
            itemAccountTextName.text = account.name
            itemAccountTextEmail.text = account.email
        }
    }
}
