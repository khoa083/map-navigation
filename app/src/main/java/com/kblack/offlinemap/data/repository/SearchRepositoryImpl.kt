package com.kblack.offlinemap.data.repository

import com.kblack.offlinemap.domain.models.GeoCoordinate
import com.kblack.offlinemap.domain.models.PlaceCandidate
import com.kblack.offlinemap.domain.repository.SearchRepository

class SearchRepositoryImpl: SearchRepository {
    override suspend fun search(
        query: String,
        maxResults: Int
    ): List<PlaceCandidate> {
        TODO("Not yet implemented")
    }

    override suspend fun reverseGeocode(point: GeoCoordinate): PlaceCandidate? {
        TODO("Not yet implemented")
    }
}