package id.altea.care.core.domain.usecase

import id.altea.care.core.domain.model.Widgets
import id.altea.care.core.domain.repository.DataRepository
import io.reactivex.Single
import javax.inject.Inject

class GetWidgetsUseCase @Inject constructor(private val  dataRepository: DataRepository) {

    fun getWidgets() : Single<List<Widgets>> {
        return dataRepository.getWidgets()
    }

}