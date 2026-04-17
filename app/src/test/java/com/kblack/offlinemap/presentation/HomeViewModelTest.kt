package com.kblack.offlinemap.presentation

import com.kblack.offlinemap.domain.models.MapModel
import com.kblack.offlinemap.domain.usecase.mapallowlist.GetMapUrlResponseUseCase
import com.kblack.offlinemap.domain.usecase.mapallowlist.LoadMapAllowlistUseCase
import com.kblack.offlinemap.domain.usecase.mapdownload.CancelAllUseCase
import com.kblack.offlinemap.domain.usecase.mapdownload.CancelDownloadMapUseCase
import com.kblack.offlinemap.domain.usecase.mapdownload.DeleteMapUseCase
import com.kblack.offlinemap.domain.usecase.mapdownload.DownloadMapUseCase
import com.kblack.offlinemap.domain.usecase.mapdownload.GetLocalMapStatusUseCase
import com.kblack.offlinemap.presentation.viewmodel.HomeViewModel
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private val loadMapAllowlistUseCase: LoadMapAllowlistUseCase = mockk()
    private val getMapUrlResponseUseCase: GetMapUrlResponseUseCase = mockk()
    private val getLocalMapStatusUseCase: GetLocalMapStatusUseCase = mockk()
    private val deleteMapUseCase: DeleteMapUseCase = mockk()
    private val downloadMapUseCase: DownloadMapUseCase = mockk()
    private val cancelDownloadMapUseCase: CancelDownloadMapUseCase = mockk()
    private val cancelAllUseCase: CancelAllUseCase = mockk()

    private lateinit var viewModel: HomeViewModel
    private val testDispatcher = StandardTestDispatcher()

    private val mockMap = MapModel(mapId = "vn", name = "Vietnam", url = "https://test/vn.tar.zst")

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = HomeViewModel(
            loadMapAllowlistUseCase,
            getMapUrlResponseUseCase,
            getLocalMapStatusUseCase,
            deleteMapUseCase,
            downloadMapUseCase,
            cancelDownloadMapUseCase,
            cancelAllUseCase,
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

}