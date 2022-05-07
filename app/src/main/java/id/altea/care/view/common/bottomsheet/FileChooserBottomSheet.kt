package id.altea.care.view.common.bottomsheet

import android.Manifest
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import id.altea.care.R
import id.altea.care.core.base.BaseBottomSheet
import id.altea.care.core.ext.disposedBy
import id.altea.care.core.ext.onSingleClick
import id.altea.care.core.ext.showPermissionSettingDialog
import id.altea.care.core.ext.showRationaleDialog
import id.altea.care.databinding.BottomsheetFileChooserBinding
import permissions.dispatcher.*

@RuntimePermissions
class FileChooserBottomSheet(private val callback: (TypeFileSource) -> Unit) :
    BaseBottomSheet<BottomsheetFileChooserBinding>() {

    override fun getUiBinding(): BottomsheetFileChooserBinding {
        return BottomsheetFileChooserBinding.inflate(cloneLayoutInflater)
    }

    override fun onFirstLaunch(view: View, savedInstanceState: Bundle?) {
        viewBinding?.run {
            fileChooserBtnCamera.onSingleClick()
                .subscribe { onCameraSelectedWithPermissionCheck() }
                .disposedBy(disposable)

            fileChooserBtnDocument.onSingleClick()
                .subscribe { onStorageSelectedWithPermissionCheck(TypeFileSource.STORAGE) }
                .disposedBy(disposable)

            fileChooserBtnGallery.onSingleClick()
                .subscribe { onStorageSelectedWithPermissionCheck(TypeFileSource.GALLERY) }
                .disposedBy(disposable)
        }
    }

    @Suppress("DEPRECATION")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        onRequestPermissionsResult(requestCode, grantResults)

    }

    @NeedsPermission(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    fun onCameraSelected() {
        callback(TypeFileSource.CAMERA)
        dismiss()
    }

    @NeedsPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    fun onStorageSelected(typeFileSource: TypeFileSource) {
        callback(typeFileSource)
        dismiss()
    }

    @OnShowRationale(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    fun showRationaleForCamera(request: PermissionRequest) {
        showRationaleDialog(requireContext(), R.string.permission_camera_deny, request)
    }

    @OnShowRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    fun showRationaleForStorage(request: PermissionRequest) {
        showRationaleDialog(requireContext(), R.string.permission_storage_deny, request)
    }

    @OnNeverAskAgain(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    fun onCameraAndStorageNeverAskAgain() {
        showPermissionSettingDialog(requireActivity(), R.string.permission_camera_deny)
    }

    @OnNeverAskAgain(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    fun onStorageNeverAskAgain() {
        showPermissionSettingDialog(requireActivity(), R.string.permission_storage_deny)
    }

    companion object {
        fun newInstance(callback: (TypeFileSource) -> Unit): FileChooserBottomSheet {
            return FileChooserBottomSheet(callback)
        }
    }
}

enum class TypeFileSource {
    CAMERA, GALLERY, STORAGE,DELETE
}
