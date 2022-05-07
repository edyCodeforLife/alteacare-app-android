package id.altea.care.core.data.repositoryimpl

import id.altea.care.core.data.network.api.MarketingApi
import id.altea.care.core.domain.model.PromotionDetail
import id.altea.care.core.domain.model.PromotionList
import id.altea.care.core.domain.model.PromotionListGroup
import id.altea.care.core.domain.repository.MarketingRepository
import id.altea.care.core.helper.ProxyRetrofitQueryMap
import id.altea.care.core.mappers.Mapper.mapToPromotionList
import id.altea.care.core.mappers.Mapper.mapToPromotionListGroup
import io.reactivex.Single

class MarketingRespositoryImpl(val marketingApi: MarketingApi) : MarketingRepository {

    override fun getPromotionList(page : Int,queryParam: Map<String, Any>): Single<List<PromotionList>> {
        return marketingApi.getPromotionList(page, ProxyRetrofitQueryMap(queryParam))
            .map { response ->
                response.data.map { promotionListResponse ->
                    promotionListResponse.mapToPromotionList()
                }
            }
    }

    override fun getPromotionListGroup(): Single<List<PromotionListGroup>> {
        return marketingApi.getPromotionListGroup()
            .map { response ->
                response.data.map { promotionLisGroup ->
                    promotionLisGroup.mapToPromotionListGroup()
                }
            }
    }

    override fun getPromotionDetail(id: Int): Single<PromotionDetail> {
        return  marketingApi.getPromotionDetail(id).map { it.data.toPromotionDetail() }
    }
}