package com.kblack.offlinemap.domain.usecase.mapdownload

import com.kblack.offlinemap.domain.repository.MapDownloadRepository

class CancelAllUseCase(
    private val downloadMapRepository: MapDownloadRepository
) {
    operator fun invoke(
        onComplete: () -> Unit
    ){
        downloadMapRepository.cancelAll(onComplete)
    }
}