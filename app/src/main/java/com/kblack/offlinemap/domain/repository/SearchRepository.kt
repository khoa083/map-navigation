package com.kblack.offlinemap.domain.repository

import com.kblack.offlinemap.domain.models.GeoCoordinate
import com.kblack.offlinemap.domain.models.PlaceCandidate

interface SearchRepository {
    suspend fun search(query: String, maxResults: Int): List<PlaceCandidate>
    suspend fun reverseGeocode(point: GeoCoordinate): PlaceCandidate?
}