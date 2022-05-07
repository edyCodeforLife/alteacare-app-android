package id.altea.care.core.domain.usecase

import id.altea.care.core.domain.model.Banner
import id.altea.care.core.domain.repository.DataRepository
import io.reactivex.Single
import javax.inject.Inject

class GetBannerUseCase @Inject constructor(val dataRepository: DataRepository) {

    fun getBanner(category : String) : Single<List<Banner>> {
        return dataRepository.getBanner(category = category)
    }

}