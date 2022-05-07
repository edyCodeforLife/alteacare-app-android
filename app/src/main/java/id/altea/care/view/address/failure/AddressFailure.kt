package id.altea.care.view.address.failure

import id.altea.care.core.exception.Failure
import id.altea.care.view.address.addressform.bottomsheet.BottomSheetType

sealed class AddressFailure : Failure.FeatureFailure() {

    object SetPrimaryAddressFailure : AddressFailure()

    object DeleteAddressFailure : AddressFailure()

    data class ParameterRequireFailure(val bottomSheetType: BottomSheetType) : AddressFailure()

}
