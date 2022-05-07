package id.altea.care.view.home.bottomsheet

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.adapters.GenericItemAdapter
import id.altea.care.core.base.BaseBottomSheet
import id.altea.care.core.domain.model.Account
import id.altea.care.databinding.BottomsheetSwitchAccountBinding
import id.altea.care.view.home.item.AccountItem
import id.altea.care.view.home.item.ButtonAddAccountItem
import java.util.*

class SwitchAccountBottomSheet(
    val submitCallback: (Account) -> Unit,
    val addAccountCallback: () -> Unit
) : BaseBottomSheet<BottomsheetSwitchAccountBinding>() {

    private val itemAdapter = GenericItemAdapter()
    private val fastAdapter = FastAdapter.with(itemAdapter)
    private val accountList by lazy {
        arguments?.getParcelableArrayList<Account>(EXTRA_LIST_DATA) ?: emptyList()
    }

    override fun getUiBinding(): BottomsheetSwitchAccountBinding {
        return BottomsheetSwitchAccountBinding.inflate(cloneLayoutInflater)
    }

    override fun onFirstLaunch(view: View, savedInstanceState: Bundle?) {
        viewBinding?.run {
            btmSwitchAccountRecyclerView.let {
                it.layoutManager = LinearLayoutManager(requireContext())
                it.adapter = fastAdapter
            }
        }

        accountList.map { itemAdapter.add(AccountItem(it)) }
        itemAdapter.add(ButtonAddAccountItem { addAccountCallback.invoke() })

        fastAdapter.onClickListener = { _, _, item, _ ->
            if (item is AccountItem) {
                submitCallback.invoke(item.account)
                dismiss()
            }
            false
        }
    }

    companion object {
        private const val EXTRA_LIST_DATA = "SwitchAccountBtm.listData"
        fun newInstance(
            listData: ArrayList<Account>,
            submitCallback: (Account) -> Unit,
            addAccountCallback: () -> Unit
        ): SwitchAccountBottomSheet {
            return SwitchAccountBottomSheet(submitCallback, addAccountCallback).apply {
                val bundle = Bundle().apply {
                    putParcelableArrayList(EXTRA_LIST_DATA, listData)
                }
                arguments = bundle
            }
        }
    }
}
