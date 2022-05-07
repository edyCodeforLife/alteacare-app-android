package id.altea.care.core.domain.usecase

import id.altea.care.core.data.request.AddFamilyRequest
import id.altea.care.core.domain.model.FamilyInfo
import id.altea.care.core.domain.repository.UserRepository
import id.altea.care.core.ext.toNewFormat
import io.reactivex.Single
import javax.inject.Inject

class UpdateFamilyUseCase @Inject constructor(val userRepository: UserRepository) {

    fun updateFamilyMember(params : FamilyInfo, patientId : String) : Single<Boolean>{
        return userRepository.updateFamilyMember(AddFamilyRequest(
                params.family_relation_type,
                params.addressId,
                params.birthCountryId,
                params.birthDate?.toNewFormat(newFormat = "yyyy-MM-dd"),
                params.birthPlace,
                params.cardId,
                params.firstName,
                params.gender?.name,
                params.lastName,
                params.nationalityId
        ),patientId)
    }

}