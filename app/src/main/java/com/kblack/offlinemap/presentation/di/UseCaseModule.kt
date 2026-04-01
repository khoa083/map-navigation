package com.kblack.offlinemap.presentation.di

import com.kblack.offlinemap.domain.repository.LocationRepository
import com.kblack.offlinemap.domain.repository.MapDownloadRepository
import com.kblack.offlinemap.domain.repository.RoutingRepository
import com.kblack.offlinemap.domain.usecase.location.GetCurrentLocationUseCase
import com.kblack.offlinemap.domain.usecase.location.ObserveCurrentLocationUseCase
import com.kblack.offlinemap.domain.usecase.routing.CalculateRouteUseCase
import com.kblack.offlinemap.domain.usecase.routing.CloseRoutingEngineUseCase
import com.kblack.offlinemap.domain.usecase.routing.InitializeRoutingEngineUseCase
import com.kblack.offlinemap.domain.usecase.mapdownload.CancelAllUseCase
import com.kblack.offlinemap.domain.usecase.mapdownload.CancelDownloadMapUseCase
import com.kblack.offlinemap.domain.usecase.mapdownload.DownloadMapUseCase
import com.kblack.offlinemap.domain.usecase.mapdownload.GetGraphPathUseCase
import com.kblack.offlinemap.domain.usecase.mapdownload.GetStyleJsonPathUseCase
import com.kblack.offlinemap.domain.usecase.mapdownload.ObserverWorkerProgressUseCase
import com.kblack.offlinemap.domain.usecase.routing.BuildNavigationSnapshotUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
object UseCaseModule {

    @Provides
    @ViewModelScoped
    fun provideCalculateRouteUseCase(
        routingRepository: RoutingRepository
    ): CalculateRouteUseCase {
        return CalculateRouteUseCase(routingRepository)
    }

    @Provides
    @ViewModelScoped
    fun provideCloseRoutingEngineUseCase(
        routingRepository: RoutingRepository
    ): CloseRoutingEngineUseCase {
        return CloseRoutingEngineUseCase(routingRepository)
    }

    @Provides
    @ViewModelScoped
    fun provideInitializeRoutingEngineUseCase(
        routingRepository: RoutingRepository
    ): InitializeRoutingEngineUseCase {
        return InitializeRoutingEngineUseCase(routingRepository)
    }

    @Provides
    @ViewModelScoped
    fun provideCancelAllUseCase(
        downloadMapRepository: MapDownloadRepository
    ): CancelAllUseCase {
        return CancelAllUseCase(downloadMapRepository)
    }

    @Provides
    @ViewModelScoped
    fun provideCancelDownloadMapUseCase(
        downloadMapRepository: MapDownloadRepository
    ): CancelDownloadMapUseCase {
        return CancelDownloadMapUseCase(downloadMapRepository)
    }

    @Provides
    @ViewModelScoped
    fun provideDownloadMapUseCase(
        downloadMapRepository: MapDownloadRepository
    ): DownloadMapUseCase {
        return DownloadMapUseCase(downloadMapRepository)
    }

    @Provides
    @ViewModelScoped
    fun provideObserverWorkerProgressUseCase(
        downloadMapRepository: MapDownloadRepository
    ): ObserverWorkerProgressUseCase {
        return ObserverWorkerProgressUseCase(downloadMapRepository)
    }

    @Provides
    @ViewModelScoped
    fun provideGetStyleJsonPath(
        downloadMapRepository: MapDownloadRepository
    ): GetStyleJsonPathUseCase {
        return GetStyleJsonPathUseCase(downloadMapRepository)
    }

    @Provides
    @ViewModelScoped
    fun provideGetGraphPath(
        downloadMapRepository: MapDownloadRepository
    ): GetGraphPathUseCase {
        return GetGraphPathUseCase(downloadMapRepository)
    }

    @Provides
    @ViewModelScoped
    fun provideGetCurrentLocation(
        locationRepository: LocationRepository
    ): GetCurrentLocationUseCase {
        return GetCurrentLocationUseCase(locationRepository)
    }

    @Provides
    @ViewModelScoped
    fun provideObserveCurrentLocation(
        locationRepository: LocationRepository
    ): ObserveCurrentLocationUseCase {
        return ObserveCurrentLocationUseCase(locationRepository)
    }

    @Provides
    @ViewModelScoped
    fun provideBuildNavigationSnapshotUseCase(): BuildNavigationSnapshotUseCase {
        return BuildNavigationSnapshotUseCase()
    }

}