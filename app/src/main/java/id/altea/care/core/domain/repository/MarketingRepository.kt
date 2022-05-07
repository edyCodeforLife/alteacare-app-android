package id.altea.care.core.domain.repository

import id.altea.care.core.domain.model.PromotionDetail
import id.altea.care.core.domain.model.PromotionList
import id.altea.care.core.domain.model.PromotionListGroup
import io.reactivex.Single

interface MarketingRepository {
    fun getPromotionList(page : Int,queryParam: Map<String, Any>) : Single<List<PromotionList>>

    fun getPromotionListGroup() : Single<List<PromotionListGroup>>

    fun getPromotionDetail(id : Int) : Single<PromotionDetail>
}