package id.altea.care.core.domain.usecase

import id.altea.care.core.domain.model.Symptoms
import id.altea.care.core.domain.repository.DataRepository
import io.reactivex.Single
import javax.inject.Inject

class GetSymptomsUseCase @Inject constructor(private val repository: DataRepository) {

    fun getSymptoms(query: String,  page: Int): Single<List<Symptoms>> {
        return repository.geySymptoms(query, page)
    }
}