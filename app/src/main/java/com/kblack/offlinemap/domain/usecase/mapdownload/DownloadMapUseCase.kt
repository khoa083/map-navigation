package com.kblack.offlinemap.domain.usecase.mapdownload

import com.kblack.offlinemap.domain.models.MapDownloadStatus
import com.kblack.offlinemap.domain.models.MapModel
import com.kblack.offlinemap.domain.repository.MapDownloadRepository

class DownloadMapUseCase(
    private val downloadMapRepository: MapDownloadRepository
) {
    operator fun invoke(
        map: MapModel,
        onStatusUpdated: (map: MapModel, status: MapDownloadStatus) -> Unit
    ) {
        downloadMapRepository.downloadMap(map, onStatusUpdated)
    }
}