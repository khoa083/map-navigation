package com.kblack.offlinemap.domain.usecase

import com.kblack.offlinemap.domain.models.GeoCoordinate
import com.kblack.offlinemap.domain.models.NavigationSnapshot
import com.kblack.offlinemap.domain.models.Route
import com.kblack.offlinemap.domain.usecase.routing.BuildNavigationUseCase
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.Is.`is`
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class BuildNavUseCaseTest {
    private val buildNavigationUseCase = BuildNavigationUseCase()

    //todo: Example: https://graphhopper.com/maps/?point=21.068013%2C105.804222&point=21.068633%2C105.803836&point=21.068625%2C105.803478&point=21.068954%2C105.803749&point=21.069316%2C105.803304&point=21.0691%2C105.803096&point=21.068615%2C105.804188&point=21.069191%2C105.805154&profile=car&layer=Omniscale
    private val listGeo = listOf(
        GeoCoordinate(latitude = 21.068013, longitude = 105.804222),
        GeoCoordinate(latitude = 21.068625, longitude = 105.803478),
        GeoCoordinate(latitude = 21.068954, longitude = 105.803749),
        GeoCoordinate(latitude = 21.069316, longitude = 105.803304),
        GeoCoordinate(latitude = 21.0691, longitude = 105.803096),
        GeoCoordinate(latitude = 21.068615, longitude = 105.804188),
        GeoCoordinate(latitude = 21.069191, longitude = 105.805154)
    )

    private fun currentLocation(latitude: Double = 21.068633, longitude: Double = 105.803836) : GeoCoordinate {
        return GeoCoordinate(latitude, longitude)
    }

    private fun route(
        distanceMeters: Double = 689.0,
        durationMillis: Long = 120_000L,
        points: List<GeoCoordinate> = listGeo,
    ): Route {
        return Route(
            distanceMeters = distanceMeters,
            durationMillis = durationMillis,
            points = points,
            instructions = emptyList(),
            speedDetails = emptyMap(),
            isDirectFallback = false,
            debugInfo = ""
        )
    }

    private val defaultRoute = NavigationSnapshot(
        nearestPointIndex = -1,
        remainingDistanceMeters = 0.0,
        remainingDurationMillis = 0,
        isOffTrack = true,
        nextInstruction = null)


    @Test
    fun `BuildNavigationUseCase should default value if the route is null`() {
        val emptyRoute = route(points = emptyList())

        val actual = buildNavigationUseCase(emptyRoute, currentLocation())

        assertThat(actual, `is`(defaultRoute))
    }

    //todo: Example: https://graphhopper.com/maps/?point=21.068013%2C105.804222&point=21.068183%2C105.804199&point=21.068625%2C105.803478&point=21.068954%2C105.803749&point=21.069316%2C105.803304&point=21.0691%2C105.803096&point=21.068615%2C105.804188&point=21.069191%2C105.805154&profile=car&layer=Omniscale
    @Test
    fun `BuildNavigationUseCase should isOffTrack = false when location is within default 30m threshold`() {
        val onRoadLocation = currentLocation(latitude = 21.068183, longitude = 105.804199)

        val actual = buildNavigationUseCase(route(), onRoadLocation)

        assertFalse(actual.isOffTrack)
    }

    //todo: Example: https://graphhopper.com/maps/?point=21.068013%2C105.804222&point=21.067487%2C105.803215&point=21.068625%2C105.803478&point=21.068954%2C105.803749&point=21.069316%2C105.803304&point=21.0691%2C105.803096&point=21.068615%2C105.804188&point=21.069191%2C105.805154&profile=car&layer=Omniscale
    @Test
    fun `BuildNavigationUseCase should isOffTrack = true when location is within default 30m threshold`() {

        val farAwayLocation = currentLocation(latitude = 21.067487, longitude = 105.803215)

        val actual = buildNavigationUseCase(route(), farAwayLocation)

        assertTrue(actual.isOffTrack)
    }

    @Test
    fun `BuildNavigationUseCase isOffTrack should be at any distance`() {
        val farAwayLocation = currentLocation(latitude = 21.068183, longitude = 105.804199)

        val actual10m = buildNavigationUseCase(route(), farAwayLocation, offTrack=10.0)
        val actual50m = buildNavigationUseCase(route(), farAwayLocation, offTrack=50.0)

        assertTrue(actual10m.isOffTrack)
        assertFalse(actual50m.isOffTrack)
    }

    @Test
    fun `BuildNavigationUseCase nearestPointIndex = 0 when the location is close to the starting point`() {
        val onRoadLocation = currentLocation(latitude = 21.068183, longitude = 105.804199)

        val actual = buildNavigationUseCase(route(), onRoadLocation)

        assertThat(actual.nearestPointIndex == 0, `is`(true))
        assertThat(actual.nearestPointIndex < listGeo.size, `is`(true))
    }

    @Test
    fun `BuildNavigationUseCase nearestPointIndex = 6 when the location is close to the end point`() {
        val startLocation = GeoCoordinate(latitude = 21.069191, longitude = 105.805154)

        val actual = buildNavigationUseCase(route(), startLocation)

        assertThat(actual.nearestPointIndex == 6, `is`(true))
    }

    //(route.distanceMeters = 0


}