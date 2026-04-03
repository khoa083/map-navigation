package com.kblack.offlinemap.domain.usecase.mapdownload

import com.kblack.offlinemap.domain.models.MapDownloadStatus
import com.kblack.offlinemap.domain.models.MapModel
import com.kblack.offlinemap.domain.repository.MapDownloadRepository
import java.util.UUID

class ObserverWorkerUseCase(
    private val downloadMapRepository: MapDownloadRepository
) {
    operator fun invoke(
        workerId: UUID,
        map: MapModel,
        onStatusUpdated: (map: MapModel, status: MapDownloadStatus) -> Unit,
    ) {
        downloadMapRepository.observerWorkerProgress(workerId, map, onStatusUpdated)
    }
}