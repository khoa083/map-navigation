package com.kblack.offlinemap.domain.usecase.routing

import com.kblack.offlinemap.domain.repository.RoutingRepository

class CloseRoutingEngineUseCase(
    private val routingRepository: RoutingRepository
) {
    operator fun invoke() {
        routingRepository.close()
    }
}