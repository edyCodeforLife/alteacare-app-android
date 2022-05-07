package id.altea.care.core.domain.usecase

import id.altea.care.core.domain.model.FamilyContact
import id.altea.care.core.domain.repository.DataRepository
import io.reactivex.Single
import javax.inject.Inject

class GetContactFamilyUseCase @Inject constructor(val dataRepository: DataRepository) {

    fun getFamilyCotact() : Single<List<FamilyContact>>{
        return dataRepository.getContactFamily()
    }
}