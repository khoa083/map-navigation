package com.kblack.offlinemap.presentation.screen.overview

import android.Manifest
import android.content.Context
import android.content.Context.LOCATION_SERVICE
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.graphhopper.GHRequest
import com.graphhopper.GraphHopper
import com.graphhopper.config.Profile
import com.graphhopper.routing.ev.MaxSpeed
import com.graphhopper.util.Parameters
import com.kblack.offlinemap.domain.models.MapModel
import com.kblack.offlinemap.presentation.base.BaseContainer
import com.kblack.offlinemap.presentation.screen.home.HomeViewModel
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.withContext
import org.maplibre.android.geometry.LatLng
import org.maplibre.compose.camera.CameraPosition
import org.maplibre.compose.camera.rememberCameraState
import org.maplibre.compose.expressions.dsl.const
import org.maplibre.compose.layers.LineLayer
import org.maplibre.compose.map.MapOptions
import org.maplibre.compose.map.MaplibreMap
import org.maplibre.compose.map.OrnamentOptions
import org.maplibre.compose.sources.GeoJsonData
import org.maplibre.compose.sources.rememberGeoJsonSource
import org.maplibre.compose.style.BaseStyle
import org.maplibre.compose.util.ClickResult
import org.maplibre.geojson.Feature
import org.maplibre.geojson.LineString
import org.maplibre.geojson.Point
import org.maplibre.spatialk.geojson.Position
import timber.log.Timber
import java.io.File
import java.util.Locale
import kotlin.collections.copy
import kotlin.time.Duration.Companion.seconds

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapViewScreen(
    map: MapModel,
    mapViewModel: MapViewModel = hiltViewModel(),
) {

    Timber.d("map = $map")

    val styleJsonPath = mapViewModel.getStyleJsonPath(map)

    var hopper: GraphHopper? = null

    var showLoading by remember { mutableStateOf(false) }

    // todo test draw line
    data class GeoCoordinate(
        val latitude: Double,
        val longitude: Double
    )

    val wayPoints: MutableList<GeoCoordinate> = mutableListOf()
    val routePoints = remember { mutableStateListOf<LatLng>() }

    Timber.d("CAPTURE_PATH_MAP_tiles ${mapViewModel.getStyleJsonPath(map)}")
    Timber.d("CAPTURE_PATH_MAP_graph ${mapViewModel.getGraphPath(map)}")

    suspend fun initialize(graphDirectoryPath: String) {
        withContext(IO) {
            val graphDir = File(graphDirectoryPath)
            require(graphDir.exists() && graphDir.isDirectory) {
                "Graph directory not found: $graphDirectoryPath"
            }

            if (hopper != null) return@withContext

            val localHopper = GraphHopper().forMobile()
            localHopper.setProfiles(Profile("car").setVehicle("car").setWeighting("fastest"))
            val loaded = localHopper.load(graphDir.absolutePath)
            if (!loaded) {
                throw IllegalStateException("Cannot load GraphHopper graph from ${graphDir.absolutePath}")
            }
            hopper = localHopper
            Timber.d("[CAPTURE] Load graph success")
            Timber.d("[CAPTURE] Available profiles: ${localHopper.profiles}")

        }
    }

    suspend fun calculateRoute() {
        return withContext(IO) {
            showLoading = true
            val h = hopper ?: throw IllegalStateException("GraphHopper engine is not initialized")

            val request = GHRequest(21.0285, 105.8542, 10.7626, 106.6602)
                .setProfile("car")
                .setAlgorithm(Parameters.Algorithms.DIJKSTRA_BI)
            request.hints.putObject(Parameters.Routing.INSTRUCTIONS, true)

            request.pathDetails.add(MaxSpeed.KEY)
            request.pathDetails.add(Parameters.Details.AVERAGE_SPEED)

            val response = h.route(request)
            if (response == null || response.hasErrors()) {
                val firstError = if (response != null && response.getErrors().isNotEmpty()) {
                    response.getErrors()[0].message
                } else {
                    "Unknown route error"
                }
                throw IllegalStateException(firstError)
            }

            val best = response.best
            val points = best.points
            points.size() - 1

//            Timber.d(
//                "[ROUTE] distance=%.1f km | time=%d min | points=%d",
//                best.distance / 1000.0,
//                best.time / 60000,
//                points.size()
//            )
//            Timber.d(
//                "[ROUTE] start=(%.5f, %.5f) -> end=(%.5f, %.5f)",
//                points.getLat(0), points.getLon(0),
//                points.getLat(last), points.getLon(last)
//            )

            val tr = h.translationMap.getWithFallBack(Locale("vi", "VN"))
            best.instructions.forEachIndexed { i, ins ->
                ins.getTurnDescription(tr)
                ins.distance / 1000.0
                ins.time / 1000
                wayPoints.add(
                    GeoCoordinate(
                        points.getLat(i),
                        points.getLon(i)
                    )
                )
//                Timber.d("${ins.sign} [STEP ${i + 1}] $text | ${"%.2f".format(distKm)} km | ${timeSec}s | lat: ${points.getLat(i)} lon: ${points.getLon(i)}")
            }

//            showLoading = false
            val tmp = mutableListOf<LatLng>()
            for (i in 0 until points.size()) {
                tmp += LatLng(points.getLat(i), points.getLon(i))
            }

            withContext(Main) {
                routePoints.clear()
                routePoints.addAll(tmp)
                showLoading = false
            }


        }
    }

    LaunchedEffect(Unit) {
//        Timber.d("[CAPTURE] initialize: ${initialize(homeViewModel.getGraphPath(map)!!)}")
//        calculateRoute()
    }


    val camera =
        rememberCameraState(
            firstPosition =
                CameraPosition(
                    target = Position(latitude = 10.7626, longitude = 106.6602),
                    zoom = 4.0
                )
        )
    LaunchedEffect(Unit) {
        camera.animateTo(
            finalPosition =
                camera.position.copy(
                    target = Position(latitude = 21.0285, longitude = 105.8542),
                    zoom = 10.0
                ),
            duration = 5.seconds,
        )
    }

    val context = LocalContext.current

    fun hasLocationPermission(ctx: Context): Boolean {
        val fine = ActivityCompat.checkSelfPermission(
            ctx, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val coarse = ActivityCompat.checkSelfPermission(
            ctx, Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        return fine || coarse
    }

    var hasPermission by remember { mutableStateOf(hasLocationPermission(context)) }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        Timber.d("[LOCATION] permission result = $result")
        hasPermission = hasLocationPermission(context) // quan trọng
    }

    LaunchedEffect(Unit) {
        if (!hasPermission) {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    val locationManager = remember(context) {
        context.getSystemService(LOCATION_SERVICE) as LocationManager
    }

    val locationListener = remember {
        object : LocationListener {
            override fun onLocationChanged(location: Location) {
                Timber.d("[LOCATION] p0 $location")
            }

            override fun onProviderEnabled(provider: String) {
                super.onProviderEnabled(provider)
                Timber.d("[LOCATION] Enabled $provider")
            }

            override fun onProviderDisabled(provider: String) {
                super.onProviderDisabled(provider)
                Timber.d("[LOCATION] Enabled $provider")
            }

            @RequiresApi(Build.VERSION_CODES.S)
            override fun onLocationChanged(locations: List<Location?>) {
                super.onLocationChanged(locations)
                Timber.d("[LOCATION] LocationChanged $locations")
            }
        }
    }

    DisposableEffect(hasPermission, locationManager, locationListener) {
        Timber.d("[RECOMPOSE] DisposableEffect, hasPermission = $hasPermission")
        if (!hasPermission) {
            Timber.w("[LOCATION] permission NOT granted")
            onDispose { }
        } else {
            Timber.d("[LOCATION] permission granted, start updates")

            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, 1000L, 0f, locationListener
                )
            }
            if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                locationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, 1000L, 0f, locationListener
                )
            }

            onDispose {
                locationManager.removeUpdates(locationListener)
                Timber.d("[LOCATION] remove updates")
            }
        }
    }





    BaseContainer {
        MaplibreMap(
            cameraState = camera,
            options = MapOptions(
                ornamentOptions = OrnamentOptions(
                    isLogoEnabled = false,
                    isAttributionEnabled = false,
                    isScaleBarEnabled = false
                )
            ),
            baseStyle = BaseStyle.Uri("file://${styleJsonPath!!}"),
            onMapClick = { latLng, dp ->
                Timber.d("Map clicked at: $latLng , $dp")
                ClickResult.Pass
            }
        ) {

            val routeSnapshot = routePoints.toList()

            if (routeSnapshot.size >= 2) {
                val routeGeoJson = remember(routeSnapshot) {
                    val coords = routeSnapshot.map { Point.fromLngLat(it.longitude, it.latitude) }
                    val lineString = LineString.fromLngLats(coords)
                    Feature.fromGeometry(lineString).toJson()
                }

                val routeSource = rememberGeoJsonSource(
                    data = GeoJsonData.JsonString(routeGeoJson)
                )


                LineLayer(
                    id = "route-layer",
                    source = routeSource,
                    minZoom = 0.0f,
                    maxZoom = 24.0f,
                    color = const(Color.Red),
                    width = const(6.dp)
                )
            }
        }
        if (showLoading) {
            CircularProgressIndicator(
                modifier = Modifier
                    .align(Alignment.Center)
                    .background(Color.Red)
            )
        }

        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            SearchBar(
                modifier = Modifier
                    .align(Alignment.TopCenter),
                inputField = {
                    SearchBarDefaults.InputField(
                        query = "",
                        onQueryChange = {  },
                        onSearch = { },
                        expanded = false,
                        onExpandedChange = { it },
                        placeholder = { Text("Search", color = Color.Gray) },
                        colors = SearchBarDefaults.inputFieldColors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            disabledContainerColor = Color.White,
                            cursorColor = Color.Black,
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black
                        )
                    )
                },
                expanded = false,
                onExpandedChange = {  },
            ) {
                // Display search results in a scrollable column
            }
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Column(
                    modifier = Modifier.align(Alignment.BottomEnd),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    IconButton(
                        onClick = { },
                        modifier = Modifier
                            .alpha(0.8f)
                            .width(48.dp)
                            .height(52.dp)
                            .clip(RoundedCornerShape(28.dp, 28.dp, 8.dp, 8.dp))
                            .background(Color.White)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Increment",
                            tint = Color.Black, modifier = Modifier.size(32.dp))
                    }

                    IconButton(
                        onClick = { },
                        modifier = Modifier
                            .alpha(0.8f)
                            .width(48.dp)
                            .height(52.dp)
                            .clip(RoundedCornerShape(8.dp, 8.dp, 28.dp, 28.dp))
                            .background(Color.White)
                    ) {
                        Icon(Icons.Default.Remove, contentDescription = "Decrement",
                            tint = Color.Black, modifier = Modifier.size(32.dp))
                    }

                    Spacer(modifier = Modifier.height(100.dp))

                    IconButton(
                        onClick = { },
                        modifier = Modifier
                            .alpha(0.8f)
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                    ) {
                        Icon(Icons.Filled.MyLocation, contentDescription = "My Location",
                            tint = Color.Black)
                    }
                }
            }

            Spacer(modifier = Modifier.height(50.dp))

            IconButton(
                onClick = { },
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.White)
            ) {
                Icon(Icons.Filled.Navigation,
                    contentDescription = "Navigation",
                    tint = Color.Black,
                    modifier = Modifier.size(32.dp)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
        }


    }
}