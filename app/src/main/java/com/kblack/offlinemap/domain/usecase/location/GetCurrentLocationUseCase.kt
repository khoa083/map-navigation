package com.kblack.offlinemap.domain.usecase.location

import com.kblack.offlinemap.domain.models.GeoCoordinate
import com.kblack.offlinemap.domain.repository.LocationRepository

class GetCurrentLocationUseCase(
    private val locationRepository: LocationRepository
) {
    suspend operator fun invoke(): GeoCoordinate? {
        return locationRepository.getCurrentLocation()
    }
}