package com.ghogho.checkers

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.ghogho.checkers.ui.screens.GameScreen
import com.ghogho.checkers.ui.theme.CheckersappTheme
import com.ghogho.checkers.viewmodel.CheckersViewModel

import com.google.android.gms.ads.MobileAds

class MainActivity : ComponentActivity() {
    
    private val viewModel: CheckersViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Initialize the Google Mobile Ads SDK
        MobileAds.initialize(this) {}
        
        enableEdgeToEdge()
        setContent {
            CheckersappTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    GameScreen(
                        viewModel = viewModel,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}