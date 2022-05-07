package id.altea.care.view.address.addressform

import androidx.fragment.app.FragmentManager
import id.altea.care.core.domain.model.SelectedModel
import id.altea.care.view.address.addressform.bottomsheet.AddressFormBottomSheet
import id.altea.care.view.address.addressform.bottomsheet.BottomSheetType

class AddressFormRouter {

    fun openBottomSheet(
        supportFragment: FragmentManager,
        bottomSheetType: BottomSheetType,
        data: SelectedModel?,
        listData: List<SelectedModel>,
        submitCallback: (Pair<SelectedModel, BottomSheetType>) -> Unit,
        dismissCallback: (() -> Unit) = {}
    ) {
        AddressFormBottomSheet.newInstance(
            bottomSheetType,
            data,
            ArrayList(listData),
            submitCallback,
            dismissCallback
        )
            .show(supportFragment, "form_bottom")
    }

}
