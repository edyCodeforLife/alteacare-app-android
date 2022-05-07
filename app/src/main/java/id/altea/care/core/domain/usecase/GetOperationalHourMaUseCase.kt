package id.altea.care.core.domain.usecase

import id.altea.care.core.domain.model.OperationalHourMA
import id.altea.care.core.domain.repository.DataRepository
import io.reactivex.Single
import javax.inject.Inject

class GetOperationalHourMaUseCase @Inject constructor(private val dataRepository: DataRepository) {

    fun getOperationalHourMA() : Single<OperationalHourMA> {
        return dataRepository.getOperationalHourMA()
    }
}