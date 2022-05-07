package id.altea.care.core.domain.usecase

import id.altea.care.core.data.request.AddFamilyRequest
import id.altea.care.core.data.request.AddMemberFamilyRegisterAccountRequest
import id.altea.care.core.domain.model.FamilyInfo
import id.altea.care.core.domain.repository.UserRepository
import id.altea.care.core.ext.toNewFormat
import io.reactivex.Single
import javax.inject.Inject

class AddFamilyMemberUseCase @Inject constructor(val userRepository: UserRepository) {

    fun addFamilyMember(params: FamilyInfo): Single<Boolean> {
        return userRepository.addFamilyMember(
            AddFamilyRequest(
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
            )
        )
    }

    fun addMemberFamilyRegisterAccount(params: FamilyInfo): Single<Boolean> {
        return userRepository.addMemberFamilyRegisterAccount(
            AddMemberFamilyRegisterAccountRequest(
                params.family_relation_type,
                params.addressId,
                params.birthCountryId,
                params.birthDate?.toNewFormat(newFormat = "yyyy-MM-dd"),
                params.birthPlace,
                params.cardId,
                params.firstName,
                params.gender?.name,
                params.lastName,
                params.nationalityId,
                params.email,
                params.phone
            )
        )
    }
}