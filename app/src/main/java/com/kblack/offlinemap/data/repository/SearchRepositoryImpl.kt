package com.kblack.offlinemap.data.repository

import com.kblack.offlinemap.domain.models.GeoCoordinate
import com.kblack.offlinemap.domain.models.PlaceCandidate
import com.kblack.offlinemap.domain.repository.SearchRepository
// https://nominatim.org/release-docs/latest/api/Search/#examples
// https://photon.komoot.io/
//class SearchRepositoryImpl: SearchRepository {
//    override suspend fun search(
//        query: String,
//        maxResults: Int
//    ): List<PlaceCandidate> {
//
//    }
//
//    override suspend fun reverseGeocode(point: GeoCoordinate): PlaceCandidate? {
//
//    }
//}