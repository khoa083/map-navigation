package com.kblack.offlinemap.domain.repository

import com.kblack.offlinemap.domain.models.MapDownloadStatus
import com.kblack.offlinemap.domain.models.MapModel
import java.util.UUID

interface MapDownloadRepository {

    fun downloadMap(
        map: MapModel,
        onStatusUpdated: (map: MapModel, status: MapDownloadStatus) -> Unit,
    )

    fun cancelDownloadMap(map: MapModel)

    fun cancelAll(onComplete: () -> Unit)

    fun getStyleJsonPath(map: MapModel): String?
    fun getGraphPath(map: MapModel): String?

}