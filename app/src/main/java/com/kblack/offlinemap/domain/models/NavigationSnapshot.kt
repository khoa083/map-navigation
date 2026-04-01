package com.kblack.offlinemap.domain.models

data class NavigationSnapshot(
    val nearestPointIndex: Int,
    val remainingDistanceMeters: Double,
    val remainingDurationMillis: Long,
    val isOffTrack: Boolean,
    val nextInstruction: RouteInstruction?
)