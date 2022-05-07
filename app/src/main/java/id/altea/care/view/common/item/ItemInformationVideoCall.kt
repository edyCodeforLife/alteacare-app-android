package id.altea.care.view.common.item

import android.text.Html
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.text.parseAsHtml
import com.mikepenz.fastadapter.binding.AbstractBindingItem
import com.twilio.chat.Disposable
import id.altea.care.core.domain.model.InformationVideoCall
import id.altea.care.core.ext.autoSize
import id.altea.care.databinding.ItemInformationVideoCallBinding
import io.reactivex.disposables.CompositeDisposable

class ItemInformationVideoCall(val itemInfromationVideoCall : InformationVideoCall) :
    AbstractBindingItem<ItemInformationVideoCallBinding>(){
    var disposable : CompositeDisposable? = null
    override fun createBinding(
        inflater: LayoutInflater,
        parent: ViewGroup?
    ): ItemInformationVideoCallBinding {
        return ItemInformationVideoCallBinding.inflate(inflater,parent,false)
    }

    override val type: Int
        get()  = hashCode()

    @JvmName("setDisposable1")
    fun setDisposable(disposable: CompositeDisposable){
        this.disposable = disposable
    }

    override fun bindView(binding: ItemInformationVideoCallBinding, payloads: List<Any>) {
        binding.run {
            circleImageView.setImageResource(itemInfromationVideoCall.image)
            itemTextDescriptionInformationVideoCall.autoSize(disposable,14.8F,minLineCount = 3)
            itemTextDescriptionInformationVideoCall.text = itemInfromationVideoCall.description?.parseAsHtml()
        }
    }
}