package id.altea.care.core.domain.usecase

import id.altea.care.core.domain.model.PromotionListGroup
import id.altea.care.core.domain.repository.DataRepository
import id.altea.care.core.domain.repository.MarketingRepository
import io.reactivex.Single
import javax.inject.Inject

class PromotionListGroupUseCase @Inject constructor(val marketingRepository: MarketingRepository) {

    fun getPromotionListGroup(): Single<List<PromotionListGroup>> {
        return marketingRepository.getPromotionListGroup()
    }
}