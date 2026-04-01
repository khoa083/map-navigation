package com.kblack.offlinemap.presentation.screen.overview

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kblack.offlinemap.domain.models.GeoCoordinate
import com.kblack.offlinemap.domain.models.MapModel
import com.kblack.offlinemap.presentation.base.BaseContainer
import com.kblack.offlinemap.presentation.screen.overview.component.MapControls
import com.kblack.offlinemap.presentation.screen.overview.component.SelectPointBottomSheet
import com.kblack.offlinemap.presentation.screen.overview.component.UpdateRoutingVehicle
import com.kblack.offlinemap.presentation.ui.Constant.INITIAL_ZOOM
import com.kblack.offlinemap.presentation.ui.Constant.MAX_ZOOM
import com.kblack.offlinemap.presentation.ui.Constant.MIN_ZOOM
import com.kblack.offlinemap.presentation.ui.theme.customColors
import org.maplibre.compose.camera.CameraPosition
import org.maplibre.compose.camera.rememberCameraState
import org.maplibre.compose.expressions.dsl.const
import org.maplibre.compose.expressions.dsl.image
import org.maplibre.compose.expressions.value.LineCap
import org.maplibre.compose.expressions.value.LineJoin
import org.maplibre.compose.layers.CircleLayer
import org.maplibre.compose.layers.LineLayer
import org.maplibre.compose.layers.SymbolLayer
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
import kotlin.time.Duration.Companion.seconds


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapViewScreen(
    map: MapModel,
    mapViewModel: MapViewModel = hiltViewModel(),
) {

    val context = LocalContext.current
    val uiState by mapViewModel.uiState.collectAsStateWithLifecycle()
    val styleJsonPath = remember(map.mapId) { mapViewModel.getStyleJsonPath(map) }
    var showSelectPointSheet by remember { mutableStateOf(false) }
    var point by remember { mutableStateOf<GeoCoordinate?>(null) }
    var zoom by remember { mutableDoubleStateOf(INITIAL_ZOOM) }

    val routePoints = remember(uiState.route) {
        mutableStateListOf<GeoCoordinate>().apply { addAll(uiState.route?.points.orEmpty()) }
    }

    val showEndFlagAndTopBar = uiState.startPoint != null && uiState.endPoint != null
    val selectedTravelMode = uiState.routingOptions.travelMode

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
        hasPermission = hasLocationPermission(context)
    }

    // Call useCurrentLocationAsStart() when permission is granted
    LaunchedEffect(hasPermission) {
        if (hasPermission) {
            mapViewModel.useCurrentLocation()
        }
    }

    val camera =
        rememberCameraState(
            firstPosition =
                CameraPosition(
                    target = Position(latitude = 21.0285, longitude = 105.8542), //Hanoi
                    zoom = INITIAL_ZOOM
                )
        )

    LaunchedEffect(zoom) {
        camera.animateTo(
            finalPosition =
                camera.position.copy(
                    zoom = zoom
                ),
        )
    }

    LaunchedEffect(Unit) {
        mapViewModel.initializeMap(map)
        if (!hasPermission) {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
        mapViewModel.centerOnCurrentLocation.collect { p ->
            camera.animateTo(
                CameraPosition(
                    target = Position(latitude = p.latitude, longitude = p.longitude),
                    zoom = 18.0,
                ),
                duration = 3.seconds
            )

        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.customColors.taskCardBgColor,
    )
    {
        BaseContainer(modifier = Modifier.padding(it)) {
            MaplibreMap(
                cameraState = camera,
                options = MapOptions(
                    ornamentOptions = OrnamentOptions(
                        isLogoEnabled = false,
                        isAttributionEnabled = false,
                        isScaleBarEnabled = false,
                        padding = PaddingValues(top = 64.dp)
                    )
                ),
                baseStyle = BaseStyle.Uri("file://${styleJsonPath}"),
                onMapClick = { p, dp ->
                    Timber.d("Map clicked at: $p , $dp")
                    point = GeoCoordinate(latitude = p.latitude, longitude = p.longitude)

                    //todo test: if both points are already selected, clicking on the map should clear them and start new selection
                    if (!showEndFlagAndTopBar) {
                        showSelectPointSheet = true
                    }

                    ClickResult.Pass
                },
                onMapLoadFailed = { error ->
                    Timber.e("Map failed to load: $error")
                },
                onMapLoadFinished = {
                    Timber.d("Map loaded successfully")
                },
            ) {

                if (routePoints.size >= 2) {
                    val routeGeoJson = remember(routePoints) {
                        val cords = routePoints.map { p ->
                            Point.fromLngLat(p.longitude, p.latitude)
                        }
                        val lineString = LineString.fromLngLats(cords)
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
                        color = const(Color.Blue),
                        width = const(4.dp),
                        opacity = const(0.5f),
                        cap = const(LineCap.Round),
                        join = const(LineJoin.Round),
//                        dasharray = const(listOf(3f, 2f)) //todo: foot
                    )
                }

                if (showEndFlagAndTopBar) {

                    FlagPointLayer(
                        id = "end-point-layer-flag",
                        point = uiState.endPoint!!
                    )
                } else if (uiState.endPoint != null) {
                    CircleLayer(
                        id = "end-point-layer",
                        source = rememberGeoJsonSource(
                            data = GeoJsonData.JsonString(singlePointFeatureJson(uiState.endPoint!!))
                        ),
                        color = const(Color(0xFF0B57D0)),
                        radius = const(8.dp)

                    )
                }
                if (uiState.startPoint != null && uiState.startPoint != uiState.currentLocation) {
                    CircleLayer(
                        id = "start-point-layer",
                        source = rememberGeoJsonSource(
                            data = GeoJsonData.JsonString(singlePointFeatureJson(uiState.startPoint!!))
                        ),
                        color = const(Color(0xFF0B57D0)),
                        radius = const(8.dp)

                    )
                }
                uiState.currentLocation?.let { p ->
                    CirclePointLayer(
                        id = "current-location-layer",
                        point = p,
                        color = Color(0xFF0B57D0)
                    )
                }
            }
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .align(Alignment.Center)
                )
            }


            if (showEndFlagAndTopBar) {
                UpdateRoutingVehicle(
                    selectedTravelMode = selectedTravelMode,
                    onBackClick = { mapViewModel.clearPoints() },
                    onTravelModeChange = { mode ->
                        mapViewModel.updateRoutingOptions(
                            uiState.routingOptions.copy(travelMode = mode)
                        )
                    }
                )
            }


            MapControls(
                zoom,
                onZoomIn = { zoom = (zoom + 1.0).coerceIn(MIN_ZOOM, MAX_ZOOM) },
                onZoomOut = { zoom = (zoom - 1.0).coerceIn(MIN_ZOOM, MAX_ZOOM) },
                onClickLocation = {
                    if (hasPermission) {
                        mapViewModel.useCurrentLocation()
                    } else {
                        permissionLauncher.launch(
                            arrayOf(
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            )
                        )
                    }
                }
            )

            if (showSelectPointSheet && !showEndFlagAndTopBar) {
                SelectPointBottomSheet(
                    point = point,
                    uiState = uiState,
                    onDismissRequest = { showSelectPointSheet = false },
                    onSelectStart = { startP ->
                        mapViewModel.selectStartPoint(startP)
                    },
                    onSelectEnd = { endP ->
                        mapViewModel.selectEndPoint(endP)
                    }
                )
            }
        }
    }
}

