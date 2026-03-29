package com.kblack.offlinemap.presentation.screen.overview

import android.content.Context
import androidx.lifecycle.ViewModel
import com.kblack.offlinemap.domain.models.MapModel
import com.kblack.offlinemap.domain.usecase.mapdownload.GetGraphPathUseCase
import com.kblack.offlinemap.domain.usecase.mapdownload.GetStyleJsonPathUseCase
import com.kblack.offlinemap.domain.usecase.routing.CalculateRouteUseCase
import com.kblack.offlinemap.domain.usecase.routing.CloseRoutingEngineUseCase
import com.kblack.offlinemap.domain.usecase.routing.InitializeRoutingEngineUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val getStyleJsonPathUseCase: GetStyleJsonPathUseCase,
    private val getGraphPathUseCase: GetGraphPathUseCase,
    private val calculateRouteUseCase: CalculateRouteUseCase,
    private val closeRoutingEngineUseCase: CloseRoutingEngineUseCase,
    private val initializeRoutingEngineUseCase: InitializeRoutingEngineUseCase,
): ViewModel() {

    fun getStyleJsonPath(map: MapModel): String? = getStyleJsonPathUseCase(map)
    fun getGraphPath(map: MapModel): String? = getGraphPathUseCase(map)

}