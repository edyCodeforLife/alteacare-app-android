package id.altea.care.core.domain.usecase

import id.altea.care.core.domain.model.InformationCentral
import id.altea.care.core.domain.repository.DataRepository
import io.reactivex.Single
import javax.inject.Inject

class GetInformationCentralUseCase @Inject constructor(val dataRepository: DataRepository){

    fun getInformationCentral() : Single<InformationCentral>{
        return dataRepository.getInformationCentral()
    }
}