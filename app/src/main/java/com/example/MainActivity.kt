package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.example.data.repository.GameRepositoryImpl
import com.example.domain.usecase.GetGamesUseCase
import com.example.domain.usecase.GetMapScreenUseCase
import com.example.presentation.ui.MapViewerScreen
import com.example.presentation.viewmodel.MapViewModel
import com.example.presentation.viewmodel.MapViewModelFactory
import com.example.ui.theme.MyApplicationTheme

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
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val viewModel: MapViewModel = androidx.lifecycle.viewmodel.compose.viewModel(factory = viewModelFactory)
                    MapViewerScreen(
                        viewModel = viewModel,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}
