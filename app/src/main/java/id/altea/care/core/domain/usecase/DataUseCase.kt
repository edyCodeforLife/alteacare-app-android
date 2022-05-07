package id.altea.care.core.domain.usecase

import id.altea.care.core.domain.model.Files
import id.altea.care.core.domain.model.Result
import id.altea.care.core.domain.repository.DataRepository
import io.reactivex.Single
import okhttp3.MultipartBody
import javax.inject.Inject

class DataUseCase @Inject constructor(val dataRepository: DataRepository) {
    fun uploadFiles(photoPart : MultipartBody.Part) : Single<Result<Files>>{
        return dataRepository.uploadFiles(photoPart)
    }
}