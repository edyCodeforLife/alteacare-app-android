package id.altea.care.core.domain.usecase

import id.altea.care.core.domain.model.Block
import id.altea.care.core.domain.repository.DataRepository
import io.reactivex.Single
import javax.inject.Inject

class GetBlocksUseCase @Inject constructor(val dataRepository: DataRepository) {

    fun getBlocks() : Single<List<Block>> {

       return dataRepository.getBlocks()
    }
}