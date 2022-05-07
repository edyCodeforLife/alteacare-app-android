package id.altea.care.view.onboarding.item

import android.view.LayoutInflater
import android.view.ViewGroup
import com.mikepenz.fastadapter.binding.AbstractBindingItem
import id.altea.care.databinding.ItemOnboardingBinding

class ItemOnBoarding(val image : Int) : AbstractBindingItem<ItemOnboardingBinding>() {
    override fun createBinding(
        inflater: LayoutInflater,
        parent: ViewGroup?
    ): ItemOnboardingBinding {
        return ItemOnboardingBinding.inflate(inflater,parent,false)
    }

    override val type: Int
        get() = hashCode()


    override fun bindView(binding: ItemOnboardingBinding, payloads: List<Any>) {
        binding.run {
            itemOnboardingImg.setImageResource(image)
        }
    }
}