package id.altea.care.core.domain.usecase

import id.altea.care.core.data.request.MessageRequest
import id.altea.care.core.domain.repository.DataRepository
import io.reactivex.Single
import javax.inject.Inject

class SendMessageUseCase @Inject constructor(val dataRepository: DataRepository) {

    fun sendMessage(messageRequest: MessageRequest) : Single<Boolean>{
        return dataRepository.sendMessage(messageRequest)
    }
}