package id.altea.care.view.account.changeprofile

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.widget.Toolbar
import androidx.core.content.FileProvider
import com.yalantis.ucrop.UCrop
import com.yalantis.ucrop.UCropActivity
import id.altea.care.R
import id.altea.care.core.base.BaseActivityVM
import id.altea.care.core.domain.model.Files
import id.altea.care.core.domain.model.Profile
import id.altea.care.core.ext.*
import id.altea.care.core.helper.util.Constant
import id.altea.care.databinding.ActivityChangeProfileBinding
import id.altea.care.view.account.ChangePersonalBottomSheet
import id.altea.care.view.common.bottomsheet.TypeFileSource
import kotlinx.android.synthetic.main.activity_change_profile.*
import kotlinx.android.synthetic.main.content_change_photo_profile.*
import kotlinx.android.synthetic.main.toolbar_default_center.view.*
import timber.log.Timber
import java.io.File

class ChangeProfileActivity : BaseActivityVM<ActivityChangeProfileBinding, ChangeProfileVM>() {

    private val viewModel by viewModels<ChangeProfileVM> { viewModelFactory }
    private val TAKE_PICTURE = 1
    private val TAKE_FILE = 2
    private var imagePath: File? = null
    private val router = ChangeProfileRouter()
    private var userId: String? = null

    override fun observeViewModel(viewModel: ChangeProfileVM) {
        observe(viewModel.failureLiveData, ::handleFailure)
        observe(viewModel.isLoadingProfile, ::handleLoadingProfile)
        observe(viewModel.files, ::getFileResponse)
        observe(viewModel.profile, ::getProfileResponse)
        observe(viewModel.resultDelete, ::getDeleteResult)
        observe(viewModel.resultUpdate, ::getUpdateResult)
    }

    private fun handleLoadingProfile(showLoading: Boolean?) {
        if (showLoading == true) showProgressProfile() else hideProgressProfile()
    }

    override fun bindViewModel(): ChangeProfileVM = viewModel

    override fun enableBackButton(): Boolean = true

    override fun bindToolbar(): Toolbar? = viewBinding?.appbar?.toolbar

    override fun getUiBinding(): ActivityChangeProfileBinding =
        ActivityChangeProfileBinding.inflate(layoutInflater)

    override fun onFirstLaunch(savedInstanceState: Bundle?) {
        viewModel.getProfile()
        viewBinding.run {
            appbar.txtToolbarTitle.text = getString(R.string.str_change_profile)
        }
    }

    override fun initUiListener() {
        viewBinding?.run {
            accountImgChangeProfile.onSingleClick().subscribe {
                openFileChooser()
            }.disposedBy(disposable)

            changePersonalText.onSingleClick().subscribe {
                router.openChangePersonalBottomSheet(this@ChangeProfileActivity?.supportFragmentManager,
                    submitCallback = {
                        router.openContactActivity(this@ChangeProfileActivity)
                    },
                    dismissCallback = {},ChangePersonalBottomSheet.State.GENERAL)
            }.disposedBy(disposable)

            btnEmailChangeProfil.onSingleClick().subscribe {
                router.openChangeEmailActivity(this@ChangeProfileActivity,textEmailChangeProfile.text.toString())
            }.disposedBy(disposable)

            btnPhoneChangeProfil.onSingleClick().subscribe {
                router.openChangePhoneNumberActivity(this@ChangeProfileActivity,textNomorChangeProfile.text.toString())
            }.disposedBy(disposable)

            btnAddressChangeProfil.onSingleClick()
                .subscribe { router.openAddressListActivity(this@ChangeProfileActivity) }
                .disposedBy(disposable)
        }
    }

    private fun openFileChooser() {
        router.openImageChooser(this) { typeFile ->
            when (typeFile) {
                TypeFileSource.CAMERA -> openCamera()
                TypeFileSource.GALLERY -> openGallery()
                TypeFileSource.DELETE -> deleteStorage()
                TypeFileSource.STORAGE -> {}
            }
        }
    }

    private fun deleteStorage() {
        showDialogDeletePhotoProfile(this, onRemoveClicked = {
            viewModel.deleteAvatar()
        })
    }

