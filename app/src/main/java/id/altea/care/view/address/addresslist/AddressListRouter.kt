package id.altea.care.view.address.addresslist

import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import id.altea.care.core.domain.model.UserAddress
import id.altea.care.view.address.addressform.AddressFormActivity
import id.altea.care.view.address.addressform.AddressFormType
import id.altea.care.view.address.addresslist.bottomsheet.AddressListOptionBottomSheet

class AddressListRouter {

    fun openBottomSheetOption(context: AppCompatActivity, idAddress: String, adapterPosition: Int) {
        AddressListOptionBottomSheet.newInstance(idAddress, adapterPosition)
            .show(context.supportFragmentManager, "address_option")
    }

    fun openCreateAddressForm(
        context: AppCompatActivity,
        createOrUpdateLauncher: ActivityResultLauncher<Intent>
    ) {
        createOrUpdateLauncher.launch(
            AddressFormActivity.createIntent(context, AddressFormType.CREATE, null)
        )
    }

    fun openEditAddressForm(
        context: AppCompatActivity,
        createOrUpdateLauncher: ActivityResultLauncher<Intent>,
        userAddress: UserAddress
    ) {
        createOrUpdateLauncher.launch(
            AddressFormActivity.createIntent(
                context,
                AddressFormType.UPDATE,
                userAddress
            )
        )
    }
}
