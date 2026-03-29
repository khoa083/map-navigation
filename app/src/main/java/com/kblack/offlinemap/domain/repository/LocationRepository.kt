package com.kblack.offlinemap.domain.repository

import com.kblack.offlinemap.domain.models.GeoCoordinate
import kotlinx.coroutines.flow.Flow

interface LocationRepository {
    suspend fun getCurrentLocation(): GeoCoordinate?
    fun observeCurrentLocation(intervalMs: Long = 1000L): Flow<GeoCoordinate>
}