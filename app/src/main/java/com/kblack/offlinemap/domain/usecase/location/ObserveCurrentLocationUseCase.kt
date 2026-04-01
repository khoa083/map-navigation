package com.kblack.offlinemap.domain.usecase.location

import com.kblack.offlinemap.domain.models.GeoCoordinate
import com.kblack.offlinemap.domain.repository.LocationRepository
import kotlinx.coroutines.flow.Flow

class ObserveCurrentLocationUseCase(
    private val locationRepository: LocationRepository
) {
    operator fun invoke(intervalMs: Long = 1000L): Flow<GeoCoordinate> {
        return locationRepository.observeCurrentLocation(intervalMs)
    }
}