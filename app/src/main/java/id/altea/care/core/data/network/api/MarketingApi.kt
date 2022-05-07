package id.altea.care.core.data.network.api

import id.altea.care.core.data.response.*
import id.altea.care.core.helper.ProxyRetrofitQueryMap
import id.altea.care.core.helper.SingleLiveEvent
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.QueryMap

interface MarketingApi {

    @GET("marketing/promotion")
    fun getPromotionList(@Query("page") page: Int, @QueryMap queryParam: ProxyRetrofitQueryMap) : Single<MetaBaseResponse<List<PromotionListResponse>>>

    @GET("marketing/promotion/list-group")
    fun getPromotionListGroup() : Single<Response<List<PromotionListGroupResponse>>>

    @GET("marketing/promotion/{id}")
    fun getPromotionDetail(@Path("id") id : Int) : Single<Response<PromotionDetailResponse>>
}