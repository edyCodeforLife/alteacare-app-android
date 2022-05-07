package id.altea.care.core.domain.usecase

import id.altea.care.core.domain.model.Block
import id.altea.care.core.domain.repository.DataRepository
import id.altea.care.core.helper.util.ConstantQueryParam
import id.altea.care.core.helper.util.ConstantQueryValue
import io.reactivex.Single
import javax.inject.Inject

class GetTermRefundUseCase @Inject constructor(private val dataRepository: DataRepository) {

    fun getTermAndRefund(): Single<List<Block>> {
        return dataRepository.getBlocks(
            mapOf(
                Pair(
                    ConstantQueryParam.QUERY_TYPE, ConstantQueryValue.BLOCK_TYPE_TERM_REFUND
                )
            )
        )
    }

}
