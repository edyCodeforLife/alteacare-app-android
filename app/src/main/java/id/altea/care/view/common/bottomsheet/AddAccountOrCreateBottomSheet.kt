package id.altea.care.view.common.bottomsheet

import android.os.Bundle
import android.view.View
import id.altea.care.core.base.BaseBottomSheet
import id.altea.care.core.ext.disposedBy
import id.altea.care.core.ext.onSingleClick
import id.altea.care.databinding.BottomsheetAddOrCreateAccountBinding

class AddAccountOrCreateBottomSheet(
    private val onAddAccountCallback: () -> Unit,
    private val onCreateAccountCallback: () -> Unit
) : BaseBottomSheet<BottomsheetAddOrCreateAccountBinding>() {
    override fun getUiBinding(): BottomsheetAddOrCreateAccountBinding {
        return BottomsheetAddOrCreateAccountBinding.inflate(cloneLayoutInflater)
    }

    override fun onFirstLaunch(view: View, savedInstanceState: Bundle?) {
        viewBinding?.run {
            btmSheetAccountBtnAdd.onSingleClick()
                .doAfterNext { dismiss() }
                .subscribe { onAddAccountCallback.invoke() }
                .disposedBy(disposable)

            btmSheetAccountBtnCreate.onSingleClick()
                .doAfterNext { dismiss() }
                .subscribe { onCreateAccountCallback.invoke() }
                .disposedBy(disposable)
        }
    }

    companion object {
        fun newInstance(
            addAccountCallback: () -> Unit,
            createAccountCallback: () -> Unit
        ): AddAccountOrCreateBottomSheet {
            return AddAccountOrCreateBottomSheet(addAccountCallback, createAccountCallback)
        }
    }
}
