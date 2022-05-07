package id.altea.care.view.myconsultation.conversation

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.IItem
import com.mikepenz.fastadapter.adapters.ItemAdapter
import id.altea.care.core.base.BaseFragmentVM
import id.altea.care.databinding.FragmentHistoryConversationBinding
import id.altea.care.view.myconsultation.conversation.item.EmptyConversationItem

class HistoryConversationFragment :
    BaseFragmentVM<FragmentHistoryConversationBinding, HistoryConversationVM>() {

    private val itemAdapter = ItemAdapter<IItem<*>>()
    private val fastAdapter = FastAdapter.with(itemAdapter)

    override fun getUiBinding(): FragmentHistoryConversationBinding {
        return FragmentHistoryConversationBinding.inflate(layoutInflater)
    }

    override fun onFirstLaunch(savedInstanceState: Bundle?, view: View) {
        initRecyclerView()
        // todo initial dummy content
        itemAdapter.add(EmptyConversationItem())
    }

    private fun initRecyclerView() {
        viewBinding?.run {
            historyConversationRecycler.let {
                it.layoutManager = LinearLayoutManager(context)
                it.adapter = fastAdapter
            }
        }
    }

    override fun onReExecute() {
    }

    override fun initUiListener() {
    }

    override fun initMenu(): Int = 0

    override fun observeViewModel(viewModel: HistoryConversationVM) {

    }

    override fun bindViewModel(): HistoryConversationVM {
        return ViewModelProvider(this, viewModelFactory)[HistoryConversationVM::class.java]
    }
}