    private fun openCamera() {
        imagePath = this.createDefaultImagePath()
        val providerFile =
            FileProvider.getUriForFile(this, Constant.FILE_PROVIDER, imagePath!!)
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, providerFile)
        startActivityForResult(intent, TAKE_PICTURE)
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.setType("image/*")
        startActivityForResult(intent, TAKE_FILE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        viewBinding.run {
            if (resultCode == Activity.RESULT_OK) {
                if (requestCode == TAKE_FILE) {
                    data?.data?.let {
                        imagePath = getRealPathURI(it)
                    }
                }
                if (requestCode == UCrop.REQUEST_CROP) {
                    data?.let {
                        handleCrop(it)
                    }
                } else {
                    imagePath?.absolutePath?.run {
                        onCrop(Uri.fromFile(File(this)))
                    }
                }
            }
        }
    }

    private fun handleCrop(data: Intent) {
        UCrop.getOutput(data)?.let {
            onSuccess(it)
        }
    }

    private fun onSuccess(uri: Uri) {
        viewModel.uploadFiles(uri)
    }

    private fun onCrop(sourceUri: Uri) {
        val crop = UCrop.of(sourceUri, sourceUri)
        val option = UCrop.Options()
        option.run {
            setAllowedGestures(UCropActivity.ALL, UCropActivity.ALL, UCropActivity.ALL)
            withAspectRatio(3f, 4f)
            setShowCropFrame(false)
            setShowCropGrid(false)
            setCompressionQuality(100)
        }
        crop.withOptions(option).start(this@ChangeProfileActivity)
    }

    private fun getFileResponse(files: Files?) {
        viewBinding?.run {
            files?.let { files ->
                if (files.url != null) {
                    accountImgChangeProfile.loadImage(
                        files.url,
                        R.drawable.ic_change_photo_profile,
                        R.drawable.ic_logo_loading
                    )
                }
                viewModel.updateProfile(files.id)
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun getProfileResponse(profile: Profile?) {
        viewBinding?.run {
            profile?.let { profile ->
                userId = profile.id
                patientDataChangeProfile.let { patientDataChangeProfileBinding ->

                    profile.userDetails?.avatar?.let {
                        accountImgChangeProfile.loadImage(
                            profile.userDetails.avatar.formats?.small.orEmpty(),
                            R.drawable.ic_change_photo_profile
                        )
                    }

                    textNomorChangeProfile.text = profile.phone
                    textEmailChangeProfile.text = profile.email
                    val address = profile.userAddresses?.firstOrNull { it.type == "PRIMARY" }
                    textAddressChangeProfile.text =
                        address.getCompleteAddress(this@ChangeProfileActivity)

                    // personal data
                    val defaultPatientData = profile.defaultPatientData
                    defaultPatientData?.let { patientData ->
                        val ageMonth = patientData.age?.month ?: 0
                        val ageYear = patientData.age?.year ?: 0

                        patientDataChangeProfileBinding.patientTxtName.text =
                            "${patientData.firstName} ${patientData.lastName}"

                        patientDataChangeProfileBinding.patientTxtAge.text =
                            String.format(getString(R.string.s_age), ageYear, ageMonth)

                        patientDataChangeProfileBinding.patientTxtBirthDate.text =
                            patientData.birthDate
                                .toString()
                                .toNewFormat("yyyy-MM-dd", "dd/MM/yyyy")

                        patientDataChangeProfileBinding.patientTxtGender.text =
                            patientData.gender.toString()

                        patientDataChangeProfileBinding.patientTxtIdentity.text =
                            patientData.cardId.toString()

                        patientDataChangeProfileBinding.patientTxtPhone.text = patientData.phone
                        patientDataChangeProfileBinding.patientTxtEmail.text = patientData.email

                        patientData.userAddresses?.let { userAddress ->
                            patientDataChangeProfileBinding.patientTxtAddress.text =
                                if (userAddress.isNotEmpty()) userAddress.first()?.completeAddress() else "-"
                        }
                    }
                }
            }
        }
    }

    private fun getDeleteResult(result: Boolean?) {
        viewBinding?.run {
            result?.let { deleteResult ->
                if (deleteResult) {
                    accountImgChangeProfile.setImageResource(R.drawable.ic_change_photo_profile)
                    showSuccessSnackbar(getString(R.string.str_success_delete))
                }
            }
        }
    }

    private fun getUpdateResult(result: Boolean?) {
        if (result == true) {
            showSuccessSnackbar(getString(R.string.str_success_update_profile))
        }
    }

    private fun hideProgressProfile() = viewBinding.run {
        accountProgressBar.visibility = View.INVISIBLE
    }

    private fun showProgressProfile() = viewBinding.run {
        accountProgressBar.visibility = View.VISIBLE
    }
}
