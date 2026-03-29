package com.kblack.offlinemap.domain.usecase.mapdownload

import com.kblack.offlinemap.domain.models.MapModel
import com.kblack.offlinemap.domain.repository.MapDownloadRepository

class CancelDownloadMapUseCase(
    private val downloadMapRepository: MapDownloadRepository
) {
    operator fun invoke(
        map: MapModel
    ) {
        downloadMapRepository.cancelDownloadMap(map)
    }
}