//todo FIXME : migrate to LocationPuck
// https://maplibre.org/maplibre-compose/api/lib/maplibre-compose/org.maplibre.compose.location/-location-puck.html
// Like google maps
//┌─────────────────────────────────────────┐
//│ 1. Accuracy Circle (CircleLayer)        │
//│ 2. Shadow (CircleLayer)                 │
//│ 3. Main Dot (CircleLayer)               │
//│ 4. Bearing Indicator (SymbolLayer)      │
//│ 5. Bearing Accuracy (SymbolLayer)       │
//└─────────────────────────────────────────┘
@Composable
private fun CirclePointLayer(
    id: String,
    point: GeoCoordinate,
    color: Color,
) {
    val source = rememberGeoJsonSource(
        data = GeoJsonData.JsonString(singlePointFeatureJson(point))
    )

    CircleLayer(
        id = "$id-accuracy",
        source = source,
        color = const(color),
        opacity = const(0.3f),
        radius = const(14.dp)

    )

    CircleLayer(
        id = id,
        source = source,
        color = const(color),
        radius = const(8.dp)

    )
}

@Composable
private fun FlagPointLayer(
    id: String,
    point: GeoCoordinate,
) {
    val source = rememberGeoJsonSource(
        data = GeoJsonData.JsonString(singlePointFeatureJson(point))
    )

    SymbolLayer(
        id = id,
        source = source,
        iconImage =
            image(
                value = rememberVectorPainter(Icons.Default.Flag),
                size = DpSize(24.dp, 24.dp),
            ),
        iconAllowOverlap = const(true),

        )
}

private fun singlePointFeatureJson(point: GeoCoordinate): String {
    val markerPoint = Point.fromLngLat(point.longitude, point.latitude)
    return Feature.fromGeometry(markerPoint).toJson()
}
