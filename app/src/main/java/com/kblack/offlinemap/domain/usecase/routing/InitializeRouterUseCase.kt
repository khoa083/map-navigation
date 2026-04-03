package com.kblack.offlinemap.domain.usecase.routing

import com.kblack.offlinemap.domain.repository.RoutingRepository

class InitializeRouterUseCase(
    private val routingRepository: RoutingRepository
) {
    suspend operator fun invoke(graphDirectoryPath: String) {
        if (routingRepository.isInitialized()) return
        routingRepository.initialize(graphDirectoryPath)
    }
}
