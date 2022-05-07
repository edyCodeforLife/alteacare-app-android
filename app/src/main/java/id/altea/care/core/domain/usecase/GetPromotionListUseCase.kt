package id.altea.care.core.domain.usecase

import id.altea.care.core.domain.model.PromotionList
import id.altea.care.core.domain.repository.DataRepository
import id.altea.care.core.domain.repository.MarketingRepository
import id.altea.care.core.helper.util.ConstantQueryParam
import io.reactivex.Single
import org.slf4j.MDC.put
import javax.inject.Inject

class GetPromotionListUseCase @Inject constructor(val marketingRepository: MarketingRepository) {

    fun getPromotionList(page : Int,queryParam: HashMap<String, Any>) : Single<List<PromotionList>> {
        return marketingRepository.getPromotionList(
            page,
            queryParam.apply {
            put(ConstantQueryParam.QUERY_ORDER_BY, "weight")
            put(ConstantQueryParam.QUERY_ORDER_TYPE,"DESC")
        })
    }
}