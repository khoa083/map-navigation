package com.kblack.offlinemap.domain.models

data class GeoCoordinate(
    val latitude: Double,
    val longitude: Double,
    val accuracy: Float = 0f,
    val bearing: Float? = null,
    val bearingAccuracy: Float? = null,
    val altitude: Double? = null,
    val speed: Float? = null,
    val timestamp: Long = System.currentTimeMillis(),
) {
    val isStale: Boolean
        get() = System.currentTimeMillis() - timestamp > 30_000

    val hasGoodAccuracy: Boolean
        get() = accuracy < 50f

    val hasBearing: Boolean
        get() = bearing != null && bearing >= 0f

    val hasBearingAccuracy: Boolean
        get() = bearingAccuracy != null && bearingAccuracy >= 0f
}
