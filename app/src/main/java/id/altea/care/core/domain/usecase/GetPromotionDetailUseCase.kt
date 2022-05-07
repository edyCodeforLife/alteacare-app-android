package id.altea.care.core.domain.usecase

import id.altea.care.core.domain.model.PromotionDetail
import id.altea.care.core.domain.model.PromotionList
import id.altea.care.core.domain.repository.MarketingRepository
import id.altea.care.core.helper.util.ConstantQueryParam
import io.reactivex.Single
import javax.inject.Inject

class GetPromotionDetailUseCase  @Inject constructor(val marketingRepository: MarketingRepository) {

    fun getPromotionDetail(id : Int) : Single<PromotionDetail> {
        return marketingRepository.getPromotionDetail(id)
    }

}