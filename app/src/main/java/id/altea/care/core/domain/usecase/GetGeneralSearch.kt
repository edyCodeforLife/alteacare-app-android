package id.altea.care.core.domain.usecase

import id.altea.care.core.domain.cache.AuthCache
import id.altea.care.core.domain.model.GeneralSearch
import id.altea.care.core.domain.repository.DataRepository
import id.altea.care.core.helper.util.Constant
import io.reactivex.Single
import javax.inject.Inject

class GetGeneralSearch @Inject constructor(
    private val repository: DataRepository,
    private val authCache: AuthCache
) {

    fun doSearching(queryParam: Map<String, String>): Single<GeneralSearch> {
        return repository.generalSearch(
            queryParam
        )
    }
}
