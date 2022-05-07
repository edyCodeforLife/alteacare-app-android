package id.altea.care.core.domain.usecase

import id.altea.care.core.domain.model.CheckApp
import id.altea.care.core.domain.repository.DataRepository
import io.reactivex.Single
import javax.inject.Inject

class GetAppVersion @Inject constructor(val dataRepository: DataRepository) {

    fun getAppVersion() : Single<CheckApp>{
        return dataRepository.getVersionApp()
    }
}