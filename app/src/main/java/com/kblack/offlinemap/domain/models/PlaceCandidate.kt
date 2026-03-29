package com.kblack.offlinemap.domain.models

data class PlaceCandidate(
    val title: String,
    val subtitle: String,
    val coordinate: GeoCoordinate
)
