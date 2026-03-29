package com.kblack.offlinemap.domain.repository

import com.kblack.offlinemap.domain.models.GeoCoordinate
import com.kblack.offlinemap.domain.models.Route
import com.kblack.offlinemap.domain.models.RoutingOptions

interface RoutingRepository {
    fun isInitialized(): Boolean
    suspend fun initialize(graphDirectoryPath: String)
    suspend fun calculateRoute(
        from: GeoCoordinate,
        to: GeoCoordinate,
        options: RoutingOptions
    ): Route
    fun close()
}