package com.kblack.offlinemap.data.repository

import com.graphhopper.util.Instruction
import com.kblack.offlinemap.domain.models.GeoCoordinate
import com.kblack.offlinemap.domain.models.Route
import com.kblack.offlinemap.domain.models.RouteInstruction
import com.kblack.offlinemap.domain.models.TravelMode
import com.kblack.offlinemap.domain.utils.GeoMath

class DirectFallbackRouteBuilder {
    fun build(from: GeoCoordinate, to: GeoCoordinate, travelMode: TravelMode): Route {
        val points = listOf(from, to)
        val distanceMeters = GeoMath.distanceMeters(from, to)

        val speedKmH = when (travelMode) {
            TravelMode.Car -> 50.0
            TravelMode.Motorcycle -> 40.0
            TravelMode.Foot -> 5.5
        }

        val durationMillis = ((distanceMeters / 1000.0) / speedKmH * 3600.0 * 1000.0).toLong()

        val instruction = RouteInstruction(
            sign = Instruction.CONTINUE_ON_STREET,
            name = "direction to target",
            distanceMeters = distanceMeters,
            durationMillis = durationMillis,
            points = points
        )

        return Route(
            distanceMeters = distanceMeters,
            durationMillis = durationMillis,
            points = points,
            instructions = listOf(instruction),
            speedDetails = emptyMap(),
            isDirectFallback = true,
            debugInfo = "direct-fallback"
        )
    }
}