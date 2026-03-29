package com.kblack.offlinemap.domain.usecase.mapdownload

import com.kblack.offlinemap.domain.models.MapModel
import com.kblack.offlinemap.domain.repository.MapDownloadRepository

class GetGraphPathUseCase(
    private val downloadMapRepository: MapDownloadRepository
) {
    operator fun invoke(map: MapModel): String? {
        return downloadMapRepository.getGraphPath(map)
    }
}