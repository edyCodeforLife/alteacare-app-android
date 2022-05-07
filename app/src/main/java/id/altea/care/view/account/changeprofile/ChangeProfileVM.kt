package id.altea.care.view.account.changeprofile

import android.net.Uri
import id.altea.care.core.base.BaseViewModel
import id.altea.care.core.data.request.UpdateAvatarRequest
import id.altea.care.core.exception.Failure
import id.altea.care.core.domain.model.Files
import id.altea.care.core.domain.model.Profile
import id.altea.care.core.domain.usecase.*
import id.altea.care.core.ext.TagInjection
import id.altea.care.core.ext.disposedBy
import id.altea.care.core.ext.getGeneralErrorServer
import id.altea.care.core.helper.NetworkHandler
import id.altea.care.core.helper.SingleLiveEvent
import io.reactivex.Scheduler
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import javax.inject.Inject
import javax.inject.Named

class ChangeProfileVM @Inject constructor(
    @Named(TagInjection.UI_Scheduler) uiSchedulers: Scheduler,
    @Named(TagInjection.IO_Scheduler) ioScheduler: Scheduler,
    networkHandler: NetworkHandler,
    private val dataUseCase: DataUseCase,
    private val profileUseCase: GetProfileUseCase,
    private val updateAvatarUseCase: GetUpdateAvatarUseCase,
    private val deleteAvatarUseCase: DeleteAvatarUseCase,
    private val authUseCase: GetAuthUseCase
) : BaseViewModel(uiSchedulers, ioScheduler, networkHandler) {
    val files = SingleLiveEvent<Files>()
    val profile = SingleLiveEvent<Profile>()
    val isLoadingProfile = SingleLiveEvent<Boolean>()
    val resultDelete = SingleLiveEvent<Boolean>()
    val resultUpdate = SingleLiveEvent<Boolean>()
    fun uploadFiles(imgUri: Uri) {
        executeJob {
            val file = File(imgUri.path.orEmpty())
            val mediaTypeImg = "image/jpeg".toMediaTypeOrNull()
            val requestFile: RequestBody = file.asRequestBody(mediaTypeImg)

            val body: MultipartBody.Part =
                MultipartBody.Part.createFormData("file", file.name, requestFile)
            isLoadingProfile.value = true
            dataUseCase.uploadFiles(body)
                .subscribeOn(ioScheduler)
                .observeOn(uiSchedulers)
                .subscribe({ resultFiles ->
                    if (resultFiles.status == true) {
                        files.value = resultFiles.data ?: null
                        isLoadingProfile.value = false
                    }
                }, { throwable ->
                    isLoadingProfile.value = false
                    handleFailure(Failure.ServerError(throwable.message ?: ""))
                }).disposedBy(disposable)
        }
    }

    fun updateProfile(avatar: String?) {
        updateAvatarUseCase
            .updateAvatar(UpdateAvatarRequest(avatar))
            .compose(applySchedulers())
            .doOnSubscribe { isLoadingLiveData.value = true }
            .doOnSuccess { isLoadingLiveData.value = false }
            .subscribe({
                resultUpdate.value = it.status ?: false
            }, { throwable ->
                handleFailure(throwable.getGeneralErrorServer())
            }).disposedBy(disposable)
    }

    fun deleteAvatar() {
        executeJob {
            deleteAvatarUseCase.deleteAvatar()
                .compose(applySchedulers())
                .doOnSubscribe { isLoadingLiveData.value = true }
                .doOnSuccess { isLoadingLiveData.value = false }
                .subscribe({
                    resultDelete.value = it
                }, { throwable ->
                    handleFailure(throwable.getGeneralErrorServer())
                }).disposedBy(disposable)
        }
    }

    fun getProfile() {
        executeJob {
            isLoadingLiveData.value = true
            profileUseCase.getProfile()
                .compose(applySchedulers())
                .doOnSubscribe { isLoadingLiveData.value = true }
                .doOnSuccess { isLoadingLiveData.value = false }
                .subscribe({ resultProfile ->
                    if (resultProfile.status == true) {
                        profile.value = resultProfile.data ?: null
                        isLoadingLiveData.value = false
                    }
                }, { throwable ->
                    handleFailure(Failure.ServerError(throwable.message ?: ""))
                }).disposedBy(disposable)
        }
    }
}