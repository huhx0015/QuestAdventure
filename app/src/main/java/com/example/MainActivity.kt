package com.huhx0015.questadventure

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.huhx0015.questadventure.data.repository.GameRepositoryImpl
import com.huhx0015.questadventure.domain.usecase.GetGamesUseCase
import com.huhx0015.questadventure.domain.usecase.GetMapScreenUseCase
import com.huhx0015.questadventure.presentation.ui.MapViewerScreen
import com.huhx0015.questadventure.presentation.viewmodel.MapViewModel
import com.huhx0015.questadventure.presentation.viewmodel.MapViewModelFactory
import com.huhx0015.questadventure.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Construct Clean Architecture dependencies manually for zero boilerplate
        val repository = GameRepositoryImpl()
        val getGamesUseCase = GetGamesUseCase(repository)
        val getMapScreenUseCase = GetMapScreenUseCase(repository)
        val viewModelFactory = MapViewModelFactory(getGamesUseCase, getMapScreenUseCase)

        setContent {
            MyApplicationTheme {
                val viewModel: MapViewModel = androidx.lifecycle.viewmodel.compose.viewModel(factory = viewModelFactory)
                MapViewerScreen(
                    viewModel = viewModel,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}
