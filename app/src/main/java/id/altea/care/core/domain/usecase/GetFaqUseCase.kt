package id.altea.care.core.domain.usecase


import id.altea.care.core.domain.model.Faq
import id.altea.care.core.domain.repository.DataRepository
import io.reactivex.Single
import javax.inject.Inject

class GetFaqUseCase @Inject constructor(val dataRepository: DataRepository) {

    fun getFaq() : Single<List<Faq>>{
        return dataRepository.getFaq()
    }

}