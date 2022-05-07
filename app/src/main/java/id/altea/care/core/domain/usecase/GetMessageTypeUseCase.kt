package id.altea.care.core.domain.usecase

import id.altea.care.core.domain.model.TypeMessage
import id.altea.care.core.domain.repository.DataRepository
import io.reactivex.Single
import javax.inject.Inject

class GetMessageTypeUseCase @Inject constructor(val dataRepository: DataRepository) {

    fun getMessageType() : Single<List<TypeMessage>>{
        return dataRepository.getMessageType()
    }
}