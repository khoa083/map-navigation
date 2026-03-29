package com.kblack.offlinemap.domain.usecase.routing

import com.kblack.offlinemap.domain.models.GeoCoordinate
import com.kblack.offlinemap.domain.models.Route
import com.kblack.offlinemap.domain.models.RoutingOptions
import com.kblack.offlinemap.domain.repository.RoutingRepository

class CalculateRouteUseCase(
    private val routingRepository: RoutingRepository
) {
    suspend operator fun invoke(
        from: GeoCoordinate,
        to: GeoCoordinate,
        options: RoutingOptions
    ): Route {
        return routingRepository.calculateRoute(from, to, options)
    }